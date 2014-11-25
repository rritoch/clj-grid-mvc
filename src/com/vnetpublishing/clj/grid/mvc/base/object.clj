(ns com.vnetpublishing.clj.grid.mvc.base.object
  (:require [com.vnetpublishing.clj.grid.mvc.engine :as engine]
            [com.vnetpublishing.clj.grid.lib.grid.kernel :refer :all]))

(defmacro make-object
  []
    `(alter-meta! ~'*ns*
                   assoc
                   :ns-load-timestamp
                   (.getTime (java.util.Date.))))

(defn ns-init
  [t-ns]
   (when (not (:init (deref (engine/get-ns-state t-ns))))
         (debug (str "Initializing namespace " (.getName t-ns)))
         (reset! (engine/get-ns-state t-ns) 
                 {:properties (atom {})})
         (swap! (engine/get-ns-state t-ns) assoc :init true)
         (let [m (get (ns-interns t-ns) '--construct)
               f (and (var? m)
                      (fn? (var-get m))
                      (var-get m))]
              (if f
                  (try (f)
                       (catch Throwable 
                              t
                              (throw (Exception. (str "Error in " 
                                                      (.getName t-ns) 
                                                      " constructor.")
                                                t))))))
         true))

(def ns-set engine/ns-set)

(def ns-get engine/ns-get)

(defn get-properties
  [t-ns]
    (deref (:properties (deref (engine/get-ns-state t-ns)))))
