(defproject av-clj-llm "0.2.0-RC1"
  :description "A Clojure chatbot using different AI APIs"
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [clj-http "3.12.3"]
                 [cheshire "5.12.0"]
                 [org.clojure/core.async "1.6.673"]]
  :main ^:skip-aot av-clj-llm.main
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}}
  :dev {:dependencies [[org.clojure/test.check "1.1.0"]]})