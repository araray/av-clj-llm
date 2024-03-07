(ns av-clj-llm.utils
  (:require [clj-http.client :as http]
            [cheshire.core :as json]))

(def ^:dynamic *retry-count* 5)
(def ^:dynamic *retry-delay* 1000)

(defn exponential-backoff [retry-count]
  (Thread/sleep (+ (Math/pow 2 retry-count) (rand-int 1000))))

(defn api-request [url headers body]
  (loop [retry 0]
    (let [response (try
                     (http/post url {:headers headers
                                     :body (json/encode body)
                                     :content-type :json
                                     :accept :json
                                     :socket-timeout 10000
                                     :conn-timeout 10000})
                     (catch Exception e
                       (if (< retry *retry-count*)
                         (do
                           (println (str "Request failed with " (.getMessage e) ", retrying..."))
                           (exponential-backoff retry)
                           ::retry)
                         (throw (ex-info "API request failed after multiple attempts" {:cause e})))))]
      (if (= ::retry response)
        (recur (inc retry))
        response))))

(defn paginate-prompt [prompt max-tokens]
  (let [words (clojure.string/split prompt #"\s+")
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
