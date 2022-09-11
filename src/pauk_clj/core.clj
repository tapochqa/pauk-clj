(ns pauk-clj.core
  (:require [clojure.core.async :refer [<!!]]
            [clojure.string :as str]
            [environ.core :refer [env]]
            [morse.handlers :as h]
            [morse.polling :as p]
            [morse.api :as t]
            [clojure.string :as str]
            [pauk.core :as pauk])
  (:gen-class))

(def token (slurp "token"))

(defn length [message]
  (-> message :entities first :length))
(defn needed-text [message]
  (subs (:text message) (+ (length message) 1)))
(defn send-in-algo [id message keyword]
  (println "Got command")
  (println message)
  (t/send-text token id (pauk/paukize (needed-text message) keyword)))

(h/defhandler handler

  (h/command-fn "start"
    (fn [{{id :id :as chat} :chat}]
      (println "Bot joined new chat: " chat)
      (t/send-text token id "🕷 Напишите что-нибудь на русском или английском")))

  (h/command-fn "german"
    (fn [{{id :id} :chat text :text :as message}] (send-in-algo id message :german)))
  (h/command-fn "ugly"
    (fn [{{id :id} :chat text :text :as message}]
    (send-in-algo id message :ugly)))
  (h/command-fn "short"
    (fn [{{id :id} :chat text :text :as message}]
    (send-in-algo id message :short)))
  (h/command-fn "telegrams"
    (fn [{{id :id} :chat text :text :as message}]
    (send-in-algo id message :telegrams)))
  (h/command-fn "iso_91995_b"
    (fn [{{id :id} :chat text :text :as message}]
    (send-in-algo id message :iso-91995-b)))
  (h/command-fn "iso_r_9_1968"
    (fn [{{id :id} :chat text :text :as message}]
    (send-in-algo id message :iso-r-9-1968)))
  (h/command-fn "mid_2113"
    (fn [{{id :id} :chat text :text :as message}]
    (send-in-algo id message :mid-2113)))
  
  (h/message-fn
    (fn [{{id :id} :chat text :text :as message}]
      (println "Intercepted message: " message)
      (t/send-text token id (pauk/paukize text)))))


(defn -main
  [& args]
  (when (str/blank? token)
    (println "Please provde token in TELEGRAM_TOKEN environment variable!")
    (System/exit 1))

  (println "Starting the pauk-clj")
  (<!! (p/start token handler))
  )
