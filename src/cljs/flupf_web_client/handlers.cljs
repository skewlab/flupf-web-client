(ns flupf-web-client.handlers
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [<! chan put!]]
            [flupf-web-client.core :refer [constructchanel]]
            [flupf-web-client.construct :refer [create-state]]))

(def EVENT-MAPPER
  {:initiate-state (create-state)})

(go
  (while true
    (let [[event-name event-data] (<! constructchanel)]
      ((event-name EVENT-MAPPER) event-data))))