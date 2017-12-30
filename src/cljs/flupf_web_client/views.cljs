(ns flupf-web-client.views
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [flupf-web-client.api-service :as api]
            [reagent.core :as reagent :refer [atom]]
            [secretary.core :as secretary :include-macros true]
            [accountant.core :as accountant]
            [cljs.core.async :refer [<! put!]]
            [cljs-http.client :as http]))



;--- HEADER ---

(defn sign-out [username]
  (println username "signed out"))


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
    "Start"]]
  )

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
  []
  (let [email-address (atom nil)
        password (atom nil)
        credentials (atom nil)]
    [:div
     [:form {:class "login-form"}
      [input-element "email" "email" "email" "email" email-address]
      [input-element "password" "password" "password" "password" password]
      [:input {:type     "button"
               :value    "sign in"
               :on-click (fn []
                           (api/api-post credentials "signin" {:email    @email-address
                                                               :password @password})
                           (secretary/dispatch! "/home"))}]]]))


(defn start-page
  "First view of the webpage"
  []
  [:div
   (header)
   [:h1 "Start page"]
   (login-form)
   ])


;--- User feed ---

(defn user-feed [state]
  (api/api-get state "posts/all")
  (fn []
    [:div {:class "profile-feed"}
     (map (fn [post]
            ^{:key post} [:div {:class "post"}
                          [:p "ID: " (:id post)]
                          [:h2 (:content post)]
                          "author: " (:userid post)
                          [:br]
                          "Date: " (:dade_created post)
                          [:br]
                          [:p "Ups: " (:ups post)]])
          (:posts/all @state))]))


;--- Profile sidebar ---

(defn profile-sidebar [state]
  (let [user "users/aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"]
    (api/api-get state user)
    (fn []
      [:div {:class "profile-info"}
       [:img {:src (get-in @state [(keyword user) :avatar :String])
              :alt "no avatar available"}]
       [:h2 (get-in @state [(keyword user) :alias :String])]
       [:ul
        [:li (get-in @state [(keyword user) :description :String])]
        [:li (get-in @state [(keyword user) :website :String])]
        [:li (get-in @state [(keyword user) :phonenumber :String])]]])))


(defn contacts-list [state]
  (let [contacts "my-contacts"]
    (api/api-get state contacts)
    (prn @state)
    (fn []
      [:div {:class "contacts-list"}
       (map (fn [contact]
              ^{:key contact} [:div {:class "contact"}
                               [:h2 (get-in contact [:alias :String])]]
              ) ((keyword contacts) @state))
       ])))


(defn home-page [state]
  [:div
   [header]
   [profile-sidebar state]
   [user-feed state]
   [contacts-list state]])
