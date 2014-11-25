(ns com.vnetpublishing.clj.grid.mvc.base.model
  (:require [com.vnetpublishing.clj.grid.lib.grid.kernel :refer :all]
            [com.vnetpublishing.clj.grid.mvc.engine :refer :all]
            [com.vnetpublishing.clj.grid.mvc.base.object :as obj]))

(defmacro make-model
  []
    `(com.vnetpublishing.clj.grid.mvc.base.object/make-object))

(defn get-error
  [t-ns]
     (ns-get t-ns "_error" nil))

(defn set-error
  [t-ns e]
    (ns-set t-ns "_error" e))

(defn ns-init
  [model-ns]
    (obj/ns-init model-ns))

