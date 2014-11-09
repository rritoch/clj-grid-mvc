(ns com.vnetpublishing.clj.grid.lib.mvc.base.template
  (:require [com.vnetpublishing.clj.grid.lib.mvc.engine :as engine])
  (:use [com.vnetpublishing.clj.grid.lib.grid.kernel]))

(defn eval-clj-template 
  [source v]
  (ginc (.getFile (clojure.java.io/resource (clojure.string/replace source *ds* "/")))))

(defn eval-template 
  [source view engine]
  (binding [engine/*current-view* view]
           (if engine
               (.render engine source view)
               (eval-clj-template source view))))
