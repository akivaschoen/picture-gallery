(ns picture-gallery.util
  (:require [clojure.string :refer [lower-case]]
            [hiccup.util :refer [url-encode]]
            [noir.session :as session])
  (:import java.io.File))

(def galleries "galleries")
(def thumb-prefix "thumb_")

(defn gallery-path [] 
  (lower-case (str galleries File/separator (session/get :user))))

(defn image-uri [user-id filename]
  (lower-case (str "/img/" user-id "/" (url-encode filename))))

(defn thumb-uri [user-id filename]
  (image-uri user-id (str thumb-prefix filename)))
