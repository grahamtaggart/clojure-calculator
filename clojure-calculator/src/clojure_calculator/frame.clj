(ns clojure-calculator.frame
  (:gen-class)
  (:require [seesaw.core :as s]
            [clojure.string :as str]
            [seesaw.color :as color]
            ))
(import javax.swing.border.LineBorder)
(import '[javax.swing JButton])
(import '[java.awt Color])
(import java.awt.Color)
(use 'seesaw.core)
(use 'seesaw.font)

(native!)
(def f (s/frame :title "Clojure Calculator App" :width 500 :height 600 :minimum-size [400 :by 500] :icon (s/icon "calculator icon.png")))
(defn display [content]
  "Common display function that returns elements within frame, updates it."
  (config! f :content content)
  (show! f)
  content)


(def current-value (atom "0"))

(def text-panel (s/text :multi-line? false
                        :editable? true
                        :font "Arial-24"
                        :text @current-value
                        :background :lightgray
                        :foreground :black
                        :halign :right
                        ))
(defn update-text-panel [value]
  (let [new-value (str @current-value value)]
    (reset! current-value new-value)
    (s/config! text-panel :text new-value)))
(def plus (s/button :text "+"
                    :background :lightgray
                    :foreground :black
                    :listen [:action (fn [_] (update-text-panel "+"))]))

(def minus (s/button :text "-"
                     :background :lightgray
                     :foreground :black
                     :listen [:action (fn [_] (update-text-panel "-"))]))

(def multiply (s/button :text "*"
                        :background :lightgray
                        :foreground :black
                        :listen [:action (fn [_] (update-text-panel "*"))]))

(def divide (s/button :text "/"
                      :background :lightgray
                      :foreground :black
                      :listen [:action (fn [_] (update-text-panel "/"))]))
(def clear-line (s/button :text "C"
                          :background :lightgray
                          :foreground :black
                          :listen [:action (fn [_] (do (reset! current-value "")
                                                       (s/config! text-panel :text "")))]))

(def clear-everything (s/button :text "CE"
                                :background :lightgray
                                :foreground :black
                                :listen [:action (fn [_] (do (reset! current-value "")
                                                             (s/config! text-panel :text "")))]))

(def decimals (s/button :text "."
                        :background :lightgray
                        :foreground :black
                        :listen [:action (fn [_] (update-text-panel "."))]))

(def negative-positive (s/button :text "+/-"
                                 :background :lightgray
                                 :foreground :black
                                 :listen [:action (fn [_] (let [new-value (if (str/starts-with? @current-value "-")
                                                                            (subs @current-value 1)
                                                                            (str "-" @current-value))]
                                                            (reset! current-value new-value)
                                                            (s/config! text-panel :text new-value)))]))

(def delete (s/button :text "DEL"
                      :background :lightgray
                      :foreground :black
                      :listen [:action (fn [_] (let [new-value (if (> (count @current-value) 1)
                                                                 (subs @current-value 0 (dec (count @current-value)))
                                                                 "")]
                                                 (reset! current-value new-value)
                                                 (s/config! text-panel :text new-value)))]))
(defn string-swap [input]
  (let [tokens (re-seq #"\d+|\+|\-|\*|\/|\(|\)" input)
        [result] (reduce (fn [[acc stack] token]
                           (cond
                             (re-matches #"\d+" token) [(conj acc token) stack]
                             (= "(" token) [acc (conj stack acc)]
                             (= ")" token) [(peek stack) (pop stack)]
                             :else [(conj (into [] (pop acc)) token (peek acc)) stack]))
                         [[] []]
                         tokens)]
    (str/join " " result)))
(def evaluate (s/button :text "="
                        :foreground :black
                        :background Color/CYAN
                        :listen [:action (fn [_] (let [result (try
                                                                (str (eval (read-string (str "(" (string-swap @current-value) ")"))))
                                                                (catch Exception e "Error"))]
                                                   (reset! current-value result)
                                                   (s/config! text-panel :text result)))]))
(def grid-layout
  (s/grid-panel
    :rows 5
    :columns 4
    :items (vec (concat
                  [delete clear-line clear-everything divide]

                  (for [i [7 8 9]]
                    (s/button :text (str i)
                              :background :lightgray
                              :foreground :black
                              :listen [:action (fn [_] (update-text-panel (str i)))]))
                  [multiply]

                  (for [i [4 5 6]]
                    (s/button :text (str i)
                              :background :lightgray
                              :foreground :black
                              :listen [:action (fn [_] (update-text-panel (str i)))]))
                  [minus]

                  (for [i [1 2 3]]
                    (s/button :text (str i)
                              :background :lightgray
                              :foreground :black
                              :listen [:action (fn [_] (update-text-panel (str i)))]))
                  [plus]

                  [negative-positive
                   (s/button :text "0"
                             :background :lightgray
                             :foreground :black
                             :listen [:action (fn [_] (update-text-panel "0"))])
                   decimals
                   evaluate]))))

(def container-panel (s/border-panel
                       :north text-panel
                       :center grid-layout))

(display container-panel)



