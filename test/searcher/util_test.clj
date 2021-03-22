(ns searcher.util-test
  (:require [clojure.test :refer :all]
            [searcher.util :as util]))

(deftest name-modification-test
  (let [mix-ns {:name "ns-test" :ns
                (find-ns (symbol "searcher.util"))}
        non-ns {:name "non-ns-test"}]
    (are [result mod-text] (= result mod-text)
      "searcher.util/ns-test" (util/name-modification mix-ns)
      "non-ns-test" (util/name-modification non-ns))))

(deftest completion-test
  (with-test
    (defn test-util-completion [x] x)
    (is (= (count (util/completion "searcher.util-test/test-util-completion")) 1))
    (is (= (count (util/completion "test-util-completion")) 1))
    (is (= '() (util/completion "")))))

(deftest remove-space-after-newline-test
  (let [t1 "text1\n   "
        t2 "  \n this \n text2"
        t3 " \r\n text3"
        sep "\n"
        windows-sep "\r\n"]
    (are [mod-text remove-sp] (= mod-text remove-sp)
      "text1\n" (util/remove-space-after-newline t1 sep)
      "  \nthis \ntext2" (util/remove-space-after-newline t2 sep)
      " \r\ntext3" (util/remove-space-after-newline t3 windows-sep))))

(deftest row-count-test
  (let [t1 "This is text for test.\n count row\n should 3."
        t2 "no newline here. should 1."
        t3 "windows newline\r\n. should 2"
        sep "\n"
        windows-sep "\r\n"]
    (are [number row-count] (= number row-count)
      3 (util/row-count t1 sep)
      1 (util/row-count t2 sep)
      2 (util/row-count t3 windows-sep))))

(deftest column-count-test
  (let [t1 ""
        t2 "no newl here"
        t3 "one \n two\n three."
        t4 "one\r\n three\r\n two"
        sep "\n"
        windows-sep "\r\n"]
    (are [number column-count] (= number column-count)
      0 (util/column-count t1 sep)
      12 (util/column-count t2 sep)
      7 (util/column-count t3 sep)
      6 (util/column-count t4 windows-sep))))
      
      
        
    
