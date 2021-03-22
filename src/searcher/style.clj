(ns searcher.style
  (:require [cljfx.css :as css]))

(def fonts
  (case (System/getProperty "os.name")
    "Mac OS X" {:default-font "'Lucida Grande'" :pre-font "'Courier'"}
    {:default-font "'Droid Sans'" :pre-font "'Courier New'"}))

(def style
  (let [default-font (:default-font fonts)
        pre-font (:pre-font fonts)]
        (css/register
         ::style
         {".search" {:-fx-background-color "#ffffff"
                     :-fx-font-family default-font
                     :-fx-font-weight "normal"
                     :-fx-accent "#A4D2FC"
                     "-h4" {:-fx-font-size 16.25
                            :-fx-font-weight 400}
                     "-h5" {:-fx-font-size 13
                            :-fx-font-weight "bold"}
                     "-pre" {:-fx-font-size 13
                             :-fx-font-family pre-font
                             :-fx-background-color "#EEF3F8"
                             :-fx-border-color "#c7cfd5"
                             :-fx-padding 5}
                     "-pre-area" {:-fx-font-size 13
                                  :-fx-font-family pre-font
                                  :-fx-highlight-fill "#A4D2FC"
                                  :-fx-highlight-text-fill "#000000"
                                  :-fx-cursor :text
                                  " .content" {:-fx-background-color "#EEF3F8"
                                               :-fx-padding 5}}
                     "-input" {:-fx-border-color "#c7cfd5"
                               :-fx-background-radius 10}
                     "-item-list" {:-fx-spacing 5}
                     "-list" {"-cell" {"-link" {:-fx-cursor :hand}}}
                     "-tag" {"-info" {:-fx-font-size 12
                                      :-fx-padding [2 5 2 5]
                                      :-fx-background-color "#EEF3F8"
                                      :-fx-background-radius 10}
                             "-link" {:-fx-text-fill "#4f4f4f"
                                      ":hover" {:-fx-underline :true}}}}
          ".text-field" {:-fx-highlight-text-fill "#000000"}
          ".button" {":hover" {:-fx-cursor :hand}}})))
