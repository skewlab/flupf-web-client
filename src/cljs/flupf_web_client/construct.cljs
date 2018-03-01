(ns flupf-web-client.construct
  (:require [reagent.core :as reagent]
            [flupf-web-client.api-service :as api]
            [flupf-web-client.session :as session]))

(defn create-state []
  "initiate state"
  (reagent/atom {:active-page   :login
                 :authenticated nil
                 :sign-in false}))


(defn sign-out []
  (api/api-post {:endpoint "signout"
                 :keyword  :signout-response
                 :params   {}})
  (println "signed out"))


(defn contact-name [user-id link-tag]
  [:a {:href (str "/#/user/" user-id)}
   link-tag])

;;----------------------------------------
;;---------         Header       ---------
;;----------------------------------------
(defn header
  ""
  []
  [:header
   [:h1 {:class "logo-header"} "flupf - a simplistic network"]
   [:a "about"]
   (if (session/get :sign-in)
     [:button {:class "sign-in-btn"
               :on-click #(session/put! :sign-in false)} "Sign up!"]
     [:button {:class "sign-in-btn"
               :on-click #(session/put! :sign-in true)} "Sign in"])])

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

(defn contact-menu
  ""
  []
  [{:name   "Timeline"
    :icon   "fa fa-clock-o"
    :id     "s3"
    :action (fn []
              (println "Timeline"))}
   {:name   "Gallery"
    :icon   "fa fa-picture-o"
    :id     "s5"
    :action (fn []
              (println "Gallery"))}])