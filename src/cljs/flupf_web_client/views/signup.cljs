(ns flupf-web-client.views.signup
  (:require [reagent.core :as reagent]
            [flupf-web-client.session :as session]
            [flupf-web-client.api-service :refer [api-post]]
            [flupf-web-client.components :refer [input-element]]))

;;----------------------------------------
;;---------     SIGN UP PAGE     ---------
;;----------------------------------------

(defn sign-up-page []
  (let [sign-up-info (reagent/atom {:alias           (reagent/atom nil)
                                    :email           (reagent/atom nil)
                                    :password        (reagent/atom nil)
                                    :repeat-password (reagent/atom nil)
                                    :avatar-url      (reagent/atom nil)
                                    :description     (reagent/atom nil)
                                    :website         (reagent/atom nil)
                                    :phone-number    (reagent/atom nil)})]
    [:div {:class "sign-up"}
     [:h1 "Sign up"]
     [:form {:class     "signup-form"
             :on-submit (fn [e]
                          (.preventDefault e)
                          (api-post
                            {:endpoint "users"
                             :keyword  :signup-response
                             :params   {:email    @(:email @sign-up-info)
                                        :alias    @(:alias @sign-up-info)
                                        :password @(:password @sign-up-info) ;:repeat-password @(:repeat-password @sign-up-info)
                                        }}))}
      [input-element {:id          "alias"
                      :name        "alias"
                      :type        "text"
                      :placeholder "alias"
                      :value       (:alias @sign-up-info)
                      :label       "Alias"
                      :required    true}]

      [input-element {:id          "email"
                      :name        "email"
                      :type        "email"
                      :placeholder "email"
                      :value       (:email @sign-up-info)
                      :label       "Email"
                      :required    true}]

      [input-element {:id          "password"
                      :name        "password"
                      :type        "password"
                      :placeholder "password"
                      :value       (:password @sign-up-info)
                      :label       "Password"
                      :required    true}]

      [input-element {:id          "repeat-password"
                      :name        "repeat-password"
                      :type        "password"
                      :placeholder "repeat-password"
                      :value       (:repeat-password @sign-up-info)
                      :label       "Repeat Password"
                      :required    true}]

      [input-element {:id          "avatar"
                      :name        "avatar"
                      :type        "url"
                      :placeholder "Url to image"
                      :value       (:avatar-url @sign-up-info)
                      :label       "Url to image"}]

      [input-element {:id          "description"
                      :name        "description"
                      :type        "text"
                      :placeholder "Description"
                      :value       (:description @sign-up-info)
                      :label       "Description"}]

      [input-element {:id          "website"
                      :name        "website"
                      :type        "url"
                      :placeholder "Website"
                      :value       (:website @sign-up-info)
                      :label       "Website"}]

      [input-element {:id          "phone-number"
                      :name        "phone-number"
                      :type        "text"
                      :placeholder "Phone number"
                      :value       (:phone-number @sign-up-info)
                      :label       "Phone number"}]

      [:input {:type  "submit"
               :value "sign up"
               :class "button"}]]

     [:div (session/get-in [:signup-response :message])]
     [:span {:class "full-link-wrapper"}]]))