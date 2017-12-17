(ns flupf-web-client.prod
  (:require [flupf-web-client.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
