(ns picture-gallery.views.layout
  (:require [hiccup.form :refer :all]
            [hiccup.page :refer [html5 include-css]]
            [hiccup.element :refer [link-to]]
            [noir.session :as session]))

(defn make-menu [& items]
  [:div (for [item items] [:div.menuitem item])])

(defn guest-menu []
  (make-menu
    (link-to "/" "Home")
    (link-to "/register" "register")
    (form-to [:post "/login"]
             (text-field {:placeholder "User Name"} "id")
             (password-field {:placeholder "Password"} "pass")
              (submit-button "Login"))))

(defn user-menu [user]
  (make-menu
    (link-to "/" "Home")
    (link-to "/upload" "Upload Images")
    (link-to "/logout" (str "Logout " user))))

(defn base [& content]
  (html5
    [:head
     [:title "Welcome to picture-gallery"]
     (include-css "/css/screen.css")]
    [:body content]))

(defn common [& content]
  (base
    (if-let [user (session/get :user)]
      (user-menu user)
      (guest-menu))
    [:div.content content]))

