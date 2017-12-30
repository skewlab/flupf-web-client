(ns flupf-web-client.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core :as reagent :refer [atom]]
            [secretary.core :as secretary :include-macros true]
            [accountant.core :as accountant]
            [flupf-web-client.views :as views]
            [flupf-web-client.api-service :as api]
            [cljs.core.async :refer [<!]]))


(defonce app-state (atom {:authenticated false
                          :user-id       nil}))
;; -------------------------


(defn about-page [state]
  [:div [:h2 "About flupf-web-client"]
   [:div [:a {:href "/"} "go to the home page"]]])

;; -------------------------



;; Routes

(def page (atom #'views/start-page))

(defn current-page []
  [:div [@page]])


(secretary/defroute "/" []
                    (if (:authenticated @app-state)
                      (secretary/dispatch! "/home")
                      (reset! page #'views/start-page)))

(secretary/defroute "/home" []
                    (reset! page #(views/home-page app-state)))

(secretary/defroute "/about" []
                    (reset! page #'about-page))


;; -------------------------
;; Initialize app
(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn signed-in? [state]
  (go (let [res (<! (api/authenticate state))]
        (if (= res 200)
          (swap! state assoc :authenticated true)
          (swap! state assoc :authenticated false))
        (accountant/configure-navigation!
          {:nav-handler
           (fn [path]
             (secretary/dispatch! path))
           :path-exists?
           (fn [path]
             (secretary/locate-route path))})
        (accountant/dispatch-current!)
        (mount-root)
        )))

(defn init! []
  (signed-in? app-state)
  )
