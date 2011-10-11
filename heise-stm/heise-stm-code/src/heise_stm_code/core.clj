(ns heise-stm-code.core)

(def kollisionen (atom 0))

(def hochzaehler (ref 0))

(def runterzaehler (ref 100))

(defn inc-mit-zaehl [value]
  (swap! kollisionen inc)
  (inc value))

(defn dec-mit-zaehl [value]
  (swap! kollisionen inc)
  (dec value))

(defn stm-funktion []
  (dosync
   (alter hochzaehler   inc-mit-zaehl)
   (alter runterzaehler dec-mit-zaehl)))

(dotimes [_ 100]
  (.start (new Thread stm-funktion)))
