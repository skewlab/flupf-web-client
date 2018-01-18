(ns flupf-web-client.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core :as reagent]
            [cljs.core.async :refer [<! chan put!]]
            [flupf-web-client.views.home :refer [home-page]]
            [flupf-web-client.views.signin :refer [signin-page]]
            [flupf-web-client.views.signup :refer [sign-up-page]]
            [flupf-web-client.views.start :refer [start-page]]
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

(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
      EventType/NAVIGATE
      (fn [event]
        (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

(defn set-hash! [loc]
  (set! (.-hash js/window.location) loc))

(defn app-routes []
  (secretary/set-config! :prefix "#")

  (secretary/defroute "/" []
                      (println "root")
                      (set-page! :start))

  (secretary/defroute "/home" []
                      (println "i home defroute, core auth is: " (session/get :authenticated))
                      (set-page! :home)
                      (set-hash! "/home"))

  (secretary/defroute "/login" []
                      (println "i login defroute, core auth is: " (session/get :authenticated))
                      (set-page! :login)
                      (set-hash! "/login"))

  (secretary/defroute "/signup" []
                      (println "i signup defroute, core auth is: " (session/get :authenticated))
                      (set-page! :signup)
                      (set-hash! "/signup"))
  )



;--- Routing views ---

(defmulti active-page #(session/get :current-page))

(defmethod active-page :home []
  (if (session/get :authenticated)
    [home-page]
    [start-page]))

(defmethod active-page :start []
  (if (session/get :authenticated)
    [home-page]
    [start-page]
    ))

(defmethod active-page :login [] [signin-page])

(defmethod active-page :signup [] [sign-up-page])

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
          (set-page! :start))))
  (hook-browser-navigation!)
  (mount-root))

