(defproject clj-grid-mvc "0.1.0-SNAPSHOT"
  :description "Grid MVC Support"
  :url "http://www.vnetpublishing.com"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [clj-grid-kernel "0.1.0-SNAPSHOT"]]
  ;;:repositories [["releases" {:url "http://home.vnetpublishing.com/artifactory/libs-release-local"
  ;;                            :creds :gpg}]
  ;;               ["snapshots" {:url "http://home.vnetpublishing.com/artifactory/libs-snapshot-local"
  ;;                             :creds :gpg}]]
  :prep-tasks [["compile" "com.vnetpublishing.clj.grid.lib.mvc.types"]
               ["compile" "com.vnetpublishing.clj.grid.lib.mvc.base.object"]]
  :aot :all)