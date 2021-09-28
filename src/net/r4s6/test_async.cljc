(ns net.r4s6.test-async
  #?(:clj
     (:require [net.cgrand.macrovich :as macros])
     :cljs
     (:require-macros [net.cgrand.macrovich :as macros])))

#?(:clj
   ; This code copied from clojure.core and modified to pass a function
   ; instead of using a try/catch.
   (defn async-redefs-fn
     "Like core/with-redefs-fn but works like async-redefs macro,
     it passes in to the function a function that must be called to reset the vars.

     (async-redefs-fn {#'some-var temp-val} (fn [done] ...))"
     [binding-map func]
     (let [root-bind (fn [m]
                       (doseq [[a-var a-val] m]
                         (.bindRoot ^clojure.lang.Var a-var a-val)))
           old-vals (zipmap (keys binding-map)
                            (map #(.getRawRoot ^clojure.lang.Var %) (keys binding-map)))]
       (root-bind binding-map)
       (func #(root-bind old-vals)))))

(macros/deftime
  (defmacro async-redefs
    "Like core/with-redefs but instead of wrapping the body and restoring
     the defs at the end it provides a function that you must call to reset them.

     (async-redefs [var-symbol temp-value-expr] done (body...))"
    [bindings done & body]
    (macros/case
      :clj
      `(async-redefs-fn ~(zipmap (map #(list `var %) (take-nth 2 bindings))
                                 (take-nth 2 (next bindings)))
                        (fn [~done] ~@body))

      ; This code copied from cljs.core and modified to pass a function
      ; instead of using a try/catch.
      :cljs
      (let [names (take-nth 2 bindings)
            vals (take-nth 2 (drop 1 bindings))
            orig-val-syms (map (comp gensym #(str % "-orig-val__") name) names)
            temp-val-syms (map (comp gensym #(str % "-temp-val__") name) names)
            binds (map vector names temp-val-syms)
            resets (reverse (map vector names orig-val-syms))
            bind-value (fn [[k v]] (list 'set! k v))]
        `(let [~@(interleave orig-val-syms names)
               ~@(interleave temp-val-syms vals)]
           ~@(map bind-value binds)
           (let [body-fn# (fn [~done] ~@body)
                 done-fn# (fn [] ~@(map bind-value resets))]
             (body-fn# done-fn#)))))))


(macros/deftime
  (defmacro async
    "Runs the provided body async and provides a function that must be called to indicate that the tests are complete.

     In cljs delegates to cljs.test/async.
     In clj it provides the same interface to run the provided body async and simply waits for it to complete.

     (async done (...))"
    [done & body]
    (macros/case
      :cljs `(cljs.test/async ~done ~@body)
      :clj  `(let [p# (promise)
                   done# (partial deliver p# ::done)
                   b# (fn [~done] ~@body)]
               (future (b# done#))
               (deref p#)))))
