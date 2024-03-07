(ns av-clj-llm.main
  (:gen-class)
  (:require [av-clj-llm.api.core :as api]
            [av-clj-llm.api.openai :as openai]
            [av-clj-llm.api.anthropic :as anthropic]
            [clojure.edn :as edn]))

(defn load-config []
  (edn/read-string (slurp "resources/config.edn")))

(defn create-chat-api [config api-type]
  (case api-type
    :openai (openai/->OpenAIChat (get config :openai) (atom []))
    :anthropic (anthropic/->AnthropicChat (get config :anthropic) (atom []))))

(defn chat-loop [chat-api]
  (loop []
    (print "User: ")
    (flush)
    (let [input (read-line)]
      (when-not (= (clojure.string/lower-case input) "quit")
        (doseq [response (api/ask chat-api input)]
          (println "Assistant:" response))
        (recur)))))

(defn -main [& args]
  (let [config (load-config)
        api-type (keyword (first args))
        chat-api (create-chat-api config api-type)]
    (println "Starting chat with" (name api-type) "API...")
    (chat-loop chat-api)))
