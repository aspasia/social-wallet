(ns social-wallet.pages.wallet
  (:require [hiccup.page :as page]
            [auxiliary.translation :as t]
            [clavatar.core :as clavatar]
            [hiccup.util :as hu]
            [social-wallet.swapi :as swapi]
            [social-wallet.components.transactions_list :refer [transactions]]
            [social-wallet.components.footer :refer [footer]]
            [social-wallet.webpage :refer [render-error]]
            [social-wallet.components.header :refer [header-account]]
            [social-wallet.components.head :refer [render-head]]
            [failjure.core :as f]))

(defn wallet-page [account swapi-params uri]
  (let [email (:email account)]
    {:headers {"Content-Type"
               "text/html; charset=utf-8"}
     :body (page/html5
            (render-head)
            [:body.container.grid-lg
             (header-account account)
             [:div {:class "wallet-details"}
              [:div.wallet-card.clearfix
               [:span {:class "qrcode float-right"}
                [:img {:src (hu/url  "/qrcode/" email)}]]
               [:div.card-info.float-left
                [:div.tile.tile-centered
                 [:div.tile-icon
                  [:div.gravatar
                   [:img {:src (clavatar/gravatar email :size 87 :default :mm)}]]]
                 [:div.tile-content
                  [:div.tile-title (:name account)]
                  [:div.tile-subtitle.text-gray
                   [:i.icon.icon-mail] email]]]]
               (f/if-let-ok? [balance (swapi/balance swapi-params
                                                     (select-keys account [:email]))]
                             [:div.balance.float-right
                              (str (t/locale [:wallet :balance]))
                              [:span {:class "func--account-page--balance"}]
                              [:h2 balance]]
                             (render-error balance))]

              [:div {:style "margin-top: 60px"}
               
               [:div.divider.text-left {:data-content "YOUR TRANSACTIONS"}]
               (transactions account nil swapi-params {} uri)]]
             (footer)])}))