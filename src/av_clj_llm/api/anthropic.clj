(ns av-clj-llm.api.anthropic
  (:require [av-clj-llm.api.core :refer [ChatAPI get-chat-response stream-end]]
            [av-clj-llm.utils :as utils]
            [cheshire.core :as json]
            [clojure.core.async :as async]))

(defrecord AnthropicChat [config messages streaming?]
  ChatAPI
  (get-chat-response [this prompt ch]
    ;;  (println "---\nSending prompt:" prompt "\n")
    (let [url (:model-endpoint config)
          headers {"content-type" "application/json"
                   "x-api-key" (:api-key config)
                   "anthropic-version" (:anthropic-version config)}
          body {:messages [{:role "user" :content prompt}]
                :model (:model-engine config)
                :max_tokens (:max-tokens config)
                :stream @streaming?}]

      ;;  (println "URL:" url)
      ;;  (println "Headers:" headers)
      ;;  (println "Body:" body)

      (async/go
        (try
          (let [response (utils/api-request url headers body @streaming?)]
            (println "Processing API response...")
            ;;  (println "Full API response:" response)
            (if @streaming?
              (async/>! ch (utils/stream-handler response))
              (let [content (-> response :body (json/decode true) :content)]
                (if (seq content)
                  (let [r (-> content first :text)]
                    (println "Received response:" r)
                    (swap! messages conj {:role "assistant" :content r})
                    (async/>! ch r))
                  (do
                    (println "Unexpected response structure:")
                    (println response)
                    (async/>! ch "Error: Unexpected response structure"))))))
          (catch Exception e
            (println "Error processing API response:" e)
            (async/>! ch (ex-info "API request failed" {:cause e})))))))


  (ask [this prompt ch]
    (swap! messages conj {:role "user" :content prompt})
    (let [paginated-prompts (utils/paginate-prompt prompt (:max-tokens config))]
      (async/go
        (doseq [prompt paginated-prompts]
          (get-chat-response this prompt ch))
        (async/close! ch))))

  (set-streaming! [this streaming]
    (->AnthropicChat (:config this) (:messages this) (atom streaming))))