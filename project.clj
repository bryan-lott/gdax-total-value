(defproject gdax-total-value "1.0.0-SNAPSHOT"
  :description "Running ticker of the total USD value of your gdax account(s)"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [clj-http "3.7.0"]
                 [org.clojure/data.json "0.2.6"]]
  :main gdax-total-value.core)
