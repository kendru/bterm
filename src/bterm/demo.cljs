(ns bterm.demo
  (:require [bterm.core :as bterm]
            [bterm.io :as io]))

(enable-console-print!)

(defonce initialized (atom false))

(when-not @initialized
  (let [term (bterm/attach (.getElementById js/document "terminal")
                           {:font-size 14
                            :prompt "js=> "})]

    (letfn [(guessing-game [num]
              (io/println term "Please enter a number")
              (io/read term (fn [guess]
                              (let [guess (js/parseInt guess)]
                                (io/clear term)
                                (io/println term "You entered %d" guess)
                                (cond
                                  (= guess num) (io/println term "You guessed it!")
                                  (> guess num) (do (io/println term "Too high. Try again")
                                                    (guessing-game num))
                                  :else (do (io/println term "Too low. Try again")
                                            (guessing-game num)))))))]
      (guessing-game (-> (js/Math.random)
                         (* 10)
                         (js/Math.ceil)))
      (reset! initialized true))))


