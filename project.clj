(defproject pauk-clj "0.1.0"
  :description "РАЮК"
  :url "https://t.me/rayk_bot"

  :license {:name "ЕСЛIРЫЕ РЮВЛIС ЛIСЕИЫЕ"
            :url "НТТР://ШШШ.ЕСЛIРЫЕ.ОЯЖ/ЛЕЖАЛ/ЕРЛ-Ф10.НТМЛ"}

  :dependencies [[org.clojure/clojure "1.11.0"]
                 [http-kit "2.6.0"]
                 [cheshire "5.11.0"]
                 [org.clojars.tapochqa/pauk "1.3.0"]]

  :repl-options{:timeout 120000}

  :main ^:skip-aot pauk-clj.core
  :target-path "target/%s"

   :profiles
    {:dev
     {:global-vars
      {*warn-on-reflection* true
       *assert* true}}

  :uberjar
   {:aot :all
    :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
