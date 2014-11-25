(ns com.vnetpublishing.clj.grid.mvc.base.version
  (:require [clojure.string :as string]))

(def ^:private project-version "0.1.0-SNAPSHOT")

(defn get-version
  []
    (-> (string/split project-version #"-")
        first
        (string/split #"\.")
        vec))
