(ns flupf-web-client.views.home
  (:require [reagent.core :as reagent]
            [reagent-forms.core :refer [bind-fields]]
            [flupf-web-client.session :as session]
            [flupf-web-client.api-service :refer [api-get
                                                  api-post]]
            [flupf-web-client.construct :refer [settings-menu
                                                navigation-menu]]
            [flupf-web-client.components :refer [menu
                                                 up-button
                                                 post]]))

;;----------------------------------------
;;---------       SIDEBAR        ---------
;;----------------------------------------

;;---------       SEARCH         ---------

(defn search [text]
  (if (= (count @text) 0)
    (do (session/remove! :search-response)
        (reset! text ""))
    (api-post {:params   {:searchstring @text}
               :endpoint "search"
               :keyword  :search-response})))

;TODO: Fixa så att den bara söker när man skriver och inte när man suddar! Optimera hela sökgrejen för att göra färre anrop till backend
(defn sidebar-search
  "Search field"
  []
  (fn []
    (let [search-string (reagent/atom nil)]
      [:div
       [:i {:class "fa fa-search"}]
       [:form {:on-submit (fn [event] (.preventDefault event))}
        [:input {:placeholder "search"
                 :class       "sidebar-search"
                 :style       {:fontFamily "FontAwesome"}
                 :on-change   (fn [event]
                                (reset! search-string (-> event .-target .-value))
                                (search search-string))
                 }]]
       [:p @search-string] ;Dropdown menu would be nice
       (map (fn [search-item]
              ^{:key (:id search-item)} [:p (get-in search-item [:alias :String])])
            (session/get :search-response))])))


#_(def search-input
    [:div
     [:input {:field :numeric :id :height}]
     [:div {:field             :typeahead
            :id                :ta
            :input-placeholder "pick a friend"
            :data-source       search-source
            :input-class       "form-control"
            :list-class        "typeahead-list"
            :item-class        "typeahead-item"
            :highlight-class   "highlighted"}]])


#_(defn form []
    (let [text (reagent/atom nil)]
      (fn []
        [:div
         [:div [:h1 "Reagent Form"]]
         [bind-fields search-input text]])))

;;---------     PROFILE-INFO     ---------

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
  (api-get {:endpoint "posts/all"
            :keyword  :user-feed})
  (fn []
    [:div {:class "user-feed"}
     [:div (post-component)]
     (map (fn [feed-post]
            ^{:key feed-post} [post feed-post])
          (session/get :user-feed))]))


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


;;----------------------------------------
;;---------       HOME PAGE      ---------
;;----------------------------------------

(defn home-page []
  (fn []
    [:div
     [sidebar]
     [content]]))