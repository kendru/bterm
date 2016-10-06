(ns bterm.io
  (:refer-clojure :exclude [print println])
  (:require [bterm.term :as t]
            [goog.string :as gstring]
            [goog.string.format]))

(defn print [term & args]
  (t/write term (apply gstring/format args)))

(defn println [term s & args]
  (let [all-args (conj args (str s "\n"))]
    (apply print term all-args)))

(def read t/read)
(def clear t/clear)
