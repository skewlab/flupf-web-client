(ns flupf-web-client.views
  (:require [reagent.core :as reagent :refer [atom]]
            [secretary.core :as secretary :include-macros true]
            [accountant.core :as accountant]))

;--- HEADER ---
(defn sign-out [username]
          (println username "signed out"))

(defn header []
  [:header
   [:a {:on-click   (sign-out "Jonas")
        :class "link-right link"}
    "Sign out"]
   [:a {:href "/my-profile"
        :class-name "link-right link"}
    "My Profile"]
   [:a {:href "/start"
        :class-name "link-left link"}
    "Start"]]
  )

(defn profile-sidebar []
  [:div {:class "profile-info"}
   [:img {:src "http://www.pushetta.com/uploads/channel_media/497e655768de45f28d14039c45fc0fee.bmp"
          :alt "no avatar available"}]
   [:h2 "Username"]
   [:ul
    [:li "small description text."]]])

(defn user-feed []
  [:div {:class "profile-feed"}
   [:div {:class "post"}
    [:p "ID:"
     [:h2 "This is a mock post"]
     "author: "
     [:br]
     "Date: "
     [:br]
     "Ups: "]]])

(defn contacts-list []
  [:div {:class "contacts-list"}
   [:div {:class "contact"}
    [:h2 "Alias of contact"]]]
  )

(defn contacts [contacts]
  [:div {:class "contacts"}
   [:h3 "Contacts"]
   [:ul
    (for [contact contacts]
      ^{:key contact} [:li "Name " (:name contact)])]
   ])

(defn home-page []

  [:div [header]
   [profile-sidebar]
   [user-feed]
   [contacts-list]
   ;[:h2 "Welcome to flupf-web-client"]
   ;[:div [:a {:href "/about"} "go to about page"]]
   ;[contacts [{:name "jonasj" :lastname "johansson"}
   ;           {:name "Filip" :lastname "Johansson"}]]
   ])
