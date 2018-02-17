(ns flupf-web-client.views.start
  (:require [flupf-web-client.views.signup :refer [sign-up-page]]
            [flupf-web-client.construct :refer [header]]
            [flupf-web-client.views.signin :refer [signin-page]]))


(defn start-page
  "First view of the webpage"
  [state]
  [:div
   [header]
   [sign-up-page]
   [:div {:class "flupf-info"}
    [:h2 "a simple introduction"]
    [:p "Flupf is a lightwheight social network. We want to give you a simple
    interface without cluttering your news feed with irrelevant things
    like adds or the information that a friend of a friend likes a certain company.
    By becomming a flupfer you don't just get a cool place to hang but also you get
    to be a part of an amazing community with really nice people!"]]
   [signin-page]])