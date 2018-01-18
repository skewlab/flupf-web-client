(ns flupf-web-client.views.signin
  (:require [reagent.core :as reagent]
            [flupf-web-client.api-service :refer [api-post]]
            [flupf-web-client.components :refer [input-element]]))

;;----------------------------------------
;;---------     SIGN IN PAGE     ---------
;;----------------------------------------

(defn signin-page
  "Login form"
  []
  (let [email-address (reagent/atom nil)
        password (reagent/atom nil)
        credentials (reagent/atom nil)]
    [:div {:class "sign-in"}
     [:h1 "Sign in"]
     [:form {:class     "login-form"
             :on-submit (fn [e] (.preventDefault e))}
      [input-element {:id          "email"
                      :name        "email"
                      :type        "email"
                      :placeholder "email"
                      :value       email-address}]

      [input-element {:id          "password"
                      :name        "password"
                      :type        "password"
                      :placeholder "password"
                      :value       password}]

      [:input {:type     "submit"
               :value    "sign in"
               :class    "button"
               :on-click (fn []
                           (api-post {:endpoint "signin"
                                      :keyword  :signin-response
                                      :params   {:email    @email-address
                                                 :password @password}}))}]]
     [:span {:class "full-link-wrapper"}
      [:a {:href "/"} "Forgot your password?"]]]))