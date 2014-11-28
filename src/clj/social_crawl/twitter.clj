(ns social-crawl.twitter
  (:require [environ.core :refer [env]])
  (:use [twitter.oauth]
        [twitter.callbacks]
        [twitter.callbacks.handlers]
        [twitter.api.restful])
  (:import
   (twitter.callbacks.protocols SyncSingleCallback)))

(def my-creds (make-oauth-creds (env :twitter-consumer-key)
                                (env :twitter-consumer-secret)
                                (env :twitter-token)
                                (env :twitter-secret)))

(defn fetch-followers [screen-name cursor timeout]
  (try (followers-list :oauth-creds my-creds 
                       :params {:screen-name screen-name
                                :count 200
                                :cursor cursor})
       (catch Exception e 
         (Thread/sleep timeout)
         (fetch-followers screen-name cursor (* 2 timeout)))))

(defn write-compressed [filename data]
  (with-open [w (-> filename
                  clojure.java.io/output-stream
                  java.util.zip.GZIPOutputStream.
                  clojure.java.io/writer)]
  (binding  [*out* w]
    (println (pr-str data)))))

(defn read-compressed [filename]
  (with-open [in (java.util.zip.GZIPInputStream.
                   (clojure.java.io/input-stream
                     filename))]
    (slurp in)))

(defn user->followers [screen-name initial-followers start-cursor page-count]
  (let [initial-params {:screen-name screen-name 
                        :count 200}
        params (if start-cursor 
                 (assoc initial-params :cursor start-cursor)
                 initial-params)]
    (loop [followers initial-followers
           current-page (followers-list :oauth-creds my-creds 
                                        :params params) 
           n page-count]
    (let [next-cursor (:next_cursor (:body current-page))] 
      (if (or (zero? n) 
              (zero? next-cursor))
        followers 
        (let [updated-followers (into followers (:users (:body current-page)))
              next-page (fetch-followers screen-name next-cursor 60000)]
          (write-compressed (str "followers_" screen-name "_" n "_" next-cursor ".txt.gz") 
                            (vector screen-name updated-followers n next-cursor))
          (recur updated-followers next-page (dec n)))))))) 

;(deref us)

; @WTFSG MrOtakuComedy SoSingaporean titanlyy TanKwanHong czachrie 
; TODAYonline ChannelNewsAsia SGnews
; leehsienloong SMRT_Singapore konghee kimberly_chia
;  Thesmallvoice eskimon NEAsg TaufikBatisah JoannePeh 
; Jetstar_Asia 

(defn read-followers [filename]
  (read-string (read-compressed filename)))

; (def fol 
;   (read-followers "mrbrown.txt"))

(def fol-stcom
  (read-followers "followers_stcom_3011_1425412472691842337.txt.gz"))

(def us (future (apply user->followers fol-stcom)))
;(future-cancel us)

;(deref us)

;(count fol)
;(count fol-stcom)

; (def mrbrow-screen-names 
;   (set (map :screen_name fol)))

#_(spit "users.edn" 
      (->> fol-stcom
           (filter (complement :protected))
           (filter (comp mrbrow-screen-names :screen_name))
           (filter (comp (partial <= 5) 
                         :followers_count)) 
           (filter (comp (partial <= 25) 
                         :friends_count))
           ;count
           (filter #(re-matches #"(?i).*en.*" (:lang %)))

           (map #(select-keys % [:screen_name 
                                 :description
                                 :location 
                                 :followers_count 
                                 :friends_count 
                                 :created_at]))

           vec))

; Instagram
; CLIENT IDe87e7c796ae54f10bed1af8d17b5738b
; CLIENT SECRET06319217cf694ef380fee2f11f466c11
