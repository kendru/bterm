(defproject bterm "0.1.0"
  :description "A simple terminal emulator widget"
  :url "https://github.com/kendru/bterm"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.7.1"

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.229"]
                 [org.clojure/core.async "0.2.391"
                  :exclusions [org.clojure/tools.reader]]]

  :source-paths ["src"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :cljsbuild {:builds
              [{:id "dev"
                :source-paths ["src"]
                :figwheel {:on-jsload "bterm.core/on-js-reload"
                           :open-urls ["http://localhost:3449/index.html"]}

                :compiler {:main bterm.demo
                           :asset-path "js/compiled/out"
                           :output-to "resources/public/js/compiled/bterm.js"
                           :output-dir "resources/public/js/compiled/out"
                           :source-map-timestamp true
                           :preloads [devtools.preload]}}

               {:id "min"
                :source-paths ["src"]
                :compiler {:output-to "resources/public/js/compiled/bterm.js"
                           :main bterm.core
                           :optimizations :advanced
                           :pretty-print false}}]}

  :figwheel {:css-dirs ["resources/public/css"]}

  :profiles {:dev {:dependencies [[binaryage/devtools "0.8.2"]
                                  [figwheel-sidecar "0.5.8"]
                                  [com.cemerick/piggieback "0.2.1"]]
                   :plugins [[lein-figwheel "0.5.8"]
                             [lein-cljsbuild "1.1.4" :exclusions [[org.clojure/clojure]]]]

                   :source-paths ["src" "dev"]
                    :repl-options {; for nREPL dev you really need to limit output
                                   :init (set! *print-length* 50)
                                   :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}}})
