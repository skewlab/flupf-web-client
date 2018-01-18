(ns flupf-web-client.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core :as reagent]
            [cljs.core.async :refer [<! chan put!]]
            [flupf-web-client.views.home :refer [home-page]]
            [flupf-web-client.views.signin :refer [signin-page]]
            [flupf-web-client.views.signup :refer [sign-up-page]]
            [flupf-web-client.views.start :refer [start-page]]
            [flupf-web-client.views.user-view :refer [user-page]]
            [flupf-web-client.construct :refer [create-state]]
            [flupf-web-client.api-service :as api]
            [secretary.core :as secretary :include-macros true]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [reagent.core :as reagent]
            [flupf-web-client.session :as session])
  (:import goog.History))


(defn render [page]
  "render root component"
  (reagent/render [page] (.getElementById js/document "app")))

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
                      (if (session/get :authenticated)
                        (set-hash! "/home")
                        (render start-page)))

  (secretary/defroute "/home" []
                      (println "i home defroute, core auth is: " (session/get :authenticated))
                      (render home-page))

  (secretary/defroute "/users/:id" [id]
            (println (str "User: " id)))

  (secretary/defroute "/user/:id" [id]
                      (println "i user defroute, core auth is: " (session/get :authenticated))
                      (render #(user-page id))
                      (set-hash! (str "/user/" id)))

  (secretary/defroute "/login" []
                      (println "i login defroute, core auth is: " (session/get :authenticated))
                      (render signin-page))

  (secretary/defroute "/signup" []
                      (println "i signup defroute, core auth is: " (session/get :authenticated))
                      (set-page! :signup)
                      (set-hash! "/signup"))
  )


; --- Initial rendering --


(defn init! []
  (api/authenticate response-chanel)
  (go (let [[name response] (<! response-chanel)]
        (secretary/dispatch! (.-hash js/window.location))))
  (hook-browser-navigation!)
  (app-routes))

