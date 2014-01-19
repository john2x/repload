# repload

A Clojure utility function to reload all referred vars and aliased namespaces in a repl.

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
    ;; set "myproject" as the *only* ignored prefix
    ;; (causing a *lot* of `clojure.core` vars to be reloaded)
    user=> (repload/set-exclude-prefixes! ["myproject"])

## License

Copyright © 2014 John Louis Del Rosario

Distributed under the MIT license.
