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
    [:div
     [:div {:class "flupf-info"}
      [:h2 "a simple introduction"]
      [:p "Flupf is a lightwheight social network. We want to give you a simple
    interface without cluttering your news feed with irrelevant things
    like adds or the information that a friend of a friend likes a certain company.
    By becomming a flupfer you don't just get a cool place to hang but also you get
    to be a part of an amazing community with really nice people!"]]
     [:div {:class "sign-form sign-up"}
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
       [:div {:class "sign-column"}
        [input-element {:id          "alias"
                        :name        "alias"
                        :type        "text"
                        :placeholder "Pick an alias"
                        :value       (:alias @sign-up-info)
                        :required    true}]

        [input-element {:id          "email"
                        :name        "email"
                        :type        "email"
                        :placeholder "Your email"
                        :value       (:email @sign-up-info)
                        :required    true}]]

       [:div {:class "sign-column"}
        [input-element {:id          "password"
                        :name        "password"
                        :type        "password"
                        :placeholder "Set a password"
                        :value       (:password @sign-up-info)
                        :required    true}]

        [input-element {:id          "repeat-password"
                        :name        "repeat-password"
                        :type        "password"
                        :placeholder "Repeat the password"
                        :value       (:repeat-password @sign-up-info)
                        :required    true}]]

       #_[input-element {:id          "avatar"
                         :name        "avatar"
                         :type        "url"
                         :placeholder "Url to image"
                         :value       (:avatar-url @sign-up-info)
                         :label       "Url to image"}]

       #_[input-element {:id          "description"
                         :name        "description"
                         :type        "text"
                         :placeholder "Description"
                         :value       (:description @sign-up-info)
                         :label       "Description"}]

       #_[input-element {:id          "website"
                         :name        "website"
                         :type        "url"
                         :placeholder "Website"
                         :value       (:website @sign-up-info)
                         :label       "Website"}]

       #_[input-element {:id          "phone-number"
                         :name        "phone-number"
                         :type        "text"
                         :placeholder "Phone number"
                         :value       (:phone-number @sign-up-info)
                         :label       "Phone number"}]

       [:input {:type  "submit"
                :value "Create account"
                :class "button attention-btn"}]]

      [:div (session/get-in [:signup-response :message])]]]))