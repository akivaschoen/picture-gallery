(ns picture-gallery.routes.home
  (:require [compojure.core :refer :all]
            [noir.session :as session]
            [picture-gallery.routes.gallery :refer [show-galleries]]
            [picture-gallery.views.layout :as layout]))

(defn home []
  (if-let [user (session/get :user)]
    (layout/common (show-galleries))
    (layout/common
      [:h1 "Welcome to Picture Gallery"])))

(defroutes home-routes
  (GET "/" [] (home)))
