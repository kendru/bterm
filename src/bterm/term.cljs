(ns bterm.term)

(defprotocol ITerminal
  (read [this on-read])
  (write [this str])
  (clear [this]))
