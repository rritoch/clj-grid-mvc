(ns com.vnetpublishing.clj.grid.lib.mvc.base.model
  (:gen-class
    :name com.vnetpublishing.clj.grid.lib.mvc.base.Model
    :extends com.vnetpublishing.clj.grid.lib.mvc.base.Object
    :implements [com.vnetpublishing.clj.grid.lib.mvc.types.Model])
  (:use [com.vnetpublishing.clj.grid.lib.grid.kernel]))

(defn -getError
  [this]
  (.get this "_error" nil))

(defn -setError
  [this e]
  (.set this "_error" e))
