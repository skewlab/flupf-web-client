(ns flupf-web-client.api-service
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs-http.client :as http]
            [ajax.core :as ajax]
            [cljs.core.async :refer [<! put!]]
            [flupf-web-client.session :as session]
            [secretary.core :as secretary]
            [wscljs.client :as ws]
            [wscljs.format :as fmt]))

(defn new-post [data]
  (session/put! :user-feed (cons data (session/get :user-feed))))

(def handlers {:on-message (fn [e]
                             ;; There exist info about which table, so if we need notifications form other
                             ;; tables this is possible!
                             (new-post (js->clj (.parse js/JSON (.-data e)) :keywordize-keys true )))

               :on-close #(print "Closing a connection")})

(def socket (ws/create "ws://localhost:8000/websocket" handlers))



(def api-url "http://localhost:8000/api/")

(defn error-handler [error]
  (session/put! :error-response-message (get-in error [:response :message])))

(defn api-get [{endpoint :endpoint
                keyword  :keyword}]
  (ajax/GET (str api-url endpoint)
            {:headers {"Access-Control-Allow-Headers" "Content-Type"
                        "Access-Control-Allow-Origin" "*"}
             :handler          (fn [response]
                                 (session/put! keyword response))
             :error-handler    #(error-handler %)
             :with-credentials true
             :response-format  :json
             :keywords?        true}))


(defn api-post [{endpoint :endpoint keyword :keyword params :params}]
  (ajax/POST (str api-url endpoint)
             {:headers {"Access-Control-Allow-Headers" "Content-Type"
                        "Access-Control-Allow-Origin" "*"}
              :params           params
              :handler          (fn [response]
                                  (if (=(:message response) "User added")
                                    (api-post {:endpoint "signin"
                                               :keyword  :signin-response
                                               :params   params}))
                                  (cond (= endpoint "signin")
                                        (do (session/put! :authenticated true)
                                            (secretary/dispatch! "/home"))
                                        (= endpoint "signout")
                                        (do (session/put! :authenticated false)
                                            (secretary/dispatch! "/")))
                                  (session/put! keyword response))
              :error-handler    #(error-handler %)
              :with-credentials true
              :format           (ajax/json-request-format)
              :response-format  :json
              :keywords?        true}))

(defn authenticate [response-chanel]
  (ajax/GET "http://localhost:8000/api/auth"
            {:headers {"Access-Control-Allow-Headers" "Content-Type"
                       "Access-Control-Allow-Origin" "*"}
             :handler          (fn [res]
                                 #_(println res)
                                 (session/put! :authenticated true)
                                 (api-get {:endpoint "users/me"
                                           :keyword  :profile})
                                 (put! response-chanel [:authenticate true]))
             :error-handler    (fn [error]
                                 (println error)
                                 (session/put! :authenticated false)
                                 (put! response-chanel [:authenticate false]))
             :with-credentials true}))




