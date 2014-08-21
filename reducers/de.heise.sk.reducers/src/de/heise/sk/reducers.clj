(ns de.heise.sk.reducers
  (:require [clojure.core.reducers :as r]))

;; Das kanonische Beispiel
(def zahlen (doall (range 1e6)))

(defn standard [daten]
  (reduce + (map inc (filter odd? daten))))

(defn not-lazy [data]
  (reduce 
   + 
   (r/map inc (r/filter odd? data))))

(defn parallel [data]
  (r/fold + 
          (r/map inc 
                 (r/filter odd? data))))

;; Hintergründe
(defn mapping [map-fn]
  (fn [red-fn]
    (fn [acc element]
      (red-fn acc (map-fn element)))))

;; Fold, zweiter Teil
(def zahlen-v (vec (range 1e6)))

;; ;; Mit zuälligen Zahlen
;; (def randoms-seq (doall (repeatedly 8e5 #(rand-int 1e4))))
;; (def randoms-vec (vec   (repeatedly 8e5 #(rand-int 1e4))))

;; 
(def core (-> "clojure/core.clj"
              clojure.java.io/resource
              slurp
              vec))

(defn zaehl [string]
  (reduce
   (fn [acc zeichen]
     (update-in
      (assoc acc zeichen
             (inc (get acc zeichen 0)))
      [:all] inc))
   {:all 0}
   string))

(defn zaehl-fold [string]
  (r/fold
   (r/monoid (partial merge-with +) 
             (constantly {:all 0}))
   (fn [acc el]
     (update-in
      (assoc acc el (inc (get acc el 0)))
      [:all] inc))
   string))

(defn zeichen-zaehlen [string]
  (let [auswert   (time (zaehl string)) 
        auswert-f (time (zaehl-fold string))
        nur-chars (dissoc auswert :all)]
    (println "Gleich:" 
             (= auswert auswert-f))
    (println "Zeichen gesamt:" 
             (:all auswert))
    (print "Top: ")
    (prn (last (sort-by val nur-chars)))
    (print "Klammern: ")
    (prn (select-keys nur-chars [\( \)]))
    (println "Verschiedene:" 
             (count auswert))))