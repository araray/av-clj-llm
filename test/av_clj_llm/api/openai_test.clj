(ns av-clj-llm.api.openai-test
  (:require [clojure.test :refer [deftest testing is]]
            [av-clj-llm.api.openai :as openai]))

(deftest openai-chat-test
  (let [config {:api-key "test-api-key"
                :model-engine "gpt-3.5-turbo"
                :max-tokens 10
                :temperature 0.5
                :top-p 1.0}
        chat-api (openai/->OpenAIChat config (atom []))]
    (testing "get-chat-response"
      (with-redefs [openai/api-request (constantly {:body "{\"choices\": [{\"message\": {\"content\": \"Test response\"}}]}"})]
        (is (= "Test response" (openai/get-chat-response chat-api "Hello")))))
    (testing "ask"
      (with-redefs [openai/api-request (constantly {:body "{\"choices\": [{\"message\": {\"content\": \"Test response\"}}]}"})]
        (is (= ["Test response"] (openai/ask chat-api "Hello")))))))
