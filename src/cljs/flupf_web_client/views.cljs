(ns flupf-web-client.views
  (:require [flupf-web-client.api-service :as api]
            [reagent.core :as reagent]
            [secretary.core :as secretary :include-macros true]
            [accountant.core :as accountant]
            [flupf-web-client.session :as session]))



;--- HEADER ---

(defn sign-out [username]
  (api/api-post {:endpoint "signout"
                 :keyword  :signout-response
                 :params   {}})
  (println "signed out"))


(defn header []
  [:header
   [:a {:on-click #(sign-out "Jonas")
        :class    "link-right link"}
    "Sign out"]
   [:a {:href       "/my-profile"
        :class-name "link-right link"}
    "My Profile"]
   [:a {:href       "/home"
        :class-name "link-left link"}
    "Start"]])

;--- LOGIN PAGE ---
(defn input-element
  "An input element which updates its value on change"
  [id name type placeholder value]
  [:input {:id          id
           :name        name
           :placeholder placeholder
           :class       "form-control"
           :type        type
           :required    ""
           :value       @value
           :on-change   #(reset! value (-> % .-target .-value))}])


(defn login-form
  "Login form"
  [state]
  (let [email-address (reagent/atom nil)
        password (reagent/atom nil)
        credentials (reagent/atom nil)]
    [:div {:class "sign-in"}
     [:h1 "Sign in"]
     [:form {:class     "login-form"
             :on-submit (fn [e] (.preventDefault e))}
      [input-element "email" "email" "email" "email" email-address]
      [input-element "password" "password" "password" "password" password]
      [:input {:type     "submit"
               :value    "sign in"
               :on-click (fn []
                           (api/api-post {:endpoint "signin"
                                          :keyword  :signin-response
                                          :params   {:email    @email-address
                                                     :password @password}}))}]]
     [:span {:class "full-link-wrapper"}
      [:a {:href "/"} "Forgot your password?"]]]))


(defn login-page
  "First view of the webpage"
  [state]
  [:div (login-form state)])

(defn sign-up-page []
  (let [sign-up-info (reagent/atom {:alias           (reagent/atom nil)
                                    :email           (reagent/atom nil)
                                    :password        (reagent/atom nil)
                                    :repeat-password (reagent/atom nil)})]
    [:div {:class "sign-up"}
     [:h1 "Sign up"]
     [:form {:class     "signup-form"
             :on-submit (fn [e] (.preventDefault e))}
      [input-element "alias" "alias" "alias" "alias" (:alias @sign-up-info)]
      [input-element "email" "email" "email" "email" (:email @sign-up-info)]
      [input-element "password" "password" "password" "password" (:password @sign-up-info)]
      [input-element "repeat-password" "repeat-password" "repeat-password" "repeat-password" (:repeat-password @sign-up-info)]
      [:input {:type     "submit"
               :value    "sign up"
               :on-click (fn []
                           (api/api-post
                             {:endpoint "users"
                              :keyword  :signup-response
                              :params   {:email    @(:email @sign-up-info)
                                         :alias    @(:alias @sign-up-info)
                                         :password @(:password @sign-up-info)
                                         ;:repeat-password @(:repeat-password @sign-up-info)
                                         }}))}]]
     [:span {:class "full-link-wrapper"}]]))


;--- Post component ---
(defn post-component []
  (let [post (reagent/atom nil)]
    [:div {:class "post-component"}
     [:form {:class     "post-component"
             :on-submit (fn [e] (.preventDefault e))}
      [input-element "post-component" "post-component" "text" "Whats on yoour mind...?" post]
      [:button {:type     "sumbit"
                :class    "post-btn"
                :on-click #(api/api-post {:endpoint "posts"
                                          :keyword  :post-response
                                          :params   {:userid  (session/get-in [:profile :id])
                                                     :content @post}})} "post"]]]))


;--- User feed ---

(defn user-feed []
  (api/api-get {:endpoint "posts/all"
                :keyword  :user-feed})
  (fn []
    [:div {:class "profile-feed"}
     [:div (post-component)]
     (map (fn [post]
            ^{:key post} [:div {:class "post"}
                          [:p "ID: " (:id post)]
                          [:h2 (:content post)]
                          "author: " (:userid post)
                          [:br]
                          "Date: " (:dade_created post)
                          [:br]
                          [:p "Ups: " (:ups post)]])
          (session/get :user-feed))]))


;--- Profile sidebar ---

(defn profile-sidebar []
  [:div {:class "profile-info"}
   [:img {:src (session/get-in [:profile :avatar :String])
          :alt "no avatar available"}]
   [:h2 (session/get-in [:profile :alias :String])]
   [:ul
    [:li (session/get-in [:profile :description :String])]
    [:li (session/get-in [:profile :website :String])]
    [:li (session/get-in [:profile :phonenumber :String])]]])


(defn contacts-list []
  (let [contacts "my-contacts"]
    (api/api-get {:endpoint contacts
                  :keyword  :contacts})
    (fn []
      [:div {:class "contacts-list"}
       (map (fn [contact]
              ^{:key contact} [:div {:class "contact"}
                               [:h2 (get-in contact [:alias :String])]]
              ) (session/get :contacts))])))


(defn home-page []
  (fn []
    [:div
     [header]
     [profile-sidebar]
     [user-feed]
     [contacts-list]]))

