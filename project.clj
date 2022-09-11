(defproject pauk-clj "0.1.0"
  :description "РАЮК"
  :url "https://t.me/rayk_bot"

  :license {:name "ЕСЛIРЫЕ РЮВЛIС ЛIСЕИЫЕ"
            :url "НТТР://ШШШ.ЕСЛIРЫЕ.ОЯЖ/ЛЕЖАЛ/ЕРЛ-Ф10.НТМЛ"}

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [environ             "1.1.0"]
                 [morse               "0.4.3"]
                 [org.clojars.tapochqa/pauk "1.3.0"]]

  :plugins [[lein-environ "1.1.0"]]

  :main ^:skip-aot pauk-clj.core
  :target-path "target/%s"

  :profiles {:uberjar {:aot :all}})
