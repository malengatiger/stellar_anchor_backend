package com.anchor.api.services.stellar.circle;

import com.anchor.api.services.stellar.circle.models.BalanceData;
import com.anchor.api.services.stellar.circle.models.BlockchainAddress;
import com.anchor.api.services.stellar.circle.models.Wallet;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class CirclePaymentService {

    BalanceData getBalance() {
        //https://api-sandbox.circle.com/v1/balances
        return null;
    }
    List<Wallet> getWallets() {
       //https://api-sandbox.circle.com/v1/wallets

       return null;
    }
    Wallet getWallet(String walletId) {
        //https://api-sandbox.circle.com/v1/wallets/1000068503

        return null;
    }

    List<BlockchainAddress> getListOfBlockchainAddresses(String walletId) {
        //https://api-sandbox.circle.com/v1/wallets/1000068503/addresses
        return null;
    }

    List<Object> getPayouts(String destination, String type) {
        //https://api-sandbox.circle.com/v1/businessAccount/payouts?destination=0a17828a-d0b8-4844-b346-e930f4b629aa&type=wire


        return null;
    }
    String ping() {
        //https://api-sandbox.circle.com/ping

        return null;
    }
    String getConfiguration() {
        //https://api-sandbox.circle.com/v1/configuration
        return null;
    }
    String getPublicKey() {
        //https://api-sandbox.circle.com/v1/encryption/public
        return null;
    }
    String createNotificationSubscription(String endpoint) {
        //POST with endpoint = https://arwebapi-faa2ogs3tq-ew.a.run.app/ping
        // {
        //	"endpoint": "https://arwebapi-faa2ogs3tq-ew.a.run.app/ping"
        //}
        // .... https://api-sandbox.circle.com/v1/notifications/subscriptions

        return null;
    }
    String deleteNotificationSubscription(String id) {
        //https://api-sandbox.circle.com/v1/notifications/subscriptions/ee9697f5-10e5-424b-9b3a-0e93eaba1b7d

        return null;
    }
    Object getPayouts() {
        //https://api-sandbox.circle.com/v1/businessAccount/payouts/
        return null;
    }
    List<Object> getPayout(String id) {
        //https://api-sandbox.circle.com/v1/businessAccount/payouts/
        // id = '''''
        return null;
    }
}
/*
{
  "data": {
    "id": "58666f84-fcf4-433f-aa2f-8d49159aa7f7",
    "description": "Standard Bank ****788A",
    "trackingRef": "CIR2TZC78Z",
    "billingDetails": {
      "name": "David John Carruthers",
      "line1": "1 Main Street",
      "city": "Pretoria",
      "postalCode": "0216",
      "district": "Gauteng",
      "country": "ZA"
    },
    "bankAddress": {
      "bankName": "Standard Bank",
      "city": "Sandton",
      "country": "ZA"
    },
    "createDate": "2021-02-11T02:59:31.511Z",
    "updateDate": "2021-02-11T02:59:31.511Z"
  }
}
#################################################
{
  "data": {
    "id": "0a17828a-d0b8-4844-b346-e930f4b629aa",
    "description": "Standard Bank ****88BX",
    "trackingRef": "CIR2EAY7S6",
    "billingDetails": {
      "name": "Aubrey V Malabie",
      "line1": "198 Maude Street",
      "city": "Sandton",
      "postalCode": "2024",
      "district": "Gauteng",
      "country": "ZA"
    },
    "bankAddress": {
      "bankName": "Standard Bank",
      "city": "Sandton",
      "country": "ZA"
    },
    "createDate": "2021-02-11T09:39:45.271Z",
    "updateDate": "2021-02-11T09:39:45.271Z"
  }
}

{
  "data": {
    "walletId": "1000069935",
    "entityId": "1c7d91ee-24ad-4811-9ee9-5a29338e1080",
    "type": "end_user_wallet",
    "description": "Treasury Wallet A",
    "balances": []
  }
}


 */
