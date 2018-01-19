(ns flupf-web-client.views.user-view
  (:require [reagent.core :as reagent]
            [reagent-forms.core :refer [bind-fields]]
            [flupf-web-client.session :as session]
            [flupf-web-client.api-service :refer [api-get
                                                  api-post]]
            [flupf-web-client.components :refer [menu
                                                 up-button
                                                 post
                                                 sidebar
                                                 user-feed]]))


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
  []
  [:div {:class "content"}
   ;; Should contain the feed
   [:div {:class "left-content-column"}
    [user-feed (session/get :user-feed)]]
   [:div {:class "right-content-column"}
    [sidebar {:class   "user-sidebar"
              :profile (session/get :user-profile)
              :type :user}]]
   ;; Should contain the right field
   ])


;;----------------------------------------
;;---------       HOME PAGE      ---------
;;----------------------------------------

;(defn home-page []
;  (api-get {:endpoint "users/me"
;            :keyword  :profile})
;  (fn []
;    [:div
;     [sidebar (session/get :profile)]
;     [content (session/get-in [:profile :id])]]))
;;----------------------------------------
;;---------       User PAGE      ---------
;;----------------------------------------

(defn user-page [id]
  (println "i USERPAGE!")
  (fn []
    [:div
     [sidebar {:profile (session/get :profile)
               :class   "side-bar"
               :type :profile}]
     [content]]))
