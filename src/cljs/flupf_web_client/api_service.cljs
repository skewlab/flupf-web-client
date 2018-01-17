(ns flupf-web-client.api-service
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs-http.client :as http]
            [ajax.core :as ajax]
            [cljs.core.async :refer [<! put!]]
            [flupf-web-client.session :as session]
            [secretary.core :as secretary]))



(def api-url "http://localhost:8000/api/")

(defn error-handler [error]
  (println error))

(defn api-get [{endpoint :endpoint
                keyword  :keyword}]
  (ajax/GET (str api-url endpoint)
            {:handler          (fn [response]
                                 (session/put! keyword response))
             :error-handler    #(error-handler %)
             :with-credentials true
             :response-format  :json
             :keywords?        true}))


(defn api-post [{endpoint :endpoint keyword :keyword params :params}]
  (print params)
  (ajax/POST (str api-url endpoint)
             {:params           params
              :handler          (fn [response]
                                  (print response)
                                  (cond (= endpoint "signin")
                                        (do (session/put! :authenticated true)
                                            (secretary/dispatch! "/home"))
                                        (= endpoint "signout")
                                        (do (session/put! :authenticated false)
                                            (secretary/dispatch! "/login")))
                                  (session/put! keyword response))
              :error-handler    #(error-handler %)
              :with-credentials true
              :format           (ajax/json-request-format)
              :response-format  :json
              :keywords?        true}))

(defn authenticate [response-chanel]
  (ajax/GET "http://localhost:8000/api/auth"
            {:handler          (fn [res]
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




