(ns av-clj-llm.api.core)

(def ^:const stream-end ::stream-end)

(defprotocol ChatAPI
  (get-chat-response [this prompt ch])
  (ask [this prompt ch])
  (set-streaming! [this streaming]))