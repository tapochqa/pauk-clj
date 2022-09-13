(ns pauk-clj.telegram
  "
  Telegram HTTP API.
  "
  (:require
   [cheshire.core :as json]
   [clojure.java.io :as io]
   [clojure.string :as str]
   [org.httpkit.client :as http]))

(defn filter-params
  "
  Filter out nil values from a map.
  "
  [params]
  (persistent!
   (reduce-kv
    (fn [result k v]
      (if (some? v)
        (assoc! result k v)
        result))
    (transient {})
    params)))

(defn encode-params
  "
  JSON-encode complex values of a map.
  "
  [params]
  (persistent!
   (reduce-kv
    (fn [result k v]
      (if (coll? v)
        (assoc! result k (json/generate-string v))
        (assoc! result k v)))
    (transient {})
    params)))

(defn api-request
  [{:keys [token
           user-agent
           timeout
           keepalive]}

   api-method http-method params]

  (let [params
        (filter-params params)

        url
        (format "https://api.telegram.org/bot%s/%s"
                token (name api-method))

        request
        {:url url
         :method http-method
         :as :stream}

        request
        (cond-> request

          user-agent
          (assoc :user-agent user-agent)

          timeout
          (assoc :timeout timeout)

          keepalive
          (assoc :keepalive keepalive))

        request
        (cond-> request

          ;; for GET, complex values must be JSON-encoded
          (= :get http-method)
          (assoc :query-params (encode-params params))

          (= :post http-method)
          (->
           (assoc-in [:headers "content-type"] "application/json")
           (assoc :body (json/generate-string params))))

        {:keys [error status body headers]}
        @(http/request request)]

    (if error
      (throw (ex-info (format "Telegram HTTP error: %s" (ex-message error))
                      {:api-method api-method
                       :api-params params}
                      error))

      (let [{:keys [content-type]}
            headers

            json?
            (some-> content-type
                    (str/starts-with? "application/json"))

            ;; parse JSON manually as Http Kit cannot
            body-json
            (if json?
              (-> body io/reader (json/decode-stream keyword))
              (throw (ex-info (format "Telegram response was not JSON: %s" content-type)
                              {:http-status status
                               :http-method http-method
                               :http-headers headers
                               :api-method api-method
                               :api-params params})))

            {:keys [ok
                    result
                    error_code
                    description]}
            body-json]

        (if ok
          result
          (throw (ex-info (format "Telegram API error: %s %s %s"
                                  error_code api-method description)
                          {:http-status status
                           :http-method http-method
                           :api-method api-method
                           :api-params params
                           :error-code error_code
                           :error description})))))))


(defn get-me
  "https://core.telegram.org/bots/api#getme"
  [config]
  (api-request config :getMe :get nil))

(defn get-updates
  "https://core.telegram.org/bots/api#getupdates"

  ([config]
   (get-updates config nil))

  ([config {:keys [limit
                   offset
                   timeout
                   allowed-updates]}]

   (api-request config
                :getUpdates
                :get
                {:limit limit
                 :offset offset
                 :timeout timeout
                 :allowed_updates allowed-updates})))

(defn send-message
  "https://core.telegram.org/bots/api#sendmessage"

  ([config chat-id text]
   (send-message config chat-id text nil))

  ([config chat-id text {:keys [parse-mode
                                entities
                                disable-web-page-preview
                                disable-notification
                                protect-content
                                reply-to-message-id
                                allow-sending-without-reply
                                reply-markup]}]

   (api-request config
                :sendMessage
                :post
                {:chat_id chat-id
                 :text text
                 :parse_mode parse-mode
                 :entities entities
                 :disable_web_page_preview disable-web-page-preview
                 :disable_notification disable-notification
                 :protect_content protect-content
                 :reply_to_message_id reply-to-message-id
                 :allow_sending_without_reply allow-sending-without-reply
                 :reply_markup reply-markup})))