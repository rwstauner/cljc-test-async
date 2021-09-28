# net.r4s6/test-async

Utilities for writing async tests for both clj and cljs (cljc).

## Usage

```clojure
(ns your-tests
  (:require
    [clojure.test :as t]
    [net.r4s6.test-async :as a :include-macros true]))

(t/deftest something
  (a/async
    done
    (a/async-redefs
      [some/var new-value]
      reset-redefs
      (pass (juxt reset-redefs done) to something async)))
```

## License

Copyright © 2021 Randy Stauner

Distributed under the Eclipse Public License version 1.0.
