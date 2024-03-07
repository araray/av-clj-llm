(ns av-clj-llm.utils-test
  (:require [clojure.test :refer [deftest testing is]]
            [av-clj-llm.utils :as utils]))

(deftest paginate-prompt-test
  (testing "paginate-prompt"
    (is (= ["This is a test" "prompt"]
           (utils/paginate-prompt "This is a test prompt" 10)))))
