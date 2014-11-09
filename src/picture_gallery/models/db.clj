(ns picture-gallery.models.db
  (:require [clojure.java.jdbc :as sql]))

(def db
  {:subprotocol "postgresql"
   :subname "//localhost/gallery"
   :user "admin"
   :password "admin"})

(defn add-image [user-id name]
  (sql/with-db-transaction [con db]
    (if (empty? 
          (sql/query db ["SELECT userid FROM images WHERE userid = ? AND name = ?" user-id name]))
      (sql/insert! db :images {:userid user-id :name name})
      (throw
        (Exception. "You have already uploaded an image with that name.")))))

(defn create-user [user]
  (sql/insert! db :users user))

(defn get-user [id]
  (first 
    (sql/query db ["SELECT * FROM users WHERE id = ?" id])))
