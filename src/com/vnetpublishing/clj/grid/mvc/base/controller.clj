(ns com.vnetpublishing.clj.grid.mvc.base.controller
  (:require [com.vnetpublishing.clj.grid.mvc.engine :refer :all]
            [com.vnetpublishing.clj.grid.lib.grid.kernel :as kernel]
            [com.vnetpublishing.clj.grid.mvc.engine :refer :all]
            [com.vnetpublishing.clj.grid.mvc.base.object :as obj]))

(defmacro make-controller
  []
    `(com.vnetpublishing.clj.grid.mvc.base.object/make-object))

(defn safe-name
  [name]
  ; $regex = array('#(\.){2,}#', '#[^A-Za-z0-9\.\_\- ]#', '#^\.#');
  ; return preg_replace($regex, '', $name);
  name)

(defn ns-init
  [controller-ns module-ns]
    (when (obj/ns-init controller-ns)
          (swap! (get-ns-state controller-ns)
                 assoc
                 :dispatch-lock 
                 false)
          (swap! (get-ns-state controller-ns)
                 assoc
                 :parent-module
                 module-ns))
          true)
 
(defn get-module
  [controller-ns]
    (:parent-module (deref (get-ns-state controller-ns))))

(defn dispatch?
  ([controller-ns lock]
    (let [ret (if (-> (get-ns-state controller-ns) 
                      deref
                      :dispatch-lock)
                  false 
                  true)]
      (if lock
         (swap! (get-ns-state controller-ns) 
                assoc 
                :dispatch-lock 
                true))
      ret))
  ([controller-ns] (dispatch? controller-ns true)))

(defn dispatch
  [controller-ns lock]
    (kernel/call-other controller-ns 'dispatch lock))
  