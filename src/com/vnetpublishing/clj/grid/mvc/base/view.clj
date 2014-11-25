(ns com.vnetpublishing.clj.grid.mvc.base.view
  (:require [clojure.string :as string]
            [clojure.java.io :as io]
            [com.vnetpublishing.clj.grid.lib.grid.kernel :as kernel]
            [com.vnetpublishing.clj.grid.mvc.engine :refer :all]
            [com.vnetpublishing.clj.grid.mvc.base.object :as obj]
            [com.vnetpublishing.clj.grid.mvc.base.template :as template])
  (:import [java.util HashMap]
           [java.io File]))

(defmacro make-view
  []
    `(com.vnetpublishing.clj.grid.mvc.base.object/make-object))

(defn get-module
  [t-ns] 
    (ns-get t-ns "_parentModule"))

(defn display
  [t-ns]
    (ns-call t-ns 'display))

(defn lookup
  [filename]
  (let [path (.getPath (.relativize (.toURI (File. ""))
                                    (.toURI (File. filename))))]
       (kernel/debug (str "lookup path="
                          path))
       (or (if (kernel/get-local-resource path) filename)
           (if (kernel/get-local-resource (str "WEB-INF/resources/" 
                                        path)) 
               (str "WEB-INF" 
                    kernel/*ds* 
                    "resources" 
                    kernel/*ds* 
                    filename))
           (if (io/resource (str "META-INF/resources/"
                                 path))
               filename)
           (if (kernel/get-bundle-resource path) filename))))


(defn render
  [t-ns]
    (let [layout (or (ns-get t-ns "_layout") "index")
          format (or (ns-get t-ns "_format") "html")
          tmpfile (str format kernel/*ds* layout (ns-get t-ns "_default_template_ext" ".jsp"))
          search-paths (.get (ns-get t-ns "_paths") "template")
          len (count search-paths)]
     (loop [idx 0 found nil]
       (if (or (>= idx len) found)
         (if found
            (template/eval-template found 
                                    (obj/get-properties t-ns))
            (throw (Exception. (str "Template not found : " tmpfile " in " search-paths))))
         (recur (+ 1 idx)
                (lookup (str (nth search-paths idx) 
                             kernel/*ds* 
                             tmpfile)))))))

(defn add-template-path
  [t-ns path]
    (if (not (.get (ns-get t-ns "_paths") 
                   "template"))
        (.put (ns-get t-ns "_paths") 
              "template" 
              []))
    (.put (ns-get t-ns "_paths") 
          "template" 
          (conj (.get (ns-get t-ns "_paths") 
                      "template") 
                path)))

(defn insert-template-path
  ([t-ns path] (insert-template-path t-ns path 0))
  ([t-ns path offset]
     (cond (not (.get (ns-get t-ns "_paths") "template"))
           (add-template-path t-ns path)
           (> offset (count (get (ns-get t-ns "_paths") "template")))
           (add-template-path t-ns path)
           :else
           (let [p (.get (ns-get t-ns "_paths") "template")]
                (.put (ns-get t-ns "_paths")
                      "template"
                      (into (conj (subvec p 0 offset)
                                  path)
                            (subvec p offset)))))))

(defn set-layout
  [t-ns layout]
    (obj/ns-set t-ns "_layout" layout))

(defn get-layout
  [t-ns]
    (obj/ns-get t-ns "_layout" "index"))

(defn set-format
  [t-ns format]
    (obj/ns-set t-ns "_format" format))

(defn get-format
  [t-ns]
    (obj/ns-get t-ns "_format" "html"))

(defn ns-init
  [view-ns module-ns]
    (when (obj/ns-init view-ns)
          (assign view-ns ["_parentModule" module-ns
                        "_paths" (HashMap.)
                        "_layout" "index"
                        "_format" "html"
                        ;"_childModules" (HashMap.)
                        ])
          true))
