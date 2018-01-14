(ns flupf-web-client.construct
  (:require [reagent.core :as reagent]))

(defn create-state []
  "initiate state"
  (reagent/atom {:active-page   :login
                 :authenticated nil}))
