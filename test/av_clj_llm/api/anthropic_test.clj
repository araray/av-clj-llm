(ns av-clj-llm.api.anthropic-test
  (:require [clojure.test :refer [deftest testing is]]
            [av-clj-llm.api.anthropic :as anthropic]))

(deftest anthropic-chat-test
  (let [config {:api-key "test-api-key"
                :model-engine "claude-v1"
                :max-tokens 10
                :temperature 0.5
                :top-p 1.0}
        chat-api (anthropic/->AnthropicChat config (atom []))]
    (testing "get-chat-response"
      (with-redefs [anthropic/api-request (constantly {:body "{\"completion\": \"Test response\"}"})]
        (is (= "Test response" (anthropic/get-chat-response chat-api "Hello")))))
    (testing "ask"
      (with-redefs [anthropic/api-request (constantly {:body "{\"completion\": \"Test response\"}"})]
        (is (= ["Test response"] (anthropic/ask chat-api "Hello")))))))
