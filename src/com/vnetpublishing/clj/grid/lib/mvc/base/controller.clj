(ns com.vnetpublishing.clj.grid.lib.mvc.base.controller
  (:gen-class
    :name com.vnetpublishing.clj.grid.lib.mvc.base.Controller
    :extends com.vnetpublishing.clj.grid.lib.mvc.base.Object
    :implements [com.vnetpublishing.clj.grid.lib.mvc.types.Controller]
    :exposes-methods {postConstructHandler parentPostConstructHandler})
  (:use [com.vnetpublishing.clj.grid.lib.grid.kernel]))

(defn safeName 
  [name]
  ; $regex = array('#(\.){2,}#', '#[^A-Za-z0-9\.\_\- ]#', '#^\.#');
  ; return preg_replace($regex, '', $name);
  name)

(defn -postConstructHandler
  [this module]
  (swap! (get-ob-transaction-state this) assoc :dispatch-lock false)
  (swap! (.state this) assoc :parent-module nil)
  (.set this "_parentModule" module)
)

(defn -getModule
  [this]
  (.get this "_parentModule")
)

(defn -dispatch
  ([this lock]
  (let [ret (if (-> (get-ob-transaction-state this) :dispatch-lock) false true)]
    (if lock
       (swap! (get-ob-transaction-state this) assoc :dispatch-lock true)
     )
    ret))
  ([this] (.dispatch this true)))
  
