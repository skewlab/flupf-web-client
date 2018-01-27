(ns flupf-web-client.views.home
  (:require [reagent.core :as reagent]
            [reagent-forms.core :refer [bind-fields]]
            [flupf-web-client.session :as session]
            [flupf-web-client.api-service :refer [api-get
                                                  api-post]]
            [flupf-web-client.construct :refer [settings-menu
                                                navigation-menu
                                                contact-name]]
            [flupf-web-client.components :refer [menu
                                                 up-button
                                                 post
                                                 sidebar-search]]))

;;----------------------------------------
;;---------       SIDEBAR        ---------
;;----------------------------------------

;;---------     PROFILE-INFO     ---------

(defn profile-info [profile]
  [:div {:class "profile-info"}
   [:img {:src (get-in profile [:avatar :String])
          :alt "no avatar available"}]
   [:h2 (get-in profile [:alias :String])]
   [:ul {:class "profile-info"}
    [:li (get-in profile [:description :String])]
    [:li (get-in profile [:website :String])]
    [:li (get-in profile [:phonenumber :String])]]])


(defn sidebar [profile]
  [:div {:class "side-bar"}
   [sidebar-search]
   [profile-info profile]
   [menu (navigation-menu)]
   [menu (settings-menu)]])


;;----------------------------------------
;;---------       CONTENT        ---------
;;----------------------------------------

;;---------       POST (verb)    ---------

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
                :on-click #(api-post
                             {:endpoint "posts"
                              :keyword  :post-response
                              :params   {:userid  (session/get-in [:profile :id])
                                         :content @post}})} "Post"]]]))

;;---------     USER-FEED    ---------

(defn user-feed []
  [:div {:class "user-feed"}
   [:div (post-component)
    (map (fn [feed-post]
           ^{:key feed-post} [post feed-post])
         (session/get :user-feed))]])


;;---------    CONTACT-LIST     ---------

;TODO: Build a component for contact
(defn contacts-list []
  (let [contacts "my-contacts"]
    (api-get {:endpoint contacts
              :keyword  :contacts})
    (fn []
      [:div {:class "contacts-list"}
       (map (fn [contact]
              ^{:key contact} [:div {:class "contact"}
                               [:a {:href (str "/#/user/" (:id contact))}
                                [:h2 (get-in contact [:alias :String])]]]
              ) (session/get :contacts))])))


(defn content
  "Content view excludes siedbar"
  [userid]
  (api-get {:endpoint (str "feed/" userid)
            :keyword  :user-feed})
  [:div {:class "content"}
   ;; Should contain the feed
   [:div {:class "left-content-column"}
    [user-feed]]
   [:div {:class "right-content-column"}
    [contacts-list]]
   ;; Should contain the right field
   ])


;;----------------------------------------
;;---------       HOME PAGE      ---------
;;----------------------------------------

(defn home-page []
  (api-get {:endpoint "users/me"
            :keyword  :profile})
  (fn []
    [:div
     [sidebar (session/get :profile)]
     [content (session/get-in [:profile :id])]]))