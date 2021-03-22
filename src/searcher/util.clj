(ns searcher.util
  (:require [clojure.spec.alpha :as spec]
            [clojure.string :as string])
  (:import (java.util.regex Pattern)))

(def ^:private special-doc-map
  '{. {:url "java_interop#dot"
       :forms [(.instanceMember instance args*)
               (.instanceMember Classname args*)
               (Classname/staticMethod args*)
               Classname/staticField]
       :doc "The instance member form works for both fields and methods.
  They all expand into calls to the dot operator at macroexpansion time."}
    def {:forms [(def symbol doc-string? init?)]
         :doc "Creates and interns a global var with the name
  of symbol in the current namespace (*ns*) or locates such a var if
  it already exists.  If init is supplied, it is evaluated, and the
  root binding of the var is set to the resulting value.  If init is
  not supplied, the root binding of the var is unaffected."}
    do {:forms [(do exprs*)]
        :doc "Evaluates the expressions in order and returns the value of
  the last. If no expressions are supplied, returns nil."}
    if {:forms [(if test then else?)]
        :doc "Evaluates test. If not the singular values nil or false,
  evaluates and yields then, otherwise, evaluates and yields else. If
  else is not supplied it defaults to nil."}
    monitor-enter {:forms [(monitor-enter x)]
                   :doc "Synchronization primitive that should be avoided
  in user code. Use the 'locking' macro."}
    monitor-exit {:forms [(monitor-exit x)]
                  :doc "Synchronization primitive that should be avoided
  in user code. Use the 'locking' macro."}
    new {:forms [(Classname. args*) (new Classname args*)]
         :url "java_interop#new"
         :doc "The args, if any, are evaluated from left to right, and
  passed to the constructor of the class named by Classname. The
  constructed object is returned."}
    quote {:forms [(quote form)]
           :doc "Yields the unevaluated form."}
    recur {:forms [(recur exprs*)]
           :doc "Evaluates the exprs in order, then, in parallel, rebinds
  the bindings of the recursion point to the values of the exprs.
  Execution then jumps back to the recursion point, a loop or fn method."}
    set! {:forms[(set! var-symbol expr)
                 (set! (. instance-expr instanceFieldName-symbol) expr)
                 (set! (. Classname-symbol staticFieldName-symbol) expr)]
          :url "vars#set"
          :doc "Used to set thread-local-bound vars, Java object instance
fields, and Java class static fields."}
    throw {:forms [(throw expr)]
           :doc "The expr is evaluated and thrown, therefore it should
  yield an instance of some derivee of Throwable."}
    try {:forms [(try expr* catch-clause* finally-clause?)]
         :doc "catch-clause => (catch classname name expr*)
  finally-clause => (finally expr*)

  Catches and handles Java exceptions."}
    var {:forms [(var symbol)]
         :doc "The symbol must resolve to a var, and the Var object
itself (not its value) is returned. The reader macro #'x expands to (var x)."}})

(defn- special-doc [name-symbol]
  "Collect meta of special forms.
  To identify that it is a special form, add :special-form to the map."
  (assoc (or (special-doc-map name-symbol) (meta (resolve name-symbol)))
         :name name-symbol
         :special-form true))

(defn- namespace-doc [nspace]
  "Collect meta of namespaces.
  Add the name of ns to the map with the :name key."
  (assoc (meta nspace) :name (ns-name nspace)))

(defn- all-doc []
  "Collect meta for all namespaces, functions, variables."
  (concat (mapcat #(sort-by :name (map meta (vals (ns-interns %))))
                  (all-ns))
          (map namespace-doc (all-ns))
          (map special-doc (keys special-doc-map))))

(defn- filter-doc-by-name [text searched-str-builder]
  "Filter the maps(meta) collected by `all-doc` by text.
  The search string is constructed from the map by str-builder."
  (let [re (re-pattern text)
        ms (all-doc)]
    (filter (comp #(re-find (re-matcher re %))
                  searched-str-builder)
            ms)))
  
(defn name-modification [{n :ns nm :name}]
  "If the map contains :ns, 
  Join that ns-name and the value of :name with a slash as string.
  Otherwise, return the value of :name as a string."
  (str (when n (str (ns-name n) "/")) nm))

(defn clojure-type [{n :ns
                     nm :name
                     :keys [forms arglists special-form doc url macro spec]
                     :as m}]
  "Detect the data type from meta."
  (cond
    special-form "Special Form"
    macro "Macro"
    spec "Spec"
    (and nm (not n)) "Namespace"))

(defmulti completion-method
  "Change the meta search method depending on the search string.
  The details are as follows.

  If the search string contains a slash and is not a single slash character,
  the search will be performed using the ns-name of the :ns value (if it exists)
  and the value of :name concatenated with a slash as string.

  Otherwise, the value of :name in meta is used as the search target."
  (fn [text]
    (if-let [splited (and (string/includes? text "/")
                          (string/split text #"/"))]
      (cond
        (nil? (first splited))
        :non-qualified
        :else
        :qualified)
      :non-qualified)))

(defmethod completion-method :non-qualified [text]
  (let [quota-text (Pattern/quote text)
        searched-str-builder #(str (:name %))]
    (filter-doc-by-name quota-text searched-str-builder)))

(defmethod completion-method :qualified [text]
  (let [quota-text (Pattern/quote text)
        searched-str-builder #(str (when (contains? % :ns)
                                      (str (ns-name (:ns %)) "/"))
                                    (:name %))]
    (filter-doc-by-name quota-text searched-str-builder)))

(defmethod ^:private completion-method :default [text]
  '())

(defn completion [text]
  "Search for matching namespaces and variables 
  from the search string."
  (if-not (empty? text)
    (completion-method text)
    '()))

(defn remove-space-after-newline [text separator]
  "If there are spaces after a new line, remove them."
  (let [pattern (re-pattern (str "(" separator "+)" "(\\s+|\\t+)"))]
    (string/replace text pattern separator)))

(defn row-count [text separator]
  "Count the lines of the target string."
  (let [pat (re-pattern separator)]
    (->> (re-seq pat text)
         count
         (+ 1))))

(defn column-count [text separator]
  "Count the columns of the target string."
  (let [pat (re-pattern separator)]
    (->> (string/split text pat)
         (map count)
         (apply max))))
