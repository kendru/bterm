(ns bterm.core
  (:require [bterm.measure :as measure]
            [bterm.term :as term]
            [clojure.string :as str]
            [goog.events :as gevents]))

(defn get-id []
  (str (gensym "bterm_core_")))

(def style-xforms
  {:font identity
   :white-space identity
   :font-size #(str % "px")})

(def default-display
  {:font "monospace"
   :white-space "pre"
   :font-size 12
   :prompt "bterm# "
   :colors {:background "#333"
            :output "#eee"
            :prompt "#ddd"
            :input "#fff"}})

(defn prop-name
  "Convert a cljs keyword to a camel-cased JavaScript string"
  [kw]
  (let [s (name kw)]
    (str/replace s #"\-(\w)"
                 (fn [[_ c]] (.toUpperCase (str c))))))

(defn append-str [buffer {:keys [cols]} in]
  (loop [tb (transient (into [] (or (butlast buffer) [])))
         line (or (last buffer) "")
         chars in]
    (if (> (count chars) 0)
      (let [[c & cs] chars]
        (if (= cols (count line))
          (recur (conj! tb line) c cs)
          (if (= c "\n")
            (recur (conj! tb line) "" cs)
            (recur tb (str line c) cs))))
      (persistent! (conj! tb line)))))

;; TODO: Optimize by adding and removing lines as necessary
(defn render-lines [terminal lines]
  (let [container (get-in terminal [:elem-cache :output])]
    (aset container "innerHTML" "") ; Clear any previous contents
    (doseq [line lines]
      (let [elem (.createElement js/document "div")]
        (aset elem "innerText" line)
        (aset elem "id" (get-id))
        (.appendChild container elem)))))

(defn render-prompt [terminal]
  (let [elem (get-in terminal [:elem-cache :prompt])]
    (aset elem "innerHTML" (get-in terminal [:display :prompt]))))

(defn render [terminal]
  (let [{:keys [node dims buffer]} terminal
        {:keys [rows]} dims
        lines (take-last (- rows 2) @buffer)]
    (render-lines terminal lines)
    (render-prompt terminal)))

(defn insert-stylesheet
  "Takes a sequence of rules and inserts them as a new stylesheet"
  [rules]
  (let [style (js/document.createElement "style")]
    (.setAttribute style "type" "text/css")
    (.appendChild (.-head js/document) style)
    (doseq [rule rules]
      (.insertRule (.-sheet style) rule))))


;; TODO: Simplify and refactor
(defn init-dom [terminal output prompt input]
  (let [{:keys [node display dims]} terminal
        {:keys [colors font font-size]} display
        {:keys [cols char-width]} dims
        input-width (* char-width (- cols (count (:prompt display))))
        [node-id output-id prompt-id input-id] (repeatedly 4 get-id)]
    (insert-stylesheet
      [(str "#" node-id "{background:" (:background colors) ";}")
       (str "#" output-id "{color:" (:output colors) ";}")
       (str "#" prompt-id "{color:" (:prompt colors) ";}")
       (str "#" input-id "{"
            "color:" (:input colors) ";"
            "background:" (:background colors) ";"
            "border:none;"
            "font-family:" font ";"
            "font-size:" font-size "px;"
            "width:" input-width "px;"
            "}")
       (str "#" input-id ":focus{outline:none;}")])
    (aset node "id" node-id)
    (aset output "id" output-id)
    (aset prompt "id" prompt-id)
    (aset input "id" input-id)
    (.appendChild node output)
    (.appendChild node prompt)
    (.appendChild node input)
    (gevents/listen node gevents/EventType.CLICK #(.focus input))))

(defrecord Terminal [node display buffer history dims elem-cache on-read]
  term/ITerminal
  (term/read [this cb]
    (reset! on-read cb))

  (term/write [this str]
    (swap! buffer append-str dims str)
    (render this))

  (term/clear [this]
    (reset! buffer [])
    (render this)))

(defn attach
  ([node] (attach node default-display))
  ([node display]
   (let [display (merge default-display display)
         {:keys [font font-size]} display
          style-keys (select-keys display (keys style-xforms))
          output (.createElement js/document "div")
          prompt (.createElement js/document "span")
          input (.createElement js/document "input")]
      ; Set style properties on element
      (doseq [[key val] style-keys
              :let [xform (get style-xforms key)]]
        (aset node "style" (prop-name key) (xform val)))
      (let [on-read (atom nil)
            term (map->Terminal {:node node
                                 :display display
                                 :dims (measure/get-text-dims node font font-size)
                                 :buffer (atom [])
                                 :history (atom [])
                                 :on-read on-read
                                 :elem-cache {:output output
                                              :prompt prompt
                                              :input input}})]
        (init-dom term output prompt input)
        (gevents/listen input
                        gevents/EventType.KEYUP
                        (fn [e]
                          (when (= 13 (.-keyCode e))
                            (let [val (aget e "target" "value")]
                              (aset input "value" "")
                              (when-let [cb @on-read]
                                (reset! on-read nil)
                                (cb val))))))
        term))))

