(ns searcher.events
  (:require [cljfx.api :as fx]
            [cljfx.coerce :as co])
  (:import (javafx.scene.input KeyEvent KeyCode
                               KeyCodeCombination
                               KeyCombination
                               KeyCombination$Modifier)))

(defmulti event-handler :event/type)

(defmethod event-handler :default [event]
  (println event))

(defmethod event-handler ::set-text [{:keys [fx/context ^String fx/event]}]
  "Set the string to be searched."
  {:context (fx/swap-context context
                             assoc :search-text event)})

(defmethod event-handler ::text-field-key-event [{:keys [fx/context fx/event]}]
  "Define shortcut keys for text-field"
  (let [target (.getTarget event)
        length (count (.getCharacters target))]
    (cond
      (and (.isControlDown event) (= (.getCode event) KeyCode/A))
      (.positionCaret target 0)
      (and (.isControlDown event) (= (.getCode event) KeyCode/E))
      (.positionCaret target length))))
    
(defmethod event-handler ::set-current-item [{:keys [fx/context fx/event]}]
  "Register the selected items from the list of search results.
  Use this :current-item to display the `item-detail`."
  {:context (fx/swap-context context
                             assoc :current-item event)})

(defmethod event-handler ::go-detail-by-key [{:keys [fx/context fx/event]}]
  "Go to the item detail screen only if the Enter key is pressed."
  (let [current-item (fx/sub-val context :current-item)]
    (when (and (= (class event) KeyEvent)
               (= (.getCode event) KeyCode/ENTER))
      {:context (fx/swap-context context
                                 assoc
                                 :current-window "item-detail"
                                 :current-item current-item)})))

(defmethod event-handler ::go-detail-by-mouse [{:keys [fx/context fx/event item]}]
  "Moves to the item detail screen when the mouse is clicked.
  The trigger is :on-mouse-clicked in views."
  {:context (fx/swap-context context
                             assoc
                             :current-window "item-detail"
                             :current-item item)})

(defmethod event-handler ::go-item-list [{:keys [fx/context fx/event]}]
  "Moves to the Item list screen. At this time, :current-item will be reset."
  {:context (fx/swap-context context
                             assoc
                             :current-window "item-list"
                             :current-item {})})

(defmethod event-handler ::back-button-key-press [{:keys [fx/context ^KeyEvent fx/event]}]
  "Moves to the item list screen when the back button or enter key is pressed 
  while the target is in focus.
  At this time, :current-item will be reset."
  (let [code (.getCode event)]
    (when (or (= code KeyCode/BACK_SPACE) (= code KeyCode/ENTER))
      {:context (fx/swap-context context
                                 assoc
                                 :current-window "item-list"
                                 :current-item {})})))
