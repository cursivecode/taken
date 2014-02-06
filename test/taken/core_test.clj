(ns taken.core-test
  (:require [clojure.test :refer :all]
            [taken.core :refer :all])
  (:import org.jsoup.Jsoup))

(deftest key->method-test
  (testing "Keyword turns into symbol"
    (is (= '.toString (key->method :to-string)))
    (is (= '.ignoreContentType (key->method :ignore-content-type)))
    (is (= '.cookies (key->method :cookies)))
    (is (= '.maxBodySize (key->method :max-body-size)))))

(deftest method-call-test
  (testing "Returns list when given a seq map"
    (is (= (list '.cookie "name" "me")
           (method-call (first (seq {:cookie {:name "me"}})))))
    (is (= (list '.userAgent "Mozilla/5.0")
           (method-call (first (seq {:user-agent "Mozilla/5.0"})))))))

(deftest setup-config-test
  (testing "setup config return Jsoup connection type"
    (is (= org.jsoup.helper.HttpConnection
           (type (setup-config {:url "http://google.com"
                                :user-agent "Mozilla/5.0"
                                :timeout 5000})))))
  (testing "config arguments are actually being set"
    (is (= 3000
           (.timeout (.request (setup-config {:url "http://google.com"})))))
    (is (= 9000
           (.timeout (.request (setup-config {:url "http://google.com"
                                              :timeout 9000})))))))

(deftest attr-test
  (testing "attr getting multiple tags"
    (let [elements (-> (Jsoup/parse (slurp "../soup-clj/resources/test.html"))
                       (.select "p a"))]
      (is (= (count (attr elements "href")) 9))
      (is (= (first (attr elements "href")) "/cert/default.asp")))))

(deftest snatch-test
  (let [conn (setup-config {:url "http://facebook.com"
                            :user-agent "Mozilla/5.0"})
        element (Jsoup/parse (slurp "../soup-clj/resources/test.html"))]
    (testing "snatch takes two arguments from a connection"
      (is (= (first (snatch conn "div.loggedout_menubar a" "title"))
             "Go to Facebook Home")))
    (testing "snatch take one argument from a connection"
      (is (= (type (snatch conn "div a i.fb_logo u"))
             org.jsoup.select.Elements))
      (is (= (.text (snatch conn "div a i.fb_logo u"))
             "Facebook logo")))
    (testing "two snatch back to back"
      (is (= (.text (snatch (snatch conn "body") "div a i.fb_logo u"))
             "Facebook logo")))
    (testing "snatch takes one argument from element or document"
      (is (= (.text (snatch element "h2"))
             "W3Schools' Online Certification")))
    (testing "snatch takes two arguments from element or document"
      (is (= (first (snatch element "a img" "src"))
             "/images/w3cert.gif")))))

(deftest grab-test
  (let [conn (setup-config {:url "http://facebook.com"
                            :user-agent "Mozilla/5.0"})
        element (Jsoup/parse (slurp "../taken/resources/test.html"))]
    (testing "grab with connection"
      (is (= (:title (grab conn [[:title ["div.loggedout_menubar a" "title"] first]]))
             "Go to Facebook Home")))
    (testing "grab with multiple functions"
      (is (= (:title (grab conn [[:title ["div.loggedout_menubar a" "title"]
                                  first first]]))
             \G)))))
