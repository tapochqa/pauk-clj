(ns pauk-clj.core
  (:require [clojure.core.async :refer [<!!]]
            [clojure.string :as str]
            [environ.core :refer [env]]
            [morse.handlers :as h]
            [morse.polling :as p]
            [morse.api :as t]
            [clojure.string :as str])
  (:gen-class))

(def token (slurp "token"))

(def step-1
  (zipmap 
    (str/split "А Б В Г Д Е Ё Ж З И Й К Л М Н О П Р С Т У Ф Х Ц Ч Ш Щ Ъ Ы Ь Э Ю Я" #" ") 
    (str/split "A B V G D E YO ZH Z I IY K L M N O P R S T U F H TS CH SH SHCH ` Y ` E YU YA" #" ")))
(def step-2 
  (zipmap 
    (str/split "A B C D E F G H I J K L M N O P Q R S T V U W X Y Z" #" ") 
    (str/split "А В С Д Е Г Ж Н I Ь К Л М И О Р Ц Я Ы Т Ф Ю Ш Х У П" #" ")))

(defn paukize[text]
  (def text' (str/replace (str/upper-case text) #"[ЁёА-я]" step-1)) 
  (str (str/replace text' #"[a-zA-Z]" step-2)))

(h/defhandler handler

  (h/command-fn "start"
    (fn [{{id :id :as chat} :chat}]
      (println "Bot joined new chat: " chat)
      (t/send-text token id "🕷 Напишите что-нибудь на русском")))

  (h/message-fn
    (fn [{{id :id} :chat text :text :as message}]
      (println "Intercepted message: " message)
      (t/send-text token id (paukize text)))))


(defn -main
  [& args]
  (when (str/blank? token)
    (println "Please provde token in TELEGRAM_TOKEN environment variable!")
    (System/exit 1))

  (println "Starting the pauk-clj")
  (<!! (p/start token handler)))
