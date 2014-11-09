(ns com.vnetpublishing.clj.grid.lib.mvc.engine
  (:use [com.vnetpublishing.clj.grid.lib.grid.kernel]))

(def ^:dynamic *current-view* nil)

(defmacro assign
   [target values]
   (loop [v values f (conj '() (symbol "t")) t (if (instance? java.util.List)
                                                   (eval `(do ~target))
                                                   target)]
     (if (empty? v)
         (conj (conj f [(symbol "t") target]) (symbol "let"))
         (recur (butlast (butlast v))
                (conj f 
                      (conj (conj (conj (conj (conj nil 
                                                    (last v))  
                                              (last (butlast v))) 
                                        (symbol "set")) 
                                  (symbol "t")) 
                            (symbol ".")))
                 t))))

(defn set-default-template-engine
  [id]
  (swap! *transaction* assoc :default-template-engine id))

(defn get-default-template-engine
  []
  (:default-template-engine @*transaction*))

(defn get-template-engine
  ([] 
     (let [id (get-default-template-engine)]
          (get-template-engine id))) 
  ([id] 
     (let [engines (deref (:template-engines @*transaction*))]
          (get engines id))))

(defn start
  []
  (swap! *transaction* assoc :template-engines (atom {})))

(defn remove-template-engine 
  [id]
  (swap! (:template-engines @*transaction*) dissoc id))

(defn add-template-engine
  [id cl]
  (let [te (create-instance (resolve (symbol cl)) [])]
       (swap! (:template-engines @*transaction*) assoc id te)))
