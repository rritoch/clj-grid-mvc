(ns com.vnetpublishing.clj.grid.mvc.engine
  (:require [com.vnetpublishing.clj.grid.lib.grid.kernel :refer :all])
  (:import [java.util Date]))

(defn get-ns-state
  [t-ns]
    (let [ns-dir (get-ob-transaction-state *ns*)]
         (if-let [state (get (deref ns-dir) (.getName t-ns))]
                 state
                 (let [state (atom {})]
                      (swap! ns-dir assoc (.getName t-ns) state)
                      state))))
          

(def ^:dynamic *current-view* nil)

(defn ns-set
  [t-ns name value]
    (try  (let [m (get (ns-publics t-ns) (symbol (str "set-" name)))
                f (and (var? m)
                       (fn? (var-get m))
                       (var-get m))]
               (if f
                   (f value)
                   (swap! (:properties (deref (get-ns-state t-ns)))
                          assoc
                          name
                          value)))
         (catch Throwable
                t
                (throw (Exception. (str "Unable to set property " 
                                        name 
                                        " in namespace " 
                                        (.toString t-ns)) t)))))


(defmacro assign
   [target values]
   (loop [v values f (conj '() (symbol "t")) t (if (instance? java.util.List)
                                                   (eval `(do ~target))
                                                   target)]
     (if (empty? v)
         (conj (conj f [(symbol "t") target]) (symbol "let"))
         (recur (butlast (butlast v))
                (conj f 
                      (conj (conj (conj (conj nil 
                                              (last v))  
                                              (last (butlast v))) 
                                         
                                  (symbol "t")) 
                            (symbol "com.vnetpublishing.clj.grid.mvc.engine/ns-set")))
                 t))))



(defn ns-load-time
  [t-ns]
    (let [m (meta t-ns)]
         (:ns-load-timestamp m)))

(defn ns-load
  [ns-sym]
     (load (name ns-sym))
     (when-let [t-ns (find-ns ns-sym)]
       (if (not (ns-load-time t-ns))
           (alter-meta! t-ns
                        assoc
                        :ns-load-timestamp
                        (.getTime (Date.))))))



(defn ns-get
  ([t-ns name default-value]
     (let [m (get (ns-publics t-ns) (symbol (str "get-" name)))
           f (and (var? m)
                 (fn? (var-get m))
                 (var-get m))]
          (if f
              (or (f)
                  default-value)
              (or (get (deref (:properties (deref (get-ns-state t-ns)))) 
                       name)
                  default-value))))
  ([t-ns name]
    (ns-get t-ns name nil)))

(defn ns-call
  [t-ns sym & args]
    (let [m (get (ns-publics t-ns) sym)]
         (when-let [f (and (var? m)
                      (fn? (var-get m))
                      (var-get m))]
                   (apply f args))))