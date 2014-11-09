(ns picture-gallery.routes.auth
  (:require [hiccup.form :refer :all]
            [compojure.core :refer :all]
            [picture-gallery.models.db :as db]
            [picture-gallery.routes.home :refer :all]
            [picture-gallery.views.layout :as layout]
            [noir.response :as resp]
            [noir.session :as session]
            [noir.util.crypt :as crypt]
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

(defn format-error [id ex]
  (cond
    (and (instance? org.postgresql.util.PSQLException ex)
         (= 0 (.getErrorCode ex)))
    (str "The user with ID " id " already exists!")
    
    :else
    "An error has occurred while processing the request."))

(defn handle-registration [id pass pass1]
  (if (valid? id pass pass1)
    (try
      (db/create-user {:id id :pass (crypt/encrypt pass)})
      (session/put! :user id)
      (resp/redirect "/")
      (catch Exception ex
        (vali/rule false [:id (format-error id ex)])
        (registration-page)))
    (registration-page id)))

(defroutes auth-routes
  (GET "/register" [] (registration-page))
  (POST "/register" [id pass pass1] (handle-registration id pass pass1)))

