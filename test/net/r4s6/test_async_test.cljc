(ns net.r4s6.test-async-test
  (:require
    [clojure.string :as string]
    [clojure.test :as t]
    [net.r4s6.test-async :as a :include-macros true]))

(t/deftest async
  (t/testing "async tests run"
    (a/async
      done
      #?(:clj (future
                (Thread/sleep 1000)
                (t/is (= 1 1))
                (done))
         :cljs (do
                 (prn :js)
                 (js/setTimeout
                   (fn []
                     (t/is (= 1 1))
                     (done))
                   1000) [])))))

(defn- foo
  []
  "bar")

(t/deftest redefs
  (t/testing "local"
    (t/is (= (foo) "bar"))

    (a/async-redefs
      [foo (constantly "baz")]
      done
      (t/is (= (foo) "baz"))
      (done)
      (t/is (= (foo) "bar")))

    (t/is (= (foo) "bar")))

  (t/testing "multiple, alias and full ns"
    (a/async-redefs
      [string/join (fn [s xs]
                     (apply str s (reverse xs)))
       clojure.string/replace-first (constantly "foo")]
      done
      (t/is (= "foo"
               (string/replace-first "yy" #"." "x")))
      (t/is (= (string/join "." ["a" "b" "c"])
               ".cba"))

      (done)
      (t/is (= "xy"
               (string/replace-first "yy" #"." "x")))
      (t/is (= (string/join "." ["a" "b" "c"])
               "a.b.c")))))

(t/deftest both
  (t/testing "both"
    (a/async
      done

      (a/async-redefs
        [string/replace-first (constantly "foo")]
        reset-redefs

        (t/is (= "foo"
                 (string/replace-first "yy" #"." "x")))

        ((juxt reset-redefs done))))))

(t/deftest after
  (t/testing "restored"
    (t/is (= "xy"
             (string/replace-first "yy" #"." "x")))))
