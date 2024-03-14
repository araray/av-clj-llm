(ns av-clj-llm.api.openai
  (:require [av-clj-llm.api.core :refer [ChatAPI get-chat-response]]
            [av-clj-llm.utils :as utils]
            [cheshire.core :as json]
            [clojure.core.async :as async]))

(defrecord OpenAIChat [config messages streaming?]
  ChatAPI
  (get-chat-response [this prompt ch]
    (println "---\nSending prompt:" prompt "\n")
    (let [url (:model-endpoint config)
          headers {"Content-Type" "application/json"
                   "OpenAI-Organization" (:organization config)
                   "Authorization" (str "Bearer " (:api-key config))}
          body {:model (:model-engine config)
                :messages (conj @messages {:role "user" :content prompt})
                :max_tokens (:max-tokens config)
                :temperature (:temperature config)
                :top_p (:top-p config)
                :stream @streaming?}
          response (utils/api-request url headers body @streaming?)]
      (if @streaming?
        (async/go
          (async/>! ch (utils/stream-handler response)))
        (let [r (-> response :body (json/decode true) :choices first :message)]
          (swap! messages conj r)
          (async/put! ch (:content r))))))

  (ask [this prompt ch]
    (swap! messages conj {:role "user" :content prompt})
    (let [paginated-prompts (utils/paginate-prompt prompt (:max-tokens config))]
      (async/go
        (doseq [prompt paginated-prompts]
          (get-chat-response this prompt ch))
        (async/close! ch))))

  (set-streaming! [this streaming]
    (->OpenAIChat (:config this) (:messages this) (atom streaming))))