(ns av-clj-llm.main
  (:gen-class)
  (:require [av-clj-llm.api.core :as api]
            [av-clj-llm.api.openai :as openai]
            [av-clj-llm.api.anthropic :as anthropic]
            [clojure.edn :as edn]
            [clojure.string :as string]
            [clojure.core.async :as async]))

(defn load-config []
  (edn/read-string (slurp "resources/config.edn")))

(defn create-chat-api [config api-type]
  (case api-type
    :openai (openai/->OpenAIChat (get config :openai) (atom []) (atom false))
    :anthropic (anthropic/->AnthropicChat (get config :anthropic) (atom []) (atom false))))

(defn chat-loop [chat-api]
  (loop []
    (print "User: ")
    (flush)
    (let [input (read-line)]
      (when-not (= (string/lower-case input) "quit")
        (let [ch (async/chan)]
          (api/ask chat-api input ch)
          (loop []
            (when-some [response (async/<!! ch)]
              (cond
                (= ::api/stream-end response) (println)
                (string? response) (print response)
                :else (println "Error:" response))
              (println "")
              (flush)
              (recur))))
        (recur)))))

(defn -main [& args]
  (let [config (load-config)
        api-type (keyword (first args))
        streaming (= "true" (second args))
        chat-api (create-chat-api config api-type)]
    (api/set-streaming! chat-api streaming)
    (println "Starting chat with" (name api-type) "API...")
    (chat-loop chat-api)))