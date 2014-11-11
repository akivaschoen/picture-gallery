(ns picture-gallery.routes.gallery
  (:require [compojure.core :refer :all]
            [hiccup.element :refer :all]
            [hiccup.form :refer [check-box]]
            [hiccup.page :refer :all]
            [picture-gallery.views.layout :as layout]
            [picture-gallery.util :refer [thumb-prefix image-uri thumb-uri]]
            [picture-gallery.models.db :as db]
            [noir.session :as session]))

(defn thumbnail-link [{:keys [userid name]}]
  [:div.thumbnail
   [:a {:class name :href (image-uri userid name)}
    (image (thumb-uri userid name))
    (if (= userid (session/get :user)) (check-box name))]])

(defn display-gallery [user-id]
  (if-let [gallery (not-empty (map thumbnail-link 
                                   (db/images-by-user user-id)))]
    [:div
     [:div#error]
     gallery
     (if (= user-id (session/get :user))
       [:input#delete {:type "submit" :value "Delete Images"}])]
    [:p "The user " user-id " does not have any galleries."]))

(defn gallery-link [{:keys [userid name]}]
  [:div.thumbnail
   [:a {:href (str "/gallery/" userid)}
    (image (thumb-uri userid name))
    [:br]
    userid "'s gallery"]])

(defn show-galleries []
  (if-let [user (session/get :user)]
    (map gallery-link (db/get-gallery-previews))
    [:p "You haven't uploaded any photos yet."]))

(defroutes gallery-routes
  (GET "/gallery/:user-id" [user-id] 
       (layout/common
         (include-js "/js/gallery.js")
         (display-gallery user-id))))
