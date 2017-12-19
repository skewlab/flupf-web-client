(ns flupf-web-client.api-service
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]))

(defn api-get [endpoint]
  (go (let [response (<! (http/get (str "http://localhost:8000/api/" endpoint)
                                   {:with-credentials? false
                                    :headers           {"Access-Control-Allow-Origin" "*"}}))]
        (prn (:status response))
        (prn (:body response))
        (:body response))))


(defn api-post [endpoint data]
  (go (let [response (<! (http/post (str "http://localhost:8000/api/" endpoint)
                                   {:json-params data}))]
        (prn (:status response))
        (prn (:body response)))))