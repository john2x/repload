# repload

A Clojure utility function to reload all referred vars and aliased namespaces in a REPL.

## Motivation

1. Restarting the REPL is excruciatingly slow
2. [`tools.namespace`][tn] has too much [overhead][overhead] (e.g. state management)

[tn]: https://github.com/clojure/tools.namespace
[overhead]: https://github.com/clojure/tools.namespace#reloading-code-preparing-your-application

## Install

!["Leiningen version"](https://clojars.org/john2x/repload/latest-version.svg)

`repload` was designed to be used in the REPL exclusively. If you are using 
[Leiningen][lein] (which you should), you can inject `repload` into your 
`profiles.clj` to make it available in all your REPLs.

Edit your `~/.lein/profiles.clj` file:

    {:user
     {
       ; ...
       :dependencies [ ; ...
                       [john2x/repload "0.0.3"]]
       :injections   [ ;...
                       (require 'repload)
                       (require '[repload :refer repload])]
      }
     }

[lein]: https://github.com/technomancy/leiningen

## Usage

Now open up a new REPL:

    $ lein new myproject
    $ lein repl
    
    user=> (use 'myproject.core)
    nil
    user=> (require '[myproject.core :as mp])
    nil
    user=> (source foo)
    (defn foo
      "I don't do a whole lot."
      [x]
      (println x "Hello, World!"))
    nil
    user=> (foo "test")
    test Hello, World!
    nil
    user=> (mp/foo "test")
    test Hello, World!
    nil
    
    ;; make some changes to your project
    
    user=> (repload)
    Reloading myproject.core [foo]
    Reloading myproject.core with alias mp
    nil
    user=> (source foo)
    (defn foo
      "I don't do a whole lot."
      [x]
      (println x "Goodbye, World!"))
    nil
    user=> (foo "test")
    test Goodbye, World!
    nil
    user=> (mp/foo "test")
    test Goodbye, World!
    nil
    
By default, `repload` excludes reloading namespaces starting with `clojure` and
`repload`. You can override these prefixes with the following:

    ;; add "myproject" as an ignored prefix
    user=> (repload/add-exclude-prefix! "myproject")
    user=> (repload)
    nil  ;; nothing reloaded

    ;; set "myproject" as the *only* ignored prefix
    ;; (causing a *lot* of `clojure.core` vars to be reloaded)
    ;; (*not recommended*)
    user=> (repload/set-exclude-prefixes! ["myproject"])

## License

Copyright © 2014 John Louis Del Rosario

Distributed under the MIT license.
