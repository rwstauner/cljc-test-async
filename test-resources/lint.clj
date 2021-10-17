(ns lint
  (:require
    [clojure.test :as t]
    [net.r4s6.test-async :as a :include-macros true]))

(defn foo [])

(t/deftest redefs
  (t/testing "local"
    (t/is (= (foo) "bar"))

    (a/async-redefs
      [foo] ; error: with-redefs binding vector requires even number of forms
      done  ; warning: unused binding done
      (t/is (= (foo) "baz")))))
