(ns av-clj-llm.utils
  (:require [cheshire.core :as json]
            [clj-http.client :as http]
            [clojure.core.async :as async]
            [clojure.java.io :as io]
            [clojure.string :as string]))

(def ^:dynamic *retry-count* 5)
(def ^:dynamic *retry-delay* 1000)

(defn exponential-backoff [retry-count]
  (Thread/sleep (+ (Math/pow 2 retry-count) (rand-int 1000))))

(def ^:dynamic *max-concurrent-requests* 3)
(def request-semaphore (java.util.concurrent.Semaphore. *max-concurrent-requests*))

(defn api-request [url headers body streaming?]
  (try
    (.acquire request-semaphore)
    (loop [retry 0]
      (let [response (try
                       (println "Making API request...")
                       (http/post url (cond-> {:headers headers
                                               :body (json/encode body)
                                               :content-type :json
                                               :accept :json
                                               :socket-timeout 10000
                                               :conn-timeout 10000}
                                        streaming? (assoc :as :stream)))
                       (catch Exception e
                         (if (< retry *retry-count*)
                           (do
                             (println (str "Request failed with " (.getMessage e) ", retrying..."))
                             (when (instance? clojure.lang.ExceptionInfo e)
                               (println "Response body:" (-> e ex-data :body)))
                             (exponential-backoff retry)
                             ::retry)
                           (throw (ex-info "API request failed after multiple attempts" {:cause e})))))]
        (cond
          (= ::retry response)
          (do
            (println "Retrying API request...")
            (recur (inc retry)))

          (= 429 (:status response))
          (do
            (println "Rate limit exceeded. Waiting for 1 minute before retrying...")
            (Thread/sleep 60000)
            (recur retry))

          :else
          (do
            (println "API request successful")
            response))))
    (finally
      (.release request-semaphore))))

(defn stream-handler [response]
  (if-let [body (:body response)]
    (with-open [reader (io/reader body)]
      (doseq [line (line-seq reader)]
        (cond
          (string/starts-with? line "data: ")
          (let [data (subs line 6)]
            (if (= data "[DONE]")
              ::stream-end
              (json/decode data true)))
          (string/blank? line) nil
          :else (println "Unexpected line:" line))))
    (println "Empty response body")))

(defn paginate-prompt [prompt max-tokens]
  (let [words (string/split prompt #"\s+")
        max-chars-per-page (* max-tokens 4)
        reserved-chars 21]
    (reduce (fn [acc word]
              (let [current-prompt (last acc)
                    word-length (inc (count word))]
                (if (< (+ (count current-prompt) word-length reserved-chars) max-chars-per-page)
                  (update acc (dec (count acc)) str " " word)
                  (conj acc word))))
            [""]
            words)))