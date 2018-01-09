(ns flupf-web-client.construct)

(defn create-state []
  "initiate state"
  {:user nil
   :active-page :home
   :authenticated false })
