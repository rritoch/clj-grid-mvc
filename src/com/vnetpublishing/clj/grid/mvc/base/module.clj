(ns com.vnetpublishing.clj.grid.mvc.base.module
  (:require [clojure.string :as string]
            [com.vnetpublishing.clj.grid.lib.grid.kernel :refer :all]
            [com.vnetpublishing.clj.grid.mvc.engine :refer :all]
            [com.vnetpublishing.clj.grid.mvc.base.object :as obj]
            [com.vnetpublishing.clj.grid.mvc.base.model :as model]
            [com.vnetpublishing.clj.grid.mvc.base.controller :as controller]
            [com.vnetpublishing.clj.grid.mvc.base.view :as view]
            [clojure.java.io :as io])
  (:import [java.util HashMap]
           [java.io File]))

(defmacro make-module
  []
     `(com.vnetpublishing.clj.grid.mvc.base.object/make-object))

(defn safe-name 
  [name]
  ; $regex = array('#(\.){2,}#', '#[^A-Za-z0-9\.\_\- ]#', '#^\.#');
  ; return preg_replace($regex, '', $name);
  name)

(defn start
  [t-ns context]
    (if (extern-callable? t-ns 'start)
        (call-other t-ns 'start context)))

(defn stop
  [t-ns context]
    (if (extern-callable? t-ns 'stop)
        (call-other t-ns 'stop context)))

(defn ^:private need-load?
  [ns-sym]
     (let [t-ns (find-ns ns-sym)
           load-ts (if t-ns (ns-load-time t-ns))]
          (if (or (not t-ns)
                  (not load-ts) 
                  (let [r (io/resource (string/replace (name ns-sym) "." "/"))
                        f (if (= "file" (.getScheme r))
                              (File. (.getPath r)))]
                    (and f
                         (> (.lastModified f)
                            load-ts))))
              true
              false)))

(defn get-ns-prefix
  [t-ns]
    (let [ns-name (.toString t-ns)]
         (subs ns-name 0 (.lastIndexOf ns-name "."))))

(defn get-controller
  [t-ns name]
  (let [controller-name (safe-name name)
        controller-lcname (string/lower-case controller-name)
        ;base-path (ns-get t-ns "_basePath")
        controller-ns-prefix (str (get-ns-prefix t-ns) ".controllers.")
        controller-ns-name (str controller-ns-prefix controller-name)
        controller-ns-sym (symbol controller-ns-name)]
    (when (or (not (.get (ns-get t-ns "_controllers") controller-lcname))
              (need-load? controller-ns-sym))
          (ns-load controller-ns-sym)
          (.put (ns-get t-ns "_controllers") 
                controller-lcname
                (find-ns controller-ns-sym)))
    (controller/ns-init (find-ns controller-ns-sym)
                        t-ns)
    (.get (ns-get t-ns "_controllers") controller-lcname)))

(defn get-view
  [t-ns name]
  (let [view-name (safe-name name)
        view-lcname (string/lower-case view-name)
        ;base-path (ns-get t-ns "_basePath")
        view-ns-prefix (str (get-ns-prefix t-ns) ".views.")
        view-ns-name (str view-ns-prefix view-name "." view-name)
        view-ns-sym (symbol view-ns-name)]
    (when (or (not (.get (ns-get t-ns "_views") view-lcname))
              (need-load? view-ns-sym))
          (ns-load view-ns-sym)
          (.put (ns-get t-ns "_views") 
                view-lcname
                (find-ns view-ns-sym)))
    (view/ns-init (find-ns view-ns-sym)
                           t-ns)
    (.get (ns-get t-ns "_views") view-lcname)))

(defn get-model
  [t-ns name]
  (let [model-name (safe-name name)
        model-lcname (string/lower-case model-name)
        ;base-path (ns-get t-ns "_basePath")
        model-ns-prefix (str (get-ns-prefix t-ns) ".models.")
        model-ns-name (str model-ns-prefix model-name)
        model-ns-sym (symbol model-ns-name)]
    (when (or (not (.get (ns-get t-ns "_models") model-lcname))
              (need-load? model-ns-sym))
          (ns-load model-ns-sym)
          (.put (ns-get t-ns "_models") 
                model-lcname
                (find-ns model-ns-sym)))
    (model/ns-init (find-ns model-ns-sym))
    (.get (ns-get t-ns "_models") model-lcname)))

(defn ns-init
  [t-ns]
    (when (obj/ns-init t-ns)
          (assign t-ns ["_views" (HashMap.)
                        "_controllers" (HashMap.)
                        "_models" (HashMap.)])
          true))

