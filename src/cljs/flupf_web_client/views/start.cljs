(ns flupf-web-client.views.start
  (:require [flupf-web-client.views.signup :refer [sign-up-page]]))

(defn start-page
  "First view of the webpage"
  [state]
  [:div
   [:p "allradey have an account?"]
   [:a {:href "#/login"} "LOGIN HERE"]
   (sign-up-page)])