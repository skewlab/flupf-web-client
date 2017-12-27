(ns flupf-web-client.api-service
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]))

(defn authenticated? []
  (let [state (atom nil)]
  (go (let [response (<! (http/get "http://localhost:3000/api/auth"))]
        (prn (:status response))
        (swap! state :api-response (:status response))))
  state))

(defn api-get [state endpoint]
  (go (let [response (<! (http/get
                           (str "http://localhost:3000/api/" endpoint)))]
        (prn (:status response))
        (swap! state :api-response (:body response))))
  state)

(defn api-post [state endpoint params]
  (go (let [response (<! (http/post
                           (str "http://localhost:3000/api/" endpoint)
                           {:json-params params}))]
        (prn (js/JSON.stringify (clj->js params)) "i api post")
        (prn (:body response))
        (swap! state :api-response (:body response))))
  state)
