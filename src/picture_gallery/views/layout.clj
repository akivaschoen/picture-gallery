(ns picture-gallery.views.layout
  (:require [hiccup.form :refer :all]
            [hiccup.page :refer [html5 include-css]]
            [hiccup.element :refer [link-to]]
            [noir.session :as session]))

(defn base [& content]
  (html5
    [:head
     [:title "Welcome to picture-gallery"]
     (include-css "/css/screen.css")]
    [:body content]))

(defn common [& content]
  (base
    (if-let [user (session/get :user)]
      [:div (link-to "/logout" (str "Logout " user))]
      [:div (link-to "/register" "register")
       (form-to [:post "/login"]
                (text-field {:placeholder "User Name"} "id")
                (password-field {:placeholder "Password"} "pass")
                (submit-button "Login"))])
    content))
