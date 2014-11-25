(ns com.vnetpublishing.clj.grid.mvc.base.template
  (:require [com.vnetpublishing.clj.grid.mvc.engine :as engine]
            [com.vnetpublishing.clj.grid.lib.grid.kernel :refer :all]
            [clojure.string :as string]))

(defn eval-template 
  [source view]
    (binding [engine/*current-view* view]
             (let [s (str "/"
                          (string/replace source *ds* "/"))]
                  (debug (str "template: " s))
                  (.setAttribute *servlet-request* "viewdata" view)
                  (.forward (.getRequestDispatcher *servlet-request* s) 
                            *servlet-request* 
                            *servlet-response*))))
