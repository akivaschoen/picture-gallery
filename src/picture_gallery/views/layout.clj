(ns picture-gallery.views.layout
  (:require [compojure.response :refer [Renderable]]
            [hiccup.element :refer [link-to]]
            [hiccup.form :refer :all]
            [hiccup.page :refer [html5 include-css include-js]]
            [hiccup.element :refer [link-to]]
            [noir.session :as session]
            [ring.util.response :refer [content-type response]]))

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

(defn utf-8-response [html]
  (content-type (response html) "text/html; charset=utf-8"))

(deftype RenderablePage [content]
  Renderable
  (render [this request]
    (utf-8-response
      (html5
        [:head
         [:title "Welcome to Picture Gallery"]
         (include-css "/css/screen.css")
         [:script {:type "text/javascript"}
          (str "var context=\"" (:context request) "\";")]
         (include-js "//code.jquery.com/jquery-2.1.1.min.js")]
        [:body content]))))

(defn base [& content]
  (RenderablePage. content))

(defn common [& content]
  (base
    (if-let [user (session/get :user)]
      (user-menu user)
      (guest-menu))
    [:div.content content]))

