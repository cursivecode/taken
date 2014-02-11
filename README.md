# taken

taken is a web scraping library built with Jsoup

## Installation

Add the following dependency to your `project.clj` file:

```
  [taken "0.1.0"]
```

## Usage

```clojure
(ns yourapp
  (:require [take.core :refer [grab setup-config text]]))
```
The grab function takes a map of a connection and query options.

The connection is a jsoup connection created by the ```setup-config``` function
```clojure
(grab {:conn (setup-config {:url "http://github.com" 
                            :user-agent "Mozilla/5.0"
                            :timeout 5000})
       ...}
```

The query option is a key and a vector of a selector, an optional attribute selector, and any number of functions
```clojure
:field [".some-query" "optional-attribute" some-fns]
```

This will take the first article from techcrunch.com
```clojure
(grab {:conn (setup-config {:url "http://techcrunch.com" :user-agent "Mozilla/5.0"})
       :title [".river .post-title a" first text]
       :author [".byline a" first text]
       :summary [".excerpt" first text]})
```
The result from the function above (results will vary)
```clojure
{:author "Anthony Ha", 
 :title "Cozy Launches New Feature To Give Tenants More Control Over Their Credit Reporting", 
 :summary "Cozy is tackling another part of the rental process today with the 
           launch of a new feature for credit checks. The startup, which raised 
           a Series A led by General Catalyst last year, already allows landlords 
           to perform tasks like screening tenants and collecting rent. Now, when 
           a landlord finds an applicant that they're interested in, they can 
           actually request the credit report from the Cozy… Read More",
 :conn #<HttpConnection org.jsoup.helper.HttpConnection@54274d27>}
```



## License

Copyright © 2014 Michael Doaty

Distributed under the Eclipse Public License, same as Clojure.     