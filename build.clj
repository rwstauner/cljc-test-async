(ns build
  (:refer-clojure :exclude [test])
  (:require [org.corfield.build :as bb]))

(def lib 'net.r4s6/test-async)
(def version "0.3.0-SNAPSHOT")

; FIXME: bump version and tag
(defn test "Run the tests." [opts]
  (bb/run-tests opts))

(defn ci "Run the CI pipeline of tests (and build the JAR)." [opts]
  (-> opts
      (assoc :lib lib :version version)
      (bb/run-tests)
      (bb/clean)
      (bb/jar)))

(defn install "Install the JAR locally." [opts]
  (-> opts
      (assoc :lib lib :version version)
      (bb/install)))

(defn deploy "Deploy the JAR to Clojars." [opts]
  (-> opts
      (assoc :lib lib :version version)
      (bb/deploy)))
