(ns picture-gallery.routes.home
  (:require [compojure.core :refer :all]
            [noir.session :as session]
            [picture-gallery.views.layout :as layout]))

(defn home []
  (layout/common 
    (if-let [user (session/get :user)]
      [:h1 "Hello " user]
      [:h1 "Hello World"])))

(defroutes home-routes
  (GET "/" [] (home)))
