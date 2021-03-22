# Searcher

Searcher is a GUI tool for searching clojure documents.
You can search for loaded namespaces, functions, and variables.
Also, you can search for later loaded items in the same way.

## Usage

Include a dependency on this project in your `deps.edn`.

```clojure
:deps {com.th994/searcher {:git/url "https://github.com/th994/searcher.git"
                           :sha "568a9f83aa8ee3a7c84767de3216abbe8b0cb9d2"}}
```

And In your repl, please enter the following.

```clojure
(require '[searcher.core])
```
It will then launch and allow you to search for documents. 
