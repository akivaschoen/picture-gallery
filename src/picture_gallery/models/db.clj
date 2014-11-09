(ns picture-gallery.models.db
  (:require [clojure.java.jdbc :as sql]))

(def db
  {:subprotocol "postgresql"
   :subname "//localhost/gallery"
   :user "admin"
   :password "admin"})

(defn create-user [user]
  (sql/insert! db :users user))

(defn get-user [id]
  (first 
    (sql/query db ["SELECT * FROM users WHERE id = ?" id])))
