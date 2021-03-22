(ns searcher.core
  (:require [cljfx.api :as fx]
            [clojure.core.cache :as cache]
            [searcher.views :as views]
            [searcher.events :as events])
  (:import (javafx.application Platform)))

(def *state
  (atom
   (fx/create-context
    {:search-text ""
     :current-window "item-list"
     :current-item {}}
    cache/lru-cache-factory)))

(def event-handler
  (-> events/event-handler
      (fx/wrap-co-effects
       {:fx/context (fx/make-deref-co-effect *state)})
      (fx/wrap-effects
       {:context (fx/make-reset-effect *state)})))

(def renderer
  (fx/create-renderer
   :middleware (comp
                fx/wrap-context-desc
                (fx/wrap-map-desc (fn [_] {:fx/type views/root})))
   :opts {:fx.opt/map-event-handler event-handler
          :fx.opt/type->lifecycle #(or (fx/keyword->lifecycle %)
                                       (fx/fn->lifecycle-with-context %))}))

(fx/mount-renderer *state renderer)
