(ns searcher.views
  (:require [cljfx.api :as fx]
            [cljfx.css :as css]
            [searcher.util :as util]
            [searcher.events :as events]
            [searcher.style :as style]))

(def sep (System/lineSeparator))

(defn search-text-input [{:keys [fx/context]}]
  {:fx/type :text-field
   :max-width 200
   :on-key-released {:event/type ::events/text-field-key-event}
   :on-text-changed {:event/type ::events/set-text}
   :text (fx/sub-val context :search-text)})

(defn- name-by-meta [{nm :name ns :ns}]
  (str nm (when ns (str " (" (ns-name ns) ")"))))

(defn- item-cell-factory [item]
  {:text ""
   :graphic {:fx/type :v-box
             :style-class "search-list-cell-link"
             :on-mouse-clicked {:event/type ::events/go-detail-by-mouse :item item}
             :children [{:fx/type :label
                         :style-class "search-tag-link"
                         :text (name-by-meta item)}]}})

(defn list-view [{:keys [fx/context]}]
  (let [search-text (fx/sub-val context :search-text)]
    {:fx/type :list-view
     :on-key-pressed {:event/type ::events/go-detail-by-key}
     :on-selected-item-changed {:event/type ::events/set-current-item}
     :cell-factory {:fx/cell-type :list-cell
                    :style-class "search-list-cell"
                    :describe item-cell-factory}
     :items (util/completion search-text)}))
     
(defn item-list [{:keys [fx/context]}]
  {:fx/type :v-box
   :style-class "search-item-list"
   :alignment :center
   :children [{:fx/type :label
               :text "Input search text"}
              {:fx/type search-text-input
               :style-class "search-input"}
              {:fx/type list-view
               :v-box/vgrow :always}]})

(defn back-button [{:keys [fx/context destination]}]
  {:fx/type :button
   :text "←"
   :on-key-pressed {:event/type ::events/back-button-key-press}
   :on-mouse-clicked {:event/type ::events/go-item-list}})

(defn item-detail [{:keys [fx/context]}]
  (let [item (fx/sub-val context :current-item)
        {:keys [doc arglists]} item]
    {:fx/type :v-box
     :spacing 10
     :children [{:fx/type back-button}
                {:fx/type :label
                 :text (util/clojure-type item)
                 :style-class "search-tag-info"}
                {:fx/type :label
                 :text "Name"
                 :style-class "search-h5"}
                {:fx/type :text-area
                 :text (util/name-modification item)
                 :wrap-text true
                 :pref-row-count 1
                 :editable false
                 :style-class "search-pre-area"}
                {:fx/type :label
                 :text "Arglists"
                 :style-class "search-h5"}
                {:fx/type :text-area
                 :text (str arglists)
                 :wrap-text true
                 :pref-row-count (util/row-count (str arglists) sep)
                 :pref-column-count (util/column-count (str arglists) sep)
                 :editable false
                 :style-class "search-pre-area"}
                {:fx/type :label
                 :text "Doc"
                 :style-class "search-h5"}
                {:fx/type :text-area
                 :text (util/remove-space-after-newline (str doc) sep)
                 :wrap-text true
                 :pref-row-count (util/row-count (str doc) sep)
                 :pref-column-count (util/column-count (str doc) sep)
                 :editable false
                 :style-class "search-pre-area"}
                ]}))

(defn root [{:keys [fx/context]}]
  (let [current-window (fx/sub-val context :current-window)]
    {:fx/type :stage
     :showing true
     :title "Search"
     :scene {:fx/type :scene
             :stylesheets [(::css/url style/style)]
             :root {:fx/type :anchor-pane
                    :style-class "search"
                    :children [{:fx/type (case current-window
                                           "item-list" item-list
                                           "item-detail" item-detail)
                                :anchor-pane/bottom 10
                                :anchor-pane/top 10
                                :anchor-pane/left 10
                                :anchor-pane/right 10}]}}}))
  
