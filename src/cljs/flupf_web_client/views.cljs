(ns flupf-web-client.views
  (:require [flupf-web-client.api-service :as api]
            [reagent.core :as reagent]
            [secretary.core :as secretary :include-macros true]
            [accountant.core :as accountant]
            [flupf-web-client.construct :refer [settings-menu
                                                navigation-menu]]
            [flupf-web-client.session :as session]
            [flupf-web-client.components :refer [menu]]))



;--- HEADER ---

(defn header []
  [:header
   [:a {:href       "/my-profile"
        :class-name "link-right link"}
    "My Profile"]
   [:a {:href       "/home"
        :class-name "link-left link"}
    "Start"]])

;--- LOGIN PAGE ---
(defn input-element
  "An input element which updates its value on change"
  [{id :id name :name type :type placeholder :placeholder value :value label :label required? :required}]
  [:div
   [:strong [:label label (if required? "*")]]
   [:input {:id          id
            :name        name
            :placeholder placeholder
            :class       "form-control"
            :type        type
            :required    required?
            :value       @value
            :on-change   #(reset! value (-> % .-target .-value))}]])

(defn login-page
  "Login form"
  []
  (let [email-address (reagent/atom nil)
        password (reagent/atom nil)
        credentials (reagent/atom nil)]
    [:div {:class "sign-in"}
     [:h1 "Sign in"]
     [:form {:class     "login-form"
             :on-submit (fn [e] (.preventDefault e))}
      [input-element {:id "email" :name "email" :type "email" :placeholder "email" :value email-address}]
      [input-element {:id "password" :name "password" :type "password" :placeholder "password" :value password}]
      [:input {:type     "submit"
               :value    "sign in"
               :class    "button"
               :on-click (fn []
                           (api/api-post {:endpoint "signin"
                                          :keyword  :signin-response
                                          :params   {:email    @email-address
                                                     :password @password}}))}]]
     [:span {:class "full-link-wrapper"}
      [:a {:href "/"} "Forgot your password?"]]]))


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
                          (api/api-post
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


;--- Up button ---
(defn up-button
  ""
  [number]
  [:span {:class "up-button .active"}
   [:i {:class       "fa fa-arrow-circle-up"
        :aria-hidden true}]
   number])


;--- Post component ---


(defn post-component []
  (let [post (reagent/atom nil)]
    [:div
     [:form {:class     "post-form"
             :on-submit (fn [e] (.preventDefault e))}
      [:textarea {:id          "post"
                  :name        "post"
                  :placeholder "What are you thinking about?"
                  :type        "text"
                  :on-change   #(reset! post (-> % .-target .-value))}]
      [:button {:type     "sumbit"
                :class    "post-btn"
                :on-click #(api/api-post
                             {:endpoint "posts"
                              :keyword  :post-response
                              :params   {:userid  (session/get-in [:profile :id])
                                         :content @post}})} "Post"]]]))


;--- User feed ---
(defn post
  "view-component for a post, takes info returns html object"
  [post-info]
      [:div {:class "post"}
       [:div {:class "post-author"}
        [:img {:src (:avatar post-info)}]
        [:div
         [:h2 {:class "post-author-alias"} (:alias post-info)]
         [:small {:class "post-author-description"} "maybe also fetch description here"]]]
       [:div {:class "post-content"}
        [:h3 (:content post-info)]]
       [:br]
       "Date: " (:date_created post-info)
       [:br]
       [up-button (:ups post-info)]]
      )

(defn user-feed []
  (api/api-get {:endpoint "posts/all"
                :keyword  :user-feed})
  (fn []
    [:div {:class "user-feed"}
     [:div (post-component)]
     (map (fn [feed-post]
            ^{:key feed-post} [post feed-post])
          (session/get :user-feed))]))


;--- sidebar ---

;; Search
(defn sidebar-search
  ""
  []
  [:div
   [:i {:class "fa fa-search"}]
   [:input {:placeholder "search"
            :class       "sidebar-search"
            :style {:fontFamily "FontAwesome"}}]])

(defn profile-info []
  [:div {:class "profile-info"}
   [:img {:src (session/get-in [:profile :avatar :String])
          :alt "no avatar available"}]
   [:h2 (session/get-in [:profile :alias :String])]
   [:ul {:class "profile-info"}
    [:li (session/get-in [:profile :description :String])]
    [:li (session/get-in [:profile :website :String])]
    [:li (session/get-in [:profile :phonenumber :String])]]])


(defn sidebar []
  [:div {:class "side-bar"}
   [sidebar-search]
   [profile-info]
   [menu (navigation-menu)]
   [menu (settings-menu)]])


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

(defn content
  "Content view excludes siedbar"
  []
  [:div {:class "content"}
   ;; Should contain the feed
   [:div {:class "left-content-column"}
    [user-feed]]
   [:div {:class "right-content-column"}
    [contacts-list]]
   ;; Should contain the right field
   ])


(defn home-page []
  (fn []
    [:div
     [header]
     [sidebar]
     [content]]))


(defn start-page
  "First view of the webpage"
  [state]
  [:div
   [:p "allradey have an account?"]
   [:a {:href "#/login"} "LOGIN HERE"]
   (sign-up-page)])