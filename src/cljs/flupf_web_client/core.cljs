(ns flupf-web-client.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core :as reagent]
            [cljs.core.async :refer [<! chan put!]]
            [flupf-web-client.views :refer [landing-page]]
            [flupf-web-client.construct :refer [create-state]]))


;; --- Initialize app ---
(def constructchanel (chan))

(defn mount-root [state]
  "render root component"
  (reagent/render [#(landing-page state)] (.getElementById js/document "app")))


(defn init! []
  ;(put! constructchanel [:initiate-state])
  (mount-root (create-state)))
