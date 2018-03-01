(ns flupf-web-client.views.home
  (:require [reagent.core :as reagent]
            [reagent-forms.core :refer [bind-fields]]
            [flupf-web-client.session :as session]
            [flupf-web-client.api-service :refer [api-get
                                                  api-post
                                                  socket]]
            [flupf-web-client.construct :refer [settings-menu
                                                navigation-menu
                                                contact-name]]
            [flupf-web-client.components :refer [menu
                                                 up-button
                                                 post
                                                 sidebar-search]]
            [wscljs.client :as ws]
            [wscljs.format :as fmt]))

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
  (let [post (reagent/atom "")]
    (fn []
      [:div
       [:form {:class     "post-form"
               :on-submit (fn [e] (.preventDefault e)
                            (reset! post ""))}
        [:textarea {:id          "post"
                    :name        "post"
                    :type        "text"
                    :value       @post
                    :placeholder "What are you thinking about?"
                    :on-change   #(reset! post (-> % .-target .-value))}]
        [:button {:type     "sumbit"
                  :class    "post-btn"
                  :on-click (fn [event]
                              (api-post
                                {:endpoint "posts"
                                 :keyword  :post-response
                                 :params   {:userid  (session/get-in [:profile :id])
                                            :content @post}}))}
         ;; SVG button to post
         [:svg {:version           "1.1"
                :id                "Layer_1"
                :xmlns             "http://www.w3.org/2000/svg"
                :x                 "0px"
                :y                 "0px"
                :width             "55.216px"
                :height            "55.216px"
                :viewBox           "0 0 55.216 55.216"
                :enable-background "new 0 0 55.216 55.216"}
          [:g
           [:circle {:fill "#FF7381"
                     :cx   "27.608"
                     :cy   "27.608"
                     :r    "27.608"}]]
          [:polyline {:fill              "none"
                      :stroke            "#FFFFFF"
                      :stroke-width      "2"
                      :stroke-miterlimit "10"
                      :points            "28.566,34.168 24.839,39.497
			21.775,32.387 8.016,33.429 35.797,17.096 41.824,13.552 36.566,36.516 21.775,32.387 31.875,25.66"}]]]]])))

;;---------     USER-FEED    ---------

(defn user-feed []
  [:div {:class "user-feed"}
   [:div [post-component]
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