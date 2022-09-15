(ns pauk-clj.core
  (:require [clojure.string :as str]
            [pauk-clj.telegram :as tbot]
            [cheshire.core :as json]
            [clojure.string :as str]
            [pauk.core :as pauk]
            [clojure.pprint :refer :all])
  (:gen-class))

(defn needed-text [{text :text entities :entities}]
  (subs text (+ (-> entities first :length) 1)))

(defn reply-in-pauk 
  ([token chat-id text]
    (tbot/send-message {:token token} chat-id (pauk/paukize text)))
  ([token chat-id text standard]
    (tbot/send-message {:token token} chat-id (pauk/paukize text standard))))

(defn the-handler [token message]
  (let [text (:text message)
        chat-id (-> message :chat :id)]
    (when (not (= nil text))
      (cond
        (str/starts-with? text "/german") (reply-in-pauk token chat-id (needed-text message) :ugly)
        (str/starts-with? text "/ugly") (reply-in-pauk token chat-id (needed-text message) :ugly)
        (str/starts-with? text "/short") (reply-in-pauk token chat-id (needed-text message) :short)
        (str/starts-with? text "/telegrams") (reply-in-pauk token chat-id (needed-text message) :telegrams)
        (str/starts-with? text "/iso_91995_b") (reply-in-pauk token chat-id (needed-text message) :iso-91995-b)
        (str/starts-with? text "/iso_r_9_1968") (reply-in-pauk token chat-id (needed-text message) :iso-r-9-1968)
        (str/starts-with? text "/mid_2113") (reply-in-pauk token chat-id (needed-text message) :mid-2113)
        :else (reply-in-pauk token chat-id text)))))

(defn save-offset [offset-file offset]
  (spit offset-file (str offset)))


(defn load-offset [offset-file]
  (try
    (-> offset-file slurp Long/parseLong)
    (catch Throwable _
      nil)))

(defmacro with-safe-log
  "
  A macro to wrap Telegram calls (prevent the whole program from crushing).
  "
  [& body]
  `(try
     ~@body
     (catch Throwable e#
       (println (ex-message e#)))))


(defn run-polling
  [config]

  (let [{{:keys [udpate-timeout]} :polling
         :keys [telegram]}
        config

        me
        (tbot/get-me telegram)

        offset-file "TELEGRAM_OFFSET"

        context
        {:me me
         :telegram telegram
         :config config}

        offset
        (load-offset offset-file)]

    (loop [offset offset]

      (let [updates
            (with-safe-log
              (tbot/get-updates telegram
                              {:offset offset
                               :timeout udpate-timeout}))

            new-offset
            (or (some-> updates peek :update_id inc)
                offset)]

        (println "Got %s updates, next offset: %s, updates: %s"
                    (count updates)
                    new-offset
                    (json/generate-string updates {:pretty true}))

        (when offset
          (save-offset offset-file new-offset))
        (doseq [message updates]
          (pprint message)
          (the-handler (:token telegram) (:message message)))

        (recur new-offset)))))


(defn -main
  [mytoken]
  (println (type mytoken))
  (run-polling {:telegram {:token mytoken} :polling {:update-timeout 1000}}))
