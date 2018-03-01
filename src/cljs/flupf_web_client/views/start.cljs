(ns flupf-web-client.views.start
  (:require [flupf-web-client.views.signup :refer [sign-up-page]]
            [flupf-web-client.construct :refer [header]]
            [flupf-web-client.views.signin :refer [signin-page]]
            [flupf-web-client.session :as session]))


(defn start-page
  "First view of the webpage"
  []
  [:div
   [header]
   (if (session/get :sign-in)
     [signin-page]
     [sign-up-page])])