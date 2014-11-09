(ns picture-gallery.routes.auth
  (:require [hiccup.form :refer :all]
            [compojure.core :refer :all]
            [picture-gallery.routes.home :refer :all]
            [picture-gallery.views.layout :as layout]
            [noir.session :as session]
            [noir.response :as resp]
            [noir.validation :as vali]))

(defn valid? [id pass pass1]
  (vali/rule (vali/has-value? id)
             [:id "User ID is required."])
  (vali/rule (vali/min-length? pass 5)
             [:pass "Password must be at least 5 characters."])
  (vali/rule (= pass pass1)
             [:pass "Entered passwords do not match."])
  (not (vali/errors? :id :pass :pass1)))

(defn error-item [[error]]
  [:div.error error])

(defn registration-page [& [id]]
  (layout/base
    (form-to [:post "/register"]
             (label "user-id" "User ID")
             (text-field {:tabindex 1} "id" id)
             (vali/on-error :id error-item)
             [:br]
             
             (label "pass" "Password")
             (password-field {:tabindex 2} "pass")
             [:br]

             (label "pass1" "Verify Password")
             (password-field {:tabindex 3} "pass1")
             (vali/on-error :pass error-item)
             (vali/on-error :pass1 error-item)
             [:br]

             (submit-button {:tabindex 4} "Create Account"))))

(defn handle-registration [id pass pass1]
  (if (valid? id pass pass1)
    (do
      (session/put! :user id)
      (resp/redirect "/"))
    (registration-page id)))

(defroutes auth-routes
  (GET "/register" [] (registration-page))
  (POST "/register" [id pass pass1] (handle-registration id pass pass1)))

