(ns gdax-total-value.api
  (:require [clj-http.client :as http]
            [clojure.string :refer [join]])
  (:import [java.util Base64]
           [javax.crypto.spec SecretKeySpec]
           [javax.crypto Mac]
           [java.net URL]))

(def endpoint "https://api.gdax.com")

(defonce hmac (javax.crypto.Mac/getInstance "HmacSHA256"))

(defn encode [bs]
  (-> (Base64/getEncoder) (.encodeToString bs)))

(defn decode [s]
  (-> (Base64/getDecoder) (.decode s)))

(defn sign [key message]
  (.init hmac (SecretKeySpec. key "HmacSHA256"))
  (.doFinal hmac message))

(defn wrap-coinbase-auth [client]
  (fn [req]
    (let [sk (-> req :CB-ACCESS-SECRET decode)
          timestamp (format "%f" (/ (System/currentTimeMillis) 1000.0))
          sign-message (str timestamp
                            (-> req :method name .toUpperCase)
                            (-> req :url (URL.) .getPath)
                            (:body req))
          headers {:CB-ACCESS-KEY (:CB-ACCESS-KEY req)
                   :CB-ACCESS-SIGN (->> sign-message .getBytes (sign sk) encode)
                   :CB-ACCESS-TIMESTAMP timestamp
                   :CB-ACCESS-PASSPHRASE (:CB-ACCESS-PASSPHRASE req)}]
      (client (update-in req [:headers] merge headers)))))

(defmacro with-coinbase-auth [& body]
  `(http/with-middleware (conj http/default-middleware #'wrap-coinbase-auth)
     ~@body))

(defn get-product [product-kwd]
  (:body (http/get (str endpoint "/products/" (name product-kwd) "-USD/ticker"))))

(defn send-request-auth [method endpoint path credentials]
  (with-coinbase-auth
    (let [url (join "/" [endpoint path])]
      (method url credentials))))

(defn get-accounts [credentials]
  (send-request-auth http/get endpoint "accounts" credentials))
