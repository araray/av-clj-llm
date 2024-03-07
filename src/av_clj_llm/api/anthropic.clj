(ns av-clj-llm.api.anthropic
  (:require [av-clj-llm.api.core :refer [ChatAPI get-chat-response]]
            [av-clj-llm.utils :as utils]
            [clj-http.client :as http]
            [cheshire.core :as json]))

(defrecord AnthropicChat [config messages]
  ChatAPI
  (get-chat-response [_ prompt]
    (println "---\nSending prompt:" prompt "\n")
    (let [url "https://api.anthropic.com/v1/complete"
          headers {"Content-Type" "application/json"
                   "X-API-Key" (:api-key config)}
          body {:prompt (str prompt "\n\nAssistant:")
                :model (:model-engine config)
                :max_tokens_to_sample (:max-tokens config)
                :temperature (:temperature config)
                :top_p (:top-p config)}
          response (utils/api-request url headers body)
          r (-> response :body (json/decode true) :completion)]
      (swap! messages conj {:role "assistant" :content r})
      r))

  (ask [this prompt]
    (swap! messages conj {:role "user" :content prompt})
    (let [paginated-prompts (utils/paginate-prompt prompt (:max-tokens config))]
      (reduce (fn [responses prompt]
                (conj responses (get-chat-response this prompt)))
              []
              paginated-prompts))))
