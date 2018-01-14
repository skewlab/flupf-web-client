(ns flupf-web-client.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core :as reagent]
            [cljs.core.async :refer [<! chan put!]]
            [flupf-web-client.views :refer [home-page
                                            login-page]]
            [flupf-web-client.construct :refer [create-state]]
            [flupf-web-client.api-service :as api]
            [secretary.core :as secretary :include-macros true]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [reagent.core :as reagent]
            [flupf-web-client.session :as session])
  (:import goog.History))


;; --- Initialize app ---
(def app-state (create-state))
(def response-chanel (chan))

(defn set-page! [page]
  (session/put! :current-page page))

;--- Uri Routes ---

(defn app-routes []
  (secretary/set-config! :prefix "#")

  (secretary/defroute "/" []
                      (println "root"))

  (secretary/defroute "/home" []
                      (println "i home defroute, core auth is: " (session/get :authenticated))
                      (set-page! :home))

  (secretary/defroute "/home" []
                      (println "i login defroute, core auth is: " (session/get :authenticated))
                      (set-page! :login))
  )

(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
      EventType/NAVIGATE
      (fn [event]
        (secretary/dispatch! (.-token event))))
    (.setEnabled true)))


;--- Routing views ---

(defmulti active-page #(session/get :current-page))

(defmethod active-page :home []
  (if (session/get :authenticated)
    [home-page]
    [login-page]))

(defmethod active-page :login [] [login-page])

(defmethod active-page :loading [] [:div "loading"])


; --- Initial rendering ---

(defn mount-root []
  "render root component"
  (reagent/render [active-page] (.getElementById js/document "app")))


(defn init! []
  (api/authenticate response-chanel)
  (go (let [[name response] (<! response-chanel)]
        (app-routes)
        (if (session/get :authenticated)
          (set-page! :home)
          (set-page! :login)))

      )
  (hook-browser-navigation!)
  (mount-root))

