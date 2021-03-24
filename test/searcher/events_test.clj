(ns searcher.events-test
  (:require [clojure.test :refer :all]
            [clojure.core.cache :as cache]
            [cljfx.api :as fx]
            [cljfx.context :as context]
            [searcher.events :as events]))

(def initial-state-format
  {:search-text ""
   :current-window "item-list"
   :current-item {}})

(deftest set-text-test
  (let [text "set text test"
        context (context/create initial-state-format identity)
        new-co-map (events/event-handler {:event/type :searcher.events/set-text
                                          :fx/context context
                                          :fx/event text})]
    (is (= text (context/sub-val (:context new-co-map) :search-text)))))

(deftest set-selected-item-test
  (let [next-item {:doc "next item"}
        context (context/create initial-state-format identity)
        new-co-map (events/event-handler {:event/type :searcher.events/set-selected-item
                                          :fx/context context
                                          :fx/event next-item})]
    (is (= next-item (context/sub-val (:context new-co-map) :selected-item)))))

(deftest go-item-detail-test
  (let [next-window "item-detail"
        next-item {:test "test-item"}
        context (context/create initial-state-format identity)
        new-co-map (events/event-handler {:event/type :searcher.events/go-item-detail
                                          :fx/context context
                                          :fx/event "_dummy_event"
                                          :item next-item})]
    (are [x y] (= x y)
      next-window (context/sub-val (:context new-co-map) :current-window)
      next-item (context/sub-val (:context new-co-map) :current-item))))

(deftest go-item-list-test
  (let [next-window "item-list"
        context (context/create initial-state-format identity)
        new-co-map (events/event-handler {:event/type :searcher.events/go-item-list
                                          :fx/context context
                                          :fx/event next-window})]
    (is (= next-window (context/sub-val (:context new-co-map) :current-window)))
    (is (empty? (context/sub-val (:context new-co-map) :current-item)))))
