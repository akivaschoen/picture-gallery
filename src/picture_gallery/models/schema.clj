(ns picture-gallery.models.schema
  (:require [picture-gallery.models.db :refer :all]
            [clojure.java.jdbc :as sql]))

(defn create-users-table []
  (sql/db-do-commands db
    (sql/create-table-ddl
      :users
      [:id "VARCHAR(32) PRIMARY KEY"]
      [:pass "VARCHAR(100)"])))
