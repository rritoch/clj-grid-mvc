(ns com.vnetpublishing.clj.grid.lib.mvc.types)

(gen-interface 
  :name com.vnetpublishing.clj.grid.lib.mvc.types.Object
  :methods [[get [String] Object]
            [get [String Object] Object]
            [set [String Object] void]
            [getProperties [] java.util.Map]
            [postConstructHandler [] void]])

(gen-interface
 :name com.vnetpublishing.clj.grid.lib.mvc.types.Model
 :extends [com.vnetpublishing.clj.grid.lib.mvc.types.Object]
 :methods [[getError [] Object]
           [setError [Object] void]
          ])

(gen-interface
  :name com.vnetpublishing.clj.grid.lib.mvc.types.TemplateEngine
  :extends [com.vnetpublishing.clj.grid.lib.mvc.types.Object]
  :methods [[render [String Object] void]
            [getDefaultFileExt [] String]])

(gen-interface
 :name com.vnetpublishing.clj.grid.lib.mvc.types.View
 :extends [com.vnetpublishing.clj.grid.lib.mvc.types.Object]
 :methods [[render [] void]
           [setLayout [String] void]
           [getLayout [] String]
           [setFormat [String] void]
           [getFormat [] String]
           [display [] void]
           [getModule [] com.vnetpublishing.clj.grid.lib.mvc.types.Object]
           [getModule [String] com.vnetpublishing.clj.grid.lib.mvc.types.Object]
           [insertTemplatePath [String] void]
           [insertTemplatePath [String int] void]
           [addTemplatePath [String] void]
           [postConstructHandler [Object] void]
          ])

(gen-interface
 :name com.vnetpublishing.clj.grid.lib.mvc.types.Controller
 :extends [com.vnetpublishing.clj.grid.lib.mvc.types.Object]
 :methods [[getModule [] com.vnetpublishing.clj.grid.lib.mvc.types.Object]
           [dispatch [] Boolean]
           [dispatch [Boolean] Boolean]
           [postConstructHandler [Object] void]
          ])

(gen-interface
 :name com.vnetpublishing.clj.grid.lib.mvc.types.Module
 :extends [com.vnetpublishing.clj.grid.lib.mvc.types.Object]
 :methods [[getModel [String] com.vnetpublishing.clj.grid.lib.mvc.types.Model]
           [getView [String] com.vnetpublishing.clj.grid.lib.mvc.types.View]
           [getController [String] com.vnetpublishing.clj.grid.lib.mvc.types.Controller]
           [getClassPrefix [] String]
           [start [Object] void]
           [stop [Object] void]
           [postConstructHandler [] void]
          ])

; End of namespace com.vnetpublishing.clj.grid.lib.mvc.types