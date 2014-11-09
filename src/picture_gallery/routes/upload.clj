(ns picture-gallery.routes.upload
  (:require [clojure.java.io :as io]
            [clojure.string :refer [join lower-case]]
            [compojure.core :refer [defroutes GET POST]]
            [hiccup.form :refer :all]
            [hiccup.element :refer [image]]
            [hiccup.util :refer [url-encode]]
            [picture-gallery.models.db :as db]
            [picture-gallery.views.layout :as layout]
            [picture-gallery.util :refer :all]
            [noir.io :refer [upload-file]]
            [noir.session :as session]
            [noir.response :as resp]
            [noir.util.route :refer [restricted]]
            [ring.util.response :refer [file-response]])
  (:import [java.awt.image AffineTransformOp BufferedImage]
           [java.io File FileInputStream FileOutputStream]
           java.awt.geom.AffineTransform
           java.awt.RenderingHints
           javax.imageio.ImageIO))

(def thumb-size 150)
(def thumb-prefix "thumb_")

(defn scale [img ratio width height]
  (let [scale (AffineTransform/getScaleInstance (double ratio) (double ratio))
        transform-op (AffineTransformOp. scale AffineTransformOp/TYPE_BILINEAR)]
    (.filter transform-op img (BufferedImage. width height (.getType img)))))

(defn scale-image [file]
  (let [img (ImageIO/read file)
        img-width (.getWidth img)
        img-height (.getHeight img)
        ratio (/ thumb-size img-height)]
    (scale img ratio (int (* img-width ratio)) thumb-size)))

(defn save-thumbnail [{:keys [filename]}]
  (let [path (str (gallery-path) File/separator)]
    (ImageIO/write
      (scale-image (io/input-stream (str path filename)))
      "png"
      (File. (lower-case (str path thumb-prefix filename))))))

(defn upload-page [info]
  (layout/common
    [:h2 "Upload an image"]
    [:p info]
    (form-to {:enctype "multipart/form-data"}
             [:post "/upload"]
             (file-upload :file)
             (submit-button "Upload"))))

(defn handle-upload [{:keys [filename] :as file}]
  (upload-page 
    (if (empty? filename)
      "Please select a file to upload"

      (try
        (noir.io/upload-file (gallery-path) file :create-path? true)
        (save-thumbnail file)
        (db/add-image (session/get :user) (lower-case filename))
        (image {:height "150px"}
               (lower-case 
                 (str "/img/" (session/get :user) "/" thumb-prefix (url-encode filename))))
        
        (catch Exception ex
          (str "Error uploading file: " (.getMessage ex)))))))

(defn serve-file [user-id filename]
  (file-response (lower-case (join File/separator [galleries user-id filename]))))

(defroutes upload-routes
  (GET "/upload" [info] (restricted (upload-page info)))
  (POST "/upload" [file] (restricted (handle-upload file)))
  (GET "/img/:user-id/:filename" [user-id filename] (restricted (serve-file user-id filename))))
