(ns flupf-web-client.core
  (:require [reagent.core :as reagent :refer [atom]]
            [secretary.core :as secretary :include-macros true]
            [accountant.core :as accountant]
            [flupf-web-client.views :as views]))



;; -------------------------


(defn about-page []
  [:div [:h2 "About flupf-web-client"]
   [:div [:a {:href "/"} "go to the home page"]]])

;; -------------------------
;; Routes

(def page (atom #'views/home-page))

(defn current-page []
  [:div [@page]])

(secretary/defroute "/" []
                    (reset! page #'views/home-page))

(secretary/defroute "/about" []
                    (reset! page #'about-page))

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (accountant/configure-navigation!
    {:nav-handler
     (fn [path]
       (secretary/dispatch! path))
     :path-exists?
     (fn [path]
       (secretary/locate-route path))})
  (accountant/dispatch-current!)
  (mount-root))
