(ns flupf-web-client.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core :as reagent]
            [cljs.core.async :refer [<! chan put!]]
            [flupf-web-client.views :refer [landing-page]]
            [flupf-web-client.construct :refer [create-state]]
            [flupf-web-client.api-service :as api]))


;; --- Initialize app ---

(def response-chanel (chan))

(defn mount-root [state]
  "render root component"
  (reagent/render [#(landing-page state)] (.getElementById js/document "app")))


(defn init! []
  (let [state (create-state)]
    (api/authenticate state response-chanel)
    (go (let [[state] (<! response-chanel)]
          (mount-root state)))))
