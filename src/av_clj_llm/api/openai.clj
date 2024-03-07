(ns av-clj-llm.api.openai
  (:require [av-clj-llm.api.core :refer [ChatAPI get-chat-response]]
            [av-clj-llm.utils :as utils]
            [clj-http.client :as http]
            [cheshire.core :as json]))

(defrecord OpenAIChat [config messages]
  ChatAPI
  (get-chat-response [_ prompt]
    (println "---\nSending prompt:" prompt "\n")
    (let [url "https://api.openai.com/v1/chat/completions"
          headers {"Content-Type" "application/json"
                   "OpenAI-Organization" (:organization config)
                   "Authorization" (str "Bearer " (:api-key config))}
          body {:model (:model-engine config)
                :messages (conj @messages {:role "user" :content prompt})
                :max_tokens (:max-tokens config)
                :temperature (:temperature config)
                :top_p (:top-p config)}
          response (utils/api-request url headers body)
          r (-> response :body (json/decode true) :choices first :message)]
      (swap! messages conj r)
      (:content r)))

  (ask [this prompt]
    (swap! messages conj {:role "user" :content prompt})
    (let [paginated-prompts (utils/paginate-prompt prompt (:max-tokens config))]
      (reduce (fn [responses prompt]
                (conj responses (get-chat-response this prompt)))
              []
              paginated-prompts))))
