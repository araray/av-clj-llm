(ns av-clj-llm.api.core-test
  (:require [clojure.test :refer [deftest testing is]]
            [av-clj-llm.api.core :as core]))

(defrecord TestChat [config messages]
  core/ChatAPI
  (get-chat-response [_ prompt]
    (str "Test response for: " prompt))
  (ask [this prompt]
    [(get-chat-response this prompt)]))

(deftest chat-api-test
  (let [chat-api (->TestChat {} (atom []))]
    (testing "get-chat-response"
      (is (= "Test response for: Hello" (core/get-chat-response chat-api "Hello"))))
    (testing "ask"
      (is (= ["Test response for: Hello"] (core/ask chat-api "Hello"))))))
