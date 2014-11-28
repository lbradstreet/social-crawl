(defproject social-crawl "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :jvm-opts ["-Xmx32g" "-server"] 

  :source-paths ["src/clj"]
  :test-paths ["test/clj"]

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2371" :scope "provided"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [org.clojure/core.match "0.2.2"]
                 [org.clojure/tools.logging  "0.3.0"]
                 [com.cognitect/transit-clj "0.8.259"]
                 [com.cognitect/transit-cljs "0.8.188"]
                 [com.stuartsierra/component "0.2.2"]
                 [twitter-api "0.7.7"]
                 [com.twitter/hbc-core "2.0.0"
                  :exclusions [commons-codec
                               com.google.guava/guava
                               org.apache.httpcomponents/httpclient]]
                 [environ "1.0.0"]
                 [weasel "0.4.2"]
                 [leiningen "2.5.0"]
                 [http-kit "2.1.19"]]

  :plugins [[lein-cljsbuild "1.0.3"]
            [lein-environ "1.0.0"]]

  :main social-crawl.twitter

  :min-lein-version "2.5.0"

  :uberjar-name "social-crawl.jar"

  :profiles {:dev {:repl-options {:init-ns social-crawl.twitter}
                   :env {:is-dev true}}
             :uberjar {:env {:production true}
                       :omit-source true
                       :aot :all}})
