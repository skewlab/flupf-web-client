(ns flupf-web-client.api-service
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs-http.client :as http]
            [ajax.core :as ajax]
            [cljs.core.async :refer [<! put!]]))




(defn authenticate [state response-chanel]
  (ajax/GET "http://localhost:8000/api/auth"
            {:handler          (fn [res]
                                 (println res)
                                 (swap! state assoc :authenticated true)
                                 (put! response-chanel [state :authenticate true]))
             :error-handler    (fn [error]
                                 (println error)
                                 (swap! state assoc :authenticated false)
                                 (put! response-chanel [state :authenticate false]))
             :with-credentials true}))


(defn api-get [state endpoint]
  (go (let [response (<! (http/get
                           (str "http://localhost:8000/api/" endpoint)))]
        (swap! state assoc (keyword endpoint) (:body response)))))


(defn api-post [state endpoint params]
  (go (let [response (<! (http/post
                           (str "http://localhost:8000/api/" endpoint)
                           {:json-params params}))]
        (prn (js/JSON.stringify (clj->js params)) "i api post")
        (prn (:body response))
        (swap! state :api-response (:body response))))
  state)
