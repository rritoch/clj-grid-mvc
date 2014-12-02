(ns com.vnetpublishing.clj.grid.mvc.engine
  (:require [com.vnetpublishing.clj.grid.lib.grid.kernel :refer :all]
            [clojure.java.io :as io]
            [clojure.string :as string])
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
     (let [t-sym (gensym "t")]
          (loop [v values 
                 f (conj '() t-sym) 
                 t (if (instance? java.util.List target)
                       (eval `(do ~target))
                       target)]
                (if (empty? v)
                    (conj (conj f [t-sym target]) (symbol "let"))
                    (recur (butlast (butlast v))
                           (conj f 
                                 (conj (conj (conj (conj nil 
                                                         (last v))  
                                                   (last (butlast v))) 
                                             t-sym)
                                       `ns-set))
                           t)))))

(defn ns-load-time
  [t-ns]
    (let [m (meta t-ns)]
         (:ns-load-timestamp m)))

(defn ns-load
  [ns-sym]
     (debug (str "Loading namespace: " 
                 (name ns-sym)
                 " with ClassLoader "
                 (pr-str (.getContextClassLoader (Thread/currentThread)))))
     (if-let [r (io/resource (str (string/replace (name ns-sym) "." "/")
                                  ".clj"))]
               (load-resource (.toURI r))
               (debug (str "Resource " 
                           (str (string/replace (name ns-sym) "." "/")
                                  ".clj")
                           " not found!")))
     (if-let [t-ns (find-ns ns-sym)]
       (if (not (ns-load-time t-ns))
           (alter-meta! t-ns
                        assoc
                        :ns-load-timestamp
                        (.getTime (Date.))))
       (debug (str "Namespace "
                   (name ns-sym)
                   " not found"))))

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
