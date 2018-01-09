(ns flupf-web-client.handlers
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [<! chan put!]]
            [flupf-web-client.construct :refer [create-state]]
            [reagent.core :as reagent]))

(def response-chanel (chan))

(def EVENT-MAPPER
  {:authenticate (fn[state response-data]
                   (swap! state assoc :authenticated response-data)
                   (println state "i eventmappern"))
   })

(go
  (while true
    (let [[state event-name event-data] (<! event-chanel)]
      (println "i go")
      (println event-data)
      ((event-name EVENT-MAPPER) state event-data function))))