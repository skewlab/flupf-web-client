(ns flupf-web-client.views
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [flupf-web-client.api-service :as api]
            [reagent.core :as reagent :refer [atom]]
            [secretary.core :as secretary :include-macros true]
            [accountant.core :as accountant]
            [cljs.core.async :refer [<! put!]]
            [cljs-http.client :as http]
            [ajax.core :refer [GET POST PUT]]))


(defonce app-state (atom {}))

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
   [:a {:href       "/start"
        :class-name "link-left link"}
    "Start"]]
  )

;--- LOGIN PAGE ---
(defn sign-in
  "Sign in a user"
  []
  (println "::=> Signing in"))


(defn input-element
  "An input element which updates its value on change"
  [id name type value]
  [:input {:id        id
           :name      name
           :class     "form-control"
           :type      type
           :required  ""
           :value     @value
           :on-change #(reset! value (-> % .-target .-value))}])

(defn email-input
  [email-address-atom]
  (input-element "email" "email" "email" email-address-atom))


(defn login-form
  "Login form"
  []
  (let [email-address (atom nil)]
    [:div
     [:form {:class "login-form"}
      [email-input email-address]
      [:input {:type        "password"
               :placeholder "password"}]
      [:input {:type  "submit"
               :value "sign in"}]]
     ;[:div "epost: " @email-address]
     ]))


(defn start-page
  "First view of the webpage"
  []
  [:div
   (header)
   [:h1 "Start page"]
   (login-form)
   ])


;--- User feed ---

(defn user-feed []
  (let [state (api/api-get app-state "posts/all")]
  [:div {:class "profile-feed"}
   [:div {:class "post"}
    [:p "ID:"]
    [:h2 "This is a mock post"]
    "author:"
    [:br]
    "Date: "
    [:br]
    [:p "Ups: "]]]))


;--- Profile sidebar ---

(defn profile-sidebar [state]
  (let [state (api/api-get app-state "users/aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")]
    (fn []
      [:div {:class "profile-info"}
       [:img {:src (get-in @state [:avatar :String])
              :alt "no avatar available"}]
       [:h2 (get-in @state [:alias :String])]
       [:ul
        [:li (get-in @state [:description :String])]
        [:li (get-in @state [:website :String])]
        [:li (get-in @state [:phonenumber :String])]]])))


(defn contacts-list []
  [:div {:class "contacts-list"}
   [:div {:class "contact"}
    [:h2 "Alias of contact"]]]
  )


(defn home-page [state]
  [:div
   [header]
   [profile-sidebar]
   [user-feed]
   [contacts-list]])
