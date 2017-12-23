(ns flupf-web-client.api-service
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]))

(defn api-get [state endpoint]
  (go (let [response (<! (http/get
                           (str "http://localhost:3000/api/" endpoint)))]
        (prn (:body response))
        (swap! state :api-response (:body response))))
  state)
