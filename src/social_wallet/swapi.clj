;; social-wallet - A generic social wallet UI which uses the social-wallet-api for a beckend

;; Copyright (C) 2019- Dyne.org foundation

;; Sourcecode designed, written and maintained by
;; Aspasia Beneti <aspra@dyne.org>

;; This program is free software; you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

;; This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.

;; You should have received a copy of the GNU Affero General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.

;; Additional permission under GNU AGPL version 3 section 7.

;; If you modify this Program, or any covered work, by linking or combining it with any library (or a modified version of that library), containing parts covered by the terms of EPL v 1.0, the licensors of this Program grant you additional permission to convey the resulting work. Your modified version must prominently offer all users interacting with it remotely through a computer network (if your version supports such interaction) an opportunity to receive the Corresponding Source of your version by providing access to the Corresponding Source from a network server at no charge, through some standard or customary means of facilitating copying of software. Corresponding Source for a non-source form of such a combination shall include the source code for the parts of the libraries (dependencies) covered by the terms of EPL v 1.0 used as well as that of the covered work.

(ns social-wallet.swapi
  (:require [taoensso.timbre :as log]
            [org.httpkit.client :as client]
            [cheshire.core :as json]
            [failjure.core :as f]))


(defn- wrap-errors [response fn]
  (if (or (:error response))
      (f/fail (:error response))
      (if (not= 200 (:status response))
        (do
          (log/trace "-> " (-> response :body type) " -> " (-> response :body))
          (-> response :body (json/parse-string true) :error f/fail))
        (fn response))))

(defn- swapi-request [base-url endpoint json]
  (let [response @(client/post (str base-url "/" endpoint)
                               {:query-params
                                {:a json}})]
    (wrap-errors response #(-> % :body :balance))))

(defn balance-request [base-url params]
  (swapi-request base-url
                 "balance"
                 (json/generate-string
                  (cond-> {:connection "mongo"
                           :type "db-only"}
                    (:email params) (merge {:account-id (:email params)})))))
