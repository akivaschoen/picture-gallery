(ns picture-gallery.routes.auth
  (:require [hiccup.element :refer [link-to]]
            [hiccup.form :refer :all] 
            [compojure.core :refer :all]
            [picture-gallery.models.db :as db]
            [picture-gallery.routes.home :refer :all]
            [picture-gallery.views.layout :as layout]
            [picture-gallery.util :refer [gallery-path]]
            [noir.response :as resp]
            [noir.session :as session]
            [noir.util.crypt :as crypt]
            [noir.validation :as vali])
  (:import java.io.File))

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

             (submit-button {:tabindex 4} "Create Account")
             (link-to "/" "Cancel"))))

(defn format-error [id ex]
  (cond
    (and (instance? org.postgresql.util.PSQLException ex)
         (= 0 (.getErrorCode ex)))
    (str "The user with ID " id " already exists!")
    
    :else
    "An error has occurred while processing the request."))

(defn valid? [id pass pass1]
  (vali/rule (vali/has-value? id)
             [:id "User ID is required."])
  (vali/rule (vali/min-length? pass 5)
             [:pass "Password must be at least 5 characters."])
  (vali/rule (= pass pass1)
             [:pass "Entered passwords do not match."])
  (not (vali/errors? :id :pass :pass1)))

(defn create-gallery-path []
  (let [user-path (File. (gallery-path))]
    (if-not (.exists user-path) (.mkdirs user-path))
    (str (.getAbsolutePath user-path) File/separator)))

(defn handle-registration [id pass pass1]
  (if (valid? id pass pass1)
    (try
      (db/create-user {:id id :pass (crypt/encrypt pass)})
      (session/put! :user id)
      (create-gallery-path)
      (resp/redirect "/")
      (catch Exception ex
        (vali/rule false [:id (format-error id ex)])
        (registration-page)))
    (registration-page id)))

(defn handle-login [id pass]
  (let [user (db/get-user id)]
    (if (and user (crypt/compare pass (:pass user)))
      (session/put! :user id)))
  
  (resp/redirect "/"))

(defn handle-logout []
  (session/clear!)
  (resp/redirect "/"))

(defroutes auth-routes
  (GET "/register" [] (registration-page))
  (POST "/register" [id pass pass1] (handle-registration id pass pass1))
  (POST "/login" [id pass] (handle-login id pass))
  (GET "/logout" [] (handle-logout)))
