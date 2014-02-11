(ns taken.core
  (:require [clojure.string :as s])
  (:import org.jsoup.Jsoup))

(defn key->method
  "Takes hyphen separated key and creates camel-cased symbol"
  [k & body]
  (let [words (s/split (name k) #"[\s_-]+")]
    (symbol (s/join "" (cons (s/lower-case (str "." (first words)))
                             (map s/capitalize (rest words)))))))

(defn method-call
  "Takes a seq map and creates a method call list"
  [[k v]]
  (if (map? v)
    (list (key->method k) (name (key (first v))) (val (first v)))
    (list (key->method k) v)))

(defmacro setup-config
  "Takes a map of url and options.  Creates a connection with the url, and runs each
   config on the connection.  Returns the connection."
  [config]
  `(doto (Jsoup/connect ~(:url config)) ~@(map method-call (dissoc config :url))))

(defn attr
  "Takes an element(s) and grabs all attribute tags"
  [elements attr]
  (if attr
    (map #(.attr % attr) elements)
    elements))

(defn text
  "Returns the text of a field"
  [field]
  (.text field))

(defprotocol Takeable
  (snatch [this selector] [this selector attribute]))

(extend-protocol Takeable
  org.jsoup.helper.HttpConnection
  (snatch
    ([this selector]
       (.select (.get this) selector))
    ([this selector attribute]
       (attr (.select (.get this) selector) attribute)))
  org.jsoup.nodes.Element
  (snatch
    ([this selector]
       (.select this selector))
    ([this selector attribute]
       (attr (.select this selector) attribute)))
  org.jsoup.select.Elements
  (snatch
    ([this selector]
       (.select this selector))
    ([this selector attribute]
       (attr (.select this selector) attribute))))

(defn grab-helper [m [k v]]
  (let [possible-query (take 2 v)]
    (if (every? string? possible-query)
      (assoc m k ((apply comp (reverse (drop 2 v)))
                  (apply snatch (cons (:conn m) possible-query))))
      (assoc m k ((apply comp (reverse (drop 1 v)))
                  (apply snatch (cons (:conn m) (take 1 v))))))))

(defn grab [options]
  (reduce grab-helper (select-keys options [:conn]) (dissoc options :conn)))

