(ns com.vnetpublishing.clj.grid.lib.mvc.base.module
  (:gen-class 
     :name com.vnetpublishing.clj.grid.lib.mvc.base.Module
     ;:state state
     :extends com.vnetpublishing.clj.grid.lib.mvc.base.Object
     :implements [com.vnetpublishing.clj.grid.lib.mvc.types.Module])
  (:use [com.vnetpublishing.clj.grid.lib.grid.kernel]
        [com.vnetpublishing.clj.grid.lib.mvc.engine]))

(defn safe-name 
  [name]
  ; $regex = array('#(\.){2,}#', '#[^A-Za-z0-9\.\_\- ]#', '#^\.#');
  ; return preg_replace($regex, '', $name);
  name)

(defn -start
  [this context]
  nil)

(defn -stop
  [this context]
  nil)

(defn -getClassPrefix
  [this]
  (.getName (type this)))

(defn -getController
  [this name]
  (let [controller-name (safe-name name)
        controller-lcname (clojure.string/lower-case controller-name)
        base-path (.get this "_basePath")]
    (if (not (.get (.get this "_controllers") controller-lcname))
      (let [controller-class-prefix (str (.getClassPrefix this) ".controllers.")
            controller-class-name (str controller-class-prefix controller-name)
           ]
        (if (not (class-exists? controller-class-name))
          (conjure (str base-path *ds* "controllers" *ds* controller-name ".clj")
                   (str (clojure.string/replace controller-class-name "." *ds*)
                        ".clj")))
        (.set (.get this "_controllers") 
              controller-lcname
              (create-instance (resolve (symbol controller-class-name)) 
                               [] 
                               this))))
    (.get (.get this "_controllers") controller-lcname)))


(defn -getView
  [this name]
  (let [view-name (safe-name name)
        view-lcname (clojure.string/lower-case view-name)
        base-path (.get this "_basePath")]
    (if (not (.get (.get this "_controllers") view-lcname))
      (let [view-class-prefix (str (.getClassPrefix this) ".views.")
            view-class-name (str view-class-prefix view-name)
            view-ns (str view-class-prefix view-lcname "." view-name)]
        (if (not (class-exists? view-class-name))
            (conjure (str base-path *ds* "views" *ds* view-lcname *ds* view-name ".clj")
                     (str (clojure.string/replace view-ns "." *ds*)
                          ".clj")))
        (.set (.get this "_views") 
              view-lcname
              (create-instance (resolve (symbol view-class-name)) 
                               [] 
                               this))))
    (.get (.get this "_views") view-lcname)))

(defn -getModel
  [this name]
  (let [model-name (safe-name name)
        model-lcname (clojure.string/lower-case model-name)
        base-path (.get this "_basePath")]
    (if (not (.get (.get this "_models") model-lcname))
        (let [model-class-prefix (str (.getClassPrefix this) ".models.")
              model-class-name (str model-class-prefix model-name)]
        (if (not (class-exists? model-class-name))
          (conjure (str base-path *ds* "models" *ds* model-name ".clj")
                   (str (clojure.string/replace model-class-name "." *ds*)
                        ".clj")))
        (.set (.get this "_models") 
              model-lcname
              (create-instance (resolve (symbol model-class-name))
                               []))))
    (.get (.get this "_models") model-lcname)))

(defn -postConstructHandler
  [this]
  (assign this ["_views" (new com.vnetpublishing.clj.grid.lib.mvc.base.Object)
                "_controllers" (new com.vnetpublishing.clj.grid.lib.mvc.base.Object)
                "_models" (new com.vnetpublishing.clj.grid.lib.mvc.base.Object)]))
