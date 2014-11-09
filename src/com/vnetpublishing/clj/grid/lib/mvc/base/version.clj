(ns com.vnetpublishing.clj.grid.lib.mvc.base.version
    (:gen-class (:name com.vnetpublishing.clj.grid.lib.mvc.base.Version))
    (:require [clojure.java.io :as io]))

(def ^:private project-version #_(-> (io/reader (io/resource "project.clj")) 
                                    slurp 
                                    read-string 
                                    (nth 2))
                                "0.1.0-SNAPSHOT")

(defn getVersion
  []
  (-> (clojure.string/split project-version #"-")
      first
      (clojure.string/split #"\.")
      vec)) 
    
          