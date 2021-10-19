# net.r4s6/test-async

Utilities for writing async tests for both clj and cljs (cljc).

## Usage

In `deps.edn`:

```clojure
{net.r4s6/test-async {:mvn/version "0.2.0"}}
```

In your tests:

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

## API

### async

Works just like `cljs.test/async`

```clojure
(async
  done
  (do
    ...
    (done))
```

In this example `done` is the name that the callback function
will be bound to in the body.

NOTE: It should be called without arguments.

### async-redefs

Like `clojure.core/with-redefs` but instead of restoring defs at the end of the
block it defines a callback function that your block should call when finished
(similar to the way `async` works) that will restore the vars.

```clojure
(async-redefs
  [some-var new-value]
  reset-redefs
  (do
    ...
    (reset-redefs))
```

In this example `reset-redefs` is the name that the callback function
will be bound to in the body.

NOTE: It should be called without arguments.

### with-timeout

Configure the timeout for async tests (default is 10000ms).

```clojure
(with-timeout 30000
  (async
    ...))
```

Alternatively you can set `*timeout*` globally.

## License

Copyright © 2021 Randy Stauner

Distributed under the Eclipse Public License version 1.0.
