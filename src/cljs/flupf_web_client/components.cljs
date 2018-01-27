(ns flupf-web-client.components
  (:require [reagent.core :as reagent]
            [flupf-web-client.session :as session]
            [flupf-web-client.api-service :refer [api-get
                                                  api-post]]
            [flupf-web-client.construct :refer [settings-menu
                                                navigation-menu
                                                contact-menu
                                                contact-name]]))

;; Put all components in this file


;;----------------------------------------
;;---------         MENU         ---------
;;----------------------------------------

(defn menu-link
  "Menu link"
  [menu-item]
  [:li {:on-click (:action menu-item)}
   [:i {:aria-hidden "true"
        :class       (:icon menu-item)}]
   [:span (:name menu-item)]])

(defn menu
  "Menu template"
  [menu-items]
  [:ul {:class "menu"}
   (map (fn [menu-item]
          ^{:key (:id menu-item)}
          [menu-link menu-item]) menu-items)])


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
                                (search search-string))}]]
       (map (fn [search-item]
              ^{:key (:id search-item)}
              [contact-name (:id search-item)
               [:p (get-in search-item [:alias :String])]])
            (session/get :search-response))])))

;;---------     PROFILE-INFO     ---------

(defn profile-info [profile]
  [:div {:class "profile-info"}
   [:img {:src (get-in profile [:avatar :String])
          :alt "no avatar available"}]
   [contact-name (:id profile) [:h2 (get-in profile [:alias :String])]]
   [:ul {:class "profile-info"}
    [:li (get-in profile [:description :String])]
    [:li (get-in profile [:website :String])]
    [:li (get-in profile [:phonenumber :String])]]])


(defn sidebar [{profile :profile class :class}]
  [:div {:class class}
   (if (not= type :user) [sidebar-search] nil)
   [profile-info profile]
   [menu (navigation-menu)]
   [menu (settings-menu)]])

(defn contact-sidebar [{profile :profile class :class}]
  [:div {:class "user-side-bar"}
   [profile-info profile]
   [menu (contact-menu)]])



;;----------------------------------------
;;---------       UP BUTTON      ---------
;;----------------------------------------

(defn up-button
  ""
  [number]
  [:span {:class "up-button .active"}
   [:i {:class       "fa fa-arrow-circle-up"
        :aria-hidden true}]
   number])


;;----------------------------------------
;;---------   POST (substantive) ---------
;;----------------------------------------

(defn post
  "view-component for a post, takes info returns html object"
  [post-info]
  [:div {:class "post"}
   [:div {:class "post-author"}
    [:img {:src (:avatar post-info)}]
    [:div
     [contact-name (:userid post-info)
      [:h2 {:class "post-author-alias"} (:alias post-info)]]
     [:small {:class "post-author-description"} "maybe also fetch description here"]]]
   [:div {:class "post-content"}
    [:p (:content post-info)]]
   [:br]
   [:small (:date_created post-info)]
   [:br]
   [up-button (:ups post-info)]]
  )

;;---------     USER-FEED    ---------

(defn user-feed [feed]
  [:div {:class "user-feed"}
   (map (fn [feed-post]
          ^{:key feed-post} [post feed-post])
        feed)])


;;----------------------------------------
;;---------     INPUT ELEMENT    ---------
;;----------------------------------------

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
