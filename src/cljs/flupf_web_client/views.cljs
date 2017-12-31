(ns flupf-web-client.views
  (:require [flupf-web-client.api-service :as api]
            [reagent.core :as reagent :refer [atom]]
            [secretary.core :as secretary :include-macros true]
            [accountant.core :as accountant]))



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
  [:input {:id           id
           :name         name
           :placeholder  placeholder
           :class        "form-control"
           :type         type
           :required     ""
           :autocomplete "email"
           :value        @value
           :on-change    #(reset! value (-> % .-target .-value))}])


(defn login-form
  "Login form"
  []
  (let [email-address (atom nil)
        password (atom nil)
        credentials (atom nil)]
    [:div {:class "sign-in"}
     [:h1 "Sign in"]
     [:form {:class     "login-form"
             :on-submit (fn [e] (.preventDefault e))}
      [input-element "email" "email" "email" "email" email-address]
      [input-element "password" "password" "password" "password" password]
      [:input {:type     "submit"
               :value    "sign in"
               :on-click (fn []
                           (api/api-post credentials "signin" {:email    @email-address
                                                               :password @password})
                           (secretary/dispatch! "/home"))}]]
     [:span {:class "full-link-wrapper"}
      [:a {:href "/"} "Forgot your password?"]]]))


(defn start-page
  "First view of the webpage"
  []
  [:div (login-form)])


;--- User feed ---

(defn user-feed [state]
  (api/api-get state "posts/all")
  (fn []
    [:div {:class "user-feed"}
     (map (fn [post]
            ^{:key post} [:div {:class "post"}
                          [:div {:class "post-author"}
                           [:p "ID: " (:id post)]]
                          [:div {:class "post-content"}
                           [:h2 (:content post)]]
                          "author: " (:userid post)
                          [:br]
                          "Date: " (:dade_created post)
                          [:br]
                          [:p "Ups: " (:ups post)]])
          (:posts/all @state))]))


;--- Profile sidebar ---

(defn profile-sidebar [state]
  (let [user "users/me"]
    (api/api-get state user)
    (fn []
      [:div {:class "profile-info"}

       [:img {:src (get-in @state [(keyword user) :avatar :String])
              :alt "no avatar available"}]
       [:h2 {:class "user-profile-name"} (get-in @state [(keyword user) :alias :String])]
       [:p {:class "user-description"} (get-in @state [(keyword user) :description :String])]
       [:ul
        [:li (get-in @state [(keyword user) :website :String])]
        [:li (get-in @state [(keyword user) :phonenumber :String])]]])))


(defn contacts-list [state]
  (let [contacts "my-contacts"]
    (api/api-get state contacts)
    (fn []
      [:div {:class "contacts-list"}
       (map (fn [contact]
              ^{:key contact} [:div {:class "contact"}
                               [:h2 (get-in contact [:alias :String])]]
              ) ((keyword contacts) @state))
       ])))


(defn home-page [state]
  [:div {:class "wrapper"}
   [header]
   [:div {:class "user-home-view"}
    [profile-sidebar state]
    [:div {:class "feed-field"}
     [:div {:class "left-column"}
      [user-feed state]]
     [:div {:class "right-column"}
      [contacts-list state]]]]])
