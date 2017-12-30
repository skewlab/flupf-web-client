(ns flupf-web-client.api-service
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]))


(defn authenticate [state]
  (go (let [response (<! (http/get
                           (str "http://localhost:8000/api/auth")))]
        (:status response))))


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
