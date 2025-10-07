(ns hermes.core
  (:require [clojure.data.json :as json]
            [nrepl.core :as nrepl]
            [clojure.java.io :as io]
            [clojure.string :as str])
  (:gen-class))

(def nrepl-conn (atom nil))
(def nrepl-client (atom nil))

(defn find-nrepl-port
  "Searches for .nrepl-port file starting from given directory up to root"
  [start-dir]
  (loop [dir (io/file start-dir)]
    (when dir
      (let [port-file (io/file dir ".nrepl-port")]
        (if (.exists port-file)
          {:port (Integer/parseInt (str/trim (slurp port-file)))
           :project-dir (.getAbsolutePath dir)}
          (recur (.getParentFile dir)))))))

(defn read-message []
  (when-let [line (.readLine (io/reader *in*))]
    (json/read-str line :key-fn keyword)))

(defn write-message [msg]
  (println (json/write-str msg))
  (flush))

(defn eval-clojure [code]
  (if-let [client @nrepl-client]
    (try
      (let [responses (nrepl/message client {:op "eval" :code code})
            results (doall responses)
            output (apply str (keep :out results))
            value (some :value results)
            error (some :err results)]
        {:success true
         :value value
         :output output
         :error error})
      (catch Exception e
        {:success false
         :error (.getMessage e)}))
    {:success false
     :error "Not connected to nREPL"}))

(defn handle-initialize [id]
  (write-message
   {:jsonrpc "2.0"
    :id id
    :result {:protocolVersion "2024-11-05"
             :capabilities {:tools {}}
             :serverInfo {:name "clojure-nrepl-mcp"
                         :version "0.1.0"}}}))

(defn handle-list-tools [id]
  (write-message
   {:jsonrpc "2.0"
    :id id
    :result {:tools [{:name "eval_clojure"
                      :description "**PRIMARY TOOL FOR CLOJURE DEVELOPMENT** - Evaluate Clojure code in the connected nREPL. This connects to the user's active editor REPL session, sharing the same state, loaded namespaces, and definitions. USE THIS TOOL FREQUENTLY to explore the codebase, test assumptions, verify function behavior, and experiment with ideas BEFORE writing code. Examples: Check loaded namespaces with (all-ns), explore namespace contents with (dir namespace.name), test functions, look up docs with (doc fn-name)."
                      :inputSchema {:type "object"
                                    :properties {:code {:type "string"
                                                        :description "Clojure code to evaluate"}}
                                    :required ["code"]}}]}}))

(defn handle-call-tool [id tool-name arguments]
  (if (= tool-name "eval_clojure")
    (let [result (eval-clojure (:code arguments))]
      (write-message
       {:jsonrpc "2.0"
        :id id
        :result {:content [{:type "text"
                           :text (str (when (:value result)
                                       (str "=> " (:value result) "\n"))
                                    (when (and (:output result)
                                             (not (str/blank? (:output result))))
                                      (str (:output result) "\n"))
                                    (when (:error result)
                                      (str "Error: " (:error result))))}]}}))
    (write-message
     {:jsonrpc "2.0"
      :id id
      :error {:code -32601
              :message "Tool not found"}})))

(defn handle-message [msg]
  (let [{:keys [id method params]} msg]
    (case method
      "initialize" (handle-initialize id)
      "tools/list" (handle-list-tools id)
      "tools/call" (handle-call-tool id (:name params) (:arguments params))
      (write-message
       {:jsonrpc "2.0"
        :id id
        :error {:code -32601
                :message "Method not found"}}))))

(defn connect-to-nrepl [work-dir]
  (if-let [{:keys [port project-dir]} (find-nrepl-port work-dir)]
    (try
      (let [conn (nrepl/connect :host "localhost" :port port)
            client (nrepl/client conn 1000)]
        (reset! nrepl-conn conn)
        (reset! nrepl-client client)
        (binding [*out* *err*]
          (println (str "Connected to nREPL at localhost:" port))
          (println (str "Project directory: " project-dir))))
      (catch Exception e
        (binding [*out* *err*]
          (println "Failed to connect to nREPL:" (.getMessage e)))))
    (binding [*out* *err*]
      (println "Could not find .nrepl-port file. Make sure your editor's REPL is running."))))

(defn -main [& args]
  (let [work-dir (or (first args)
                     (System/getenv "CLAUDE_CODE_CWD")
                     (System/getProperty "user.dir"))]
    (binding [*out* *err*]
      (println "MCP Server starting...")
      (println "Looking for .nrepl-port from:" work-dir))

    (connect-to-nrepl work-dir)

    (loop []
      (when-let [msg (read-message)]
        (handle-message msg)
        (recur)))))

(comment
  (connect-to-nrepl ".")
  (eval-clojure "(+ 2 2)")
	(def hello "world")
  )