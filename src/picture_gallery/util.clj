(ns picture-gallery.util
  (:require [clojure.string :refer [lower-case]]
            [noir.session :as session])
  (:import java.io.File))

(def galleries "galleries")

(defn gallery-path [] 
  (lower-case (str galleries File/separator (session/get :user))))
