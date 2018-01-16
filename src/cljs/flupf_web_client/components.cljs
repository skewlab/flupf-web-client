(ns flupf-web-client.components)

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
     [:h2 {:class "post-author-alias"} (:alias post-info)]
     [:small {:class "post-author-description"} "maybe also fetch description here"]]]
   [:div {:class "post-content"}
    [:h3 (:content post-info)]]
   [:br]
   "Date: " (:date_created post-info)
   [:br]
   [up-button (:ups post-info)]]
  )


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
