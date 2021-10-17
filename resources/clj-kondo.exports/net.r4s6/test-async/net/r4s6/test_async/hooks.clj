(ns net.r4s6.test-async.hooks
  (:require
    [clj-kondo.hooks-api :as api]))

(defn async-redefs
  "Rewrite
  (async-redefs [bind ings] done body)
  as
  (fn [done] (with-redefs [bind ings] body))
  so that we will also get \"unused binding\" if the callback isn't utilized."
  [{:keys [node]}]
  (let [[binding-vec callback-sym & body] (rest (:children node))
        new-node (api/list-node
                   [(api/token-node 'fn)
                    (api/vector-node [callback-sym])
                    (api/list-node
                      (concat
                        [(api/token-node 'with-redefs)
                         (api/vector-node (:children binding-vec))]
                        body))])]
    {:node (with-meta new-node (meta node))}))
