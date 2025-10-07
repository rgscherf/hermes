(defproject hermes "0.1.1"
  :description "An MCP server for evaluating results in the current nREPL"
  :url "https://github.com/rgscherf/hermes"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [nrepl/nrepl "1.1.1"]
                 [org.clojure/data.json "2.4.0"]]
  :source-paths ["src"]
  :main hermes.core
  :aot [hermes.core]
  :uberjar-name "hermes-standalone.jar"
  :repl-options {:init-ns hermes.core})
