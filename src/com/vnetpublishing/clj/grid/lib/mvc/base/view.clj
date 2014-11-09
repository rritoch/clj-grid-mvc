(ns com.vnetpublishing.clj.grid.lib.mvc.base.view
  (:gen-class
    :name com.vnetpublishing.clj.grid.lib.mvc.base.View
    :extends com.vnetpublishing.clj.grid.lib.mvc.base.Object
    :implements [com.vnetpublishing.clj.grid.lib.mvc.types.View])
  (:require [com.vnetpublishing.clj.grid.lib.mvc.base.template :as template])
  (:use [clojure.java.io]
        [com.vnetpublishing.clj.grid.lib.grid.kernel]
        [com.vnetpublishing.clj.grid.lib.mvc.engine]))

(defn -getModule
  ([this] (.get this "_parentModule"))
  ([this name]
    (let [parent (.get this "_parentModule")
          class-base (str (.getClassPrefix parent) ".views.")
          parts (clojure.string/split class-base (.getName (type this)))
          view-name (nth parts 1)
          module-name (clojure.string/lower-case name)]
         (if (not (.get (.get this "_childModules") module-name))
             (let [module-class-prefix (str class-base view-name ".modules.")
                   module-class-name (str module-class-prefix name)]
                  (if (not (class-exists? module-class-name))
                      (ginc-once (str (get (.get this "_paths") "viewbase") 
                                          *ds*  
                                          "modules" 
                                          *ds* 
                                          module-name 
                                          ".clj"))
                  )
                  
                  (create-instance (resolve (symbol module-class-name)) [] this)
                  #_(let [module (eval `(new ~(symbol module-class-name)))]
                     (.postConstructHandler module this)
                     (.set (.get this "_childModules") module-name module)
                  )
             )
         )
         (.get (.get this "_childModules") module-name)
         
    )
    
))

(defn -display
  [this]
  nil
)

(defn ^:private lookup
  [filename]
  (let [path (.getPath (.relativize (.toURI (java.io.File. ""))
                                    (.toURI (java.io.File. filename))))]
       (debug (str "lookup path="
                   path))
       (or (if (get-local-resource path) filename)
           (if (get-local-resource (str "WEB-INF/resources/" 
                                        path)) 
               (str "WEB-INF" 
                    *ds* 
                    "resources" 
                    *ds* 
                    filename))
           (if (resource (str "META-INF/resources/"
                              path))
               filename)
           (if (get-bundle-resource path) filename))))


(defn -render
  [this]
  (let [layout (or (.get this "_layout") "index")
        format (or (.get this "_format") "html")
        engine (get-template-engine)
        tmpfile (str format *ds* layout (.getDefaultFileExt engine))
        search-paths (.get (.get this "_paths") "template")
        len (count search-paths)]
    #_(debug (str "[render] " search-paths))
    
   (loop [idx 0 found nil]
     (if (or (>= idx len) found)
       (if found
          (template/eval-template found 
                                  (.getProperties this)
                                  engine)
          (throw (Exception. (str "Template not found : " tmpfile " in " search-paths))))
       (recur (+ 1 idx)
              (lookup (str (nth search-paths idx) 
                           *ds* 
                           tmpfile)))))))

(defn -insertTemplatePath
  ([this path] (.insertTemplatePath this path 0))
  ([this path offset]
     (cond (not (.get (.get this "_paths") "template"))
       (.addTemplatePath this path)
       (> offset (count (get (.get this "_paths") "template")))
       (.addTemplatePath this path)
       :else
       nil ;TODO: Implement ME!
        ;$paths = $this->_paths['template'];
        ;array_splice($paths, $offset, 0, array($path));
        ;$this->_paths['template'] = $paths;
    )
))

(defn -addTemplatePath
  [this path]
  #_(debug (str "addTemplatePath " path))
  (if (not (.get (.get this "_paths") 
                 "template"))
      (.set (.get this "_paths") 
            "template" 
            []))
  (.set (.get this "_paths") 
        "template" 
        (into (.get (.get this "_paths") 
                    "template") 
              [path])))

(defn -setLayout
  [this layout]
  (.set this "_layout" layout))

(defn -getLayout
  [this]
  (.get this "_layout"))

(defn -setFormat
  [this format]
  (.set this "_format" format))

(defn -getFormat
  [this]
  (.get this "_format"))

(defn -postConstructHandler
  [this module]
  (assign this ["_parentModule" module
                "_paths" (new com.vnetpublishing.clj.grid.lib.mvc.base.Object)
                "_layout" "index"
                "_format" "html"
                "_childModules" (new com.vnetpublishing.clj.grid.lib.mvc.base.Object)]))
