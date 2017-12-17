(ns ^:figwheel-no-load flupf-web-client.dev
  (:require
    [flupf-web-client.core :as core]
    [devtools.core :as devtools]))

(devtools/install!)

(enable-console-print!)

(core/init!)
