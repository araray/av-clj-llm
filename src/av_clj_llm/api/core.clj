(ns av-clj-llm.api.core)

(defprotocol ChatAPI
  (get-chat-response [this prompt])
  (ask [this prompt]))
