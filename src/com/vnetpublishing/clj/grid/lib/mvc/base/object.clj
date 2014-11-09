(ns com.vnetpublishing.clj.grid.lib.mvc.base.object 
 (:gen-class
   :name com.vnetpublishing.clj.grid.lib.mvc.base.Object
   :state state
   :init init
   :implements [com.vnetpublishing.clj.grid.lib.mvc.types.Object])
 
 (:use [com.vnetpublishing.clj.grid.lib.grid.kernel]
       [com.vnetpublishing.clj.grid.lib.grid.util]))

(defn -get 
  ([this p d]
    (let [n (str "get" (ucfirst p))
          s (.state this)]
        (if (method-exists? this n)
           (. n this)
           (let [prop (get (deref (:properties @s)) p)]
             (if prop prop d)))))
  ([this p] (.get this p nil)))

(defn -set
  [this p v]
  (let [n (str "set" (ucfirst p))
        s (.state this)
       ]
     (if (method-exists? this n)
        (. n this v)
        (let [properties (:properties @s)]
          (swap! properties assoc p v)))))


(defn -postConstructHandler
  [this]
  nil)

(defn -getProperties
  [this]
  (deref (:properties (deref (.state this)))))

(defn -init
  []
  [[] (atom { :properties (atom {})})])
 