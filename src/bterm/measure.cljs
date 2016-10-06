(ns bterm.measure)

(defn- measure-char [font px]
  (let [font (str px "px " font)
        canvas (.createElement js/document "canvas")
        ctx (.getContext canvas "2d")]
    (set! (.-font ctx) font)
    (.-width (.measureText ctx "m"))))

(defn- line-height [elem font font-size]
  (let [tmp-el (.createElement js/document (.-nodeName elem))
        style (str "margin:0px;padding:0px;white-space:pre"
                   ";font-family:" font
                   ";font-size:" font-size "px")]
    (.setAttribute tmp-el "style" style)
    (let [parent (aget elem "parentNode")
          tmp-el (.appendChild parent tmp-el)]
      (aset tmp-el "innerHTML" "test")
      (let [height (.-clientHeight tmp-el)]
        (.removeChild parent tmp-el)
        (dec height)))))

(defn get-text-dims [elem font font-size]
  (let [width (measure-char font font-size)
        height (line-height elem font font-size)]
    {:cols (js/Math.floor (/ (.-offsetWidth elem) width))
     :rows (js/Math.floor (/ (.-offsetHeight elem) height))
     :char-width width
     :line-height height}))
