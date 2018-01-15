(ns flupf-web-client.components)

;; Put all components in this file


;;----------------------------------------
;;---------         MENU         ---------
;;----------------------------------------

(defn menu-link
  "Menu link"
  [menu-item]
  [:li {:on-click (:action menu-item)
        :key      (:id menu-item)}
   [:i {:aria-hidden "true"
        :class       (:icon menu-item)}]
   [:span (:name menu-item)]])

(defn menu
  "Menu template"
  [menu-items]
  [:ul {:class "menu"}
   (map (fn [menu-item]
          [menu-link menu-item]) menu-items)])
