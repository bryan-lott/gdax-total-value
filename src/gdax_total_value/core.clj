(ns gdax-total-value.core
  (:require [clojure.data.json :as json]
            [gdax-total-value.api :as gdax-api])
  (:gen-class))

(def credentials
  {:CB-ACCESS-PASSPHRASE (System/getenv "CB_ACCESS_PASSPHRASE")
   :CB-ACCESS-KEY (System/getenv "CB_ACCESS_KEY")
   :CB-ACCESS-SECRET (System/getenv "CB_ACCESS_SECRET")})

(defn get-current-prices []
  "Get current prices for gdax products BTC, ETH, LTC"
  (apply conj {}
    (map
      (fn [product-kwd]
        {product-kwd (read-string ((json/read-str (gdax-api/get-product product-kwd)) "price"))})
      [:BTC :ETH :LTC])))

(defn extract-current-balances [account-response-body]
  "Get the current balance of each coin in the account in the coin."
  (->> (map #(select-keys % ["currency" "balance"]) account-response-body)
       (map (fn [m] {(keyword (m "currency")) (read-string (m "balance"))}))
       (apply conj {})))

(defn calc-total-value [current-prices current-balances]
  "Calculate current payout value for each cryptocoin to USD"
  (let [usd (:USD current-balances)
        btc->usd (* (:BTC current-prices)
                    (:BTC current-balances))
        eth->usd (* (:ETH current-prices)
                    (:ETH current-balances))
        ltc->usd (* (:LTC current-prices)
                    (:LTC current-balances))]
    {:USD (format "%.2f" usd)
     :BTC (format "%.2f" btc->usd)
     :ETH (format "%.2f" eth->usd)
     :LTC (format "%.2f" ltc->usd)
     :total (format "%.2f" (+ usd btc->usd eth->usd ltc->usd))}))

(defn now [] (new java.util.Date))

(defn pretty-print-vals [total-values]
  (println "=========" (now) "==========")
  (doseq [kwd [:USD :BTC :ETH :LTC]]
    (println (name kwd) ": $" (kwd total-values) " "))
  (println "--------------------")
  (println "Total: " (:total total-values)))

(defn run []
  (->> (gdax-api/get-accounts credentials)
       (:body)
       (json/read-str)
       (extract-current-balances)
       (calc-total-value (get-current-prices))
       (pretty-print-vals)))

(defn -main
  [& args]
  (while true
    (run)
    (Thread/sleep (* 60 1000))))
