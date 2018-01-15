(ns flupf-web-client.construct
  (:require [reagent.core :as reagent]
            [flupf-web-client.api-service :as api]))

(defn create-state []
  "initiate state"
  (reagent/atom {:active-page   :login
                 :authenticated nil}))


(defn sign-out []
  (api/api-post {:endpoint "signout"
                 :keyword  :signout-response
                 :params   {}})
  (println "signed out"))

;;----------------------------------------
;;---------         MENU         ---------
;;----------------------------------------
(defn settings-menu
  ""
  []
  [{:name   "Settings"
    :icon   "fa fa-wrench"
    :id     "s1"
    :action (fn []
              (println "Settings"))}

   {:name   "Sign out"
    :icon   "fa fa-power-off"
    :id     "s2"
    :action (fn []
              (sign-out))}])

(defn navigation-menu
  ""
  []
  [{:name   "Timeline"
    :icon   "fa fa-clock-o"
    :id     "s3"
    :action (fn []
              (println "Timeline"))}
   {:name   "Messages"
    :icon   "fa fa-envelope-o"
    :id     "s4"
    :action (fn []
              (println "Messages"))}
   {:name   "Gallery"
    :icon   "fa fa-picture-o"
    :id     "s5"
    :action (fn []
              (println "Gallery"))}])