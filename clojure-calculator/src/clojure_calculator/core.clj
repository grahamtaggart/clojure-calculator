(ns clojure-calculator.core
  (:gen-class)
  (:require [clojure-calculator.frame :as calculator]))

(defn -main
  "Entry point for the calculator application"
  [& args]
  (calculator/display calculator/container-panel))


