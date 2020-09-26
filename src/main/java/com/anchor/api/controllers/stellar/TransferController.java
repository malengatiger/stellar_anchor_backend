package com.anchor.api.controllers.stellar;

import com.anchor.api.data.Fee;
import com.anchor.api.data.GetTransactionsResponse;
import com.anchor.api.data.account.Options;
import com.anchor.api.data.anchor.Anchor;
import com.anchor.api.data.anchor.Client;
import com.anchor.api.data.info.Info;
import com.anchor.api.data.transfer.sep10.AnchorSep10Challenge;
import com.anchor.api.data.transfer.sep10.ChallengeResponse;
import com.anchor.api.data.transfer.sep10.JWTToken;
import com.anchor.api.data.transfer.sep26.DepositOKResponse;
import com.anchor.api.data.transfer.sep26.DepositRequestParameters;
import com.anchor.api.data.transfer.sep26.WithdrawOKResponse;
import com.anchor.api.data.transfer.sep26.WithdrawRequestParameters;
import com.anchor.api.data.transfer.sep27.InfoServerResponse;
import com.anchor.api.services.stellar.AccountService;
import com.anchor.api.services.misc.FirebaseService;
import com.anchor.api.services.misc.TOMLService;
import com.anchor.api.util.E;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.stellar.sdk.responses.SubmitTransactionResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@CrossOrigin(maxAge = 3600)
@RestController
public class TransferController {
    public static final Logger LOGGER = Logger.getLogger(TransferController.class.getSimpleName());
    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();

    /*
    😈 👿 😈 👿
    Every other HTTP status code will be considered an error.
    The body should contain a string indicating the error details.
    This error is in a human readable format in the language indicated in the request and is
    intended to be displayed by the wallet. For example:

        {
           "error": "This anchor doesn't support the given currency code: ETH"
        }
     */
    public TransferController() {
        LOGGER.info("\uD83E\uDD6C \uD83E\uDD6C TransferController  " +
                "\uD83C\uDF51 constructed and ready to go! \uD83C\uDF45 CORS enabled for the controller");
    }

    @Autowired
    private ApplicationContext context;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AnchorSep10Challenge anchorSep10Challenge;


    @Autowired
    private FirebaseService firebaseService;

    /*
    🌺 🌺
    A deposit is when a user sends some non-stellar asset (BTC via Bitcoin network, USD via bank transfer, Cash to a teller, etc...) to an account held by an anchor.
    In turn, the anchor sends an equal amount of tokens on the Stellar network (minus fees) to the user's Stellar account.
    The deposit endpoint allows a wallet to get deposit information from an anchor, so a user has all the information needed to initiate a deposit. It also lets the anchor specify
    additional information that the user must submit interactively via a popup or embedded browser window to be able to deposit.
    🌺
    If the given account does not exist, or if the account doesn't have a trust line for that specific asset, see the Special Cases section below.


    POST TRANSFER_SERVER_SEP0024/transactions/deposit/interactive
    Content-Type: multipart/form-data
    asset_code=USD&email_address=myaccount@gmail.com&account=GACW7NONV43MZIFHCOKCQJAKSJSISSICFVUJ2C6EZIW5773OU3HD64VI

    🔵 🔵 Is this right??
    AnchorUser deposits cash at a teller; someone/some system at the BANK needs to call this endpoint ....
    AnchorUser gives cash to an Agent; agent's app must call this endpoint

    the end-result: 🔵 user has new balance for this asset code
     */
    @PostMapping("deposit")
    public DepositOKResponse deposit(@RequestBody DepositRequestParameters requestParameters) throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 TransferController:deposit ... fiat money into normal account, " +
                "\uD83C\uDF4E \uD83C\uDF4E then issue token for the equivalent value");
        // 1. write request to firestore
        // 2. do payment transaction for xlm token (stablecoin)

        return null;
    }

    /*
    This operation allows a user to redeem an asset currently on the Stellar network for the real asset
    (BTC, USD, stock, etc...) via the anchor of the Stellar asset.

    The withdraw endpoint allows a wallet to get withdrawal information from an anchor,
    so a user has all the information needed to initiate a withdrawal.
    It also lets the anchor specify the url for the interactive webapp to continue with the anchor's side of the withdraw.

    💛 💛  Is this right??
    AnchorUser gets cash at a teller; someone/some system at the BANK needs to call this endpoint ....
    AnchorUser gives cash to an Agent; agent's app must call this endpoint

    the end-result: 🔵 user has new balance for this asset code

    RESPONSE Example, when everything's OK status 200:

    {
      "account_id": "GCIBUCGPOHWMMMFPFTDWBSVHQRT4DIBJ7AD6BZJYDITBK2LCVBYW7HUQ",
      "memo_type": "id",
      "memo": "123"
    }

     */
    @PostMapping("/withdraw")
    public WithdrawOKResponse withdraw(@RequestBody WithdrawRequestParameters requestParameters) throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 TransferController:withdraw ...");
        //todo - check


        return null;
    }

    @PostMapping
    public SubmitTransactionResponse setOptions(@RequestBody Options options) throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 TransferController:setOptions ...");
        return accountService.setOptions(options.getSeed(), options.getClearFlags(), options.getHighThreshold(),
                options.getLowThreshold(), options.getInflationDestination(), options.getMasterKeyWeight());
    }

    @PostMapping("/setAnchorInfo")
    public String setAnchorInfo(@RequestBody Info info) throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 TransferController:setAnchorInfo ...");
        return firebaseService.addAnchorInfo(info);
    }

    @GetMapping(value = "/info_server", produces = MediaType.APPLICATION_JSON_VALUE)
    public InfoServerResponse getServerInfo(@RequestParam String assetCode,
                                            @RequestParam String assetIssuer,
                                            @RequestParam String lang) throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 TransferController:getServerInfo ...");
        return null;
    }

    /*
    🥬 🥬 🥬
    The fee endpoint allows an anchor to report the fee that would be charged for a given deposit or withdraw operation. This is important to allow an anchor to accurately report fees to a user even when the fee schedule is complex. If a fee can be fully expressed with the fee_fixed, fee_percent or fee_minimum fields in the /info response, then an anchor should not implement this endpoint.

    🥬 GET TRANSFER_SERVER_SEP0024/fee

    Request parameters:

    Name	Type	Description

    operation	string	Kind of operation (deposit or withdraw).
    type	    string	(optional) Type of deposit or withdrawal (SEPA, bank_account, cash, etc...).
    asset_code	string	Asset code.
    amount	    float	Amount of the asset that will be deposited/withdrawn.

    🎽 Example request:

    GET https://api.example.com/fee?operation=withdraw&asset_code=ETH&amount=0.5
    On success the endpoint should return 200 OK HTTP status code and a JSON object with the following fields:

    Name	Type	Description
    fee	float	The total fee (in units of the asset involved) that would be charged to deposit/withdraw the specified amount of asset_code.
    Example response:

    {
      "fee": 0.013
    }
    🥬
    😈 👿 😈 👿 😈 👿
    Every HTTP status code other than 200 OK will be considered an error.
    The body should contain error details. For example:
    {
       "error": "This anchor doesn't support the given currency code: ETH"
    }
     */

    @GetMapping(value = "/fee", produces = MediaType.APPLICATION_JSON_VALUE)
    public Fee fee(@RequestParam String operation, String type, String asset_code, String amount) {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 TransferController:fee ..." +
                " " + operation + " " + asset_code + " " + amount);

        return null;
    }

    @GetMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> auth(@RequestParam String account) {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 TransferController:auth ... authorizing account: ".concat(account));
        try {
            ChallengeResponse response = anchorSep10Challenge.newChallenge(account);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            String msg = E.NOT_OK + "Authentication Failed : " + e.getMessage();
            LOGGER.severe(msg);
            return ResponseEntity.badRequest()
                    .body(msg);
        }
    }

    /*
        🌼 🌼 Get JWT token from transaction xdr
            Client submits a challenge transaction (that was previously returned by the challenge endpoint) as a HTTP POST request to WEB_AUTH_ENDPOINT using one of the following formats (both should be equally supported by the server):

        🥏 🥏 check the content type is correct
            Content-Type: application/x-www-form-urlencoded, body: transaction=<signed XDR (URL-encoded)>)
            Content-Type: application/json, body: {"transaction": "<signed XDR>"}
     */
    @PostMapping(value = "/token", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> token(@RequestParam String transaction) throws Exception {
        try {
            String token = anchorSep10Challenge.getToken(transaction);
            LOGGER.info(emm + "JWT Token returned: ".concat(token).concat(emm));
            return ResponseEntity.ok(new JWTToken(token));
        } catch (Exception e) {
            String msg = E.ERROR + "Token acquisition failed " + E.ERROR + e.getMessage();
            LOGGER.severe(msg);
            return ResponseEntity.badRequest()
                    .body(msg);
        }
    }

    /* 🔵🔵🔵🔵🔵 /info
    Allows an anchor to communicate basic info about what their TRANSFER_SERVER_SEP0024 supports to wallets and clients.

        Request
        GET TRANSFER_SERVER_SEP0024/info
        Request parameters:

        🍎
        Name	Type	Description
        lang	string	(optional) Defaults to en. Language code specified using ISO 639-1. description fields in the response should be in this language.

        🍎 🍎 Response
        The response should be a JSON object like:

        {
          "deposit": {
            "USD": {
              "enabled": true,
              "fee_fixed": 5,
              "fee_percent": 1,
              "min_amount": 0.1,
              "max_amount": 1000
            },
            "ETH": {
              "enabled": true,
              "fee_fixed": 0.002,
              "fee_percent": 0
            }
          },
          "withdraw": {
            "USD": {
              "enabled": true,
              "fee_minimum": 5,
              "fee_percent": 0.5,
              "min_amount": 0.1,
              "max_amount": 1000
            },
            "ETH": {
              "enabled": false
            }
          },
          "fee": {
            "enabled": false
          }
        }
     */

    @Autowired
    private TOMLService tomlService;
    @GetMapping("/info")
    public Map<String, Object> info() throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 TransferController:info ... get info object ");
        Anchor anchor = firebaseService.getAnchor();

        if (anchor != null) {
           //what ?

        } else {
            throw new Exception("Anchor Missing");
        }
        return null;
    }

    public static final String emm = E.BLUE_DOT + E.BLUE_DOT + E.BLUE_DOT + E.BLUE_DOT ;
    /*
        🌼 One of id, 🥏 stellar_transaction_id or 🥏 external_transaction_id is required.

        On success the endpoint should return 200 OK HTTP status code and a JSON object with the following fields:

        Name	    Type	Description
        transaction	GetTransactionsResponse	The transaction that was requested by the client.

        If the transaction cannot be found, the endpoint should return a 404 NOT FOUND result.

        😈 👿 😈 👿
        Every HTTP status code other than 200 OK will be considered an error. An empty transaction list is not an error.
        The body should contain error details. For example:

            {
               "error": "This anchor doesn't support the given currency code: ETH"
            }
     */
    @GetMapping("/transaction")
    public ResponseEntity transaction(@RequestParam String id,
                                      @RequestParam String stellar_transaction_id,
                                      @RequestParam String external_transaction_id) throws NotFoundException {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 TransferController:transaction ...");
        GetTransactionsResponse transactionsResponse = null;
        return new ResponseEntity("Transaction not found", HttpStatus.NOT_FOUND);
    }

    /*
    🥏 🥏 🥏 GET TRANSFER_SERVER/transactions
        🍎 Request parameters:

        Name	Type	Description
        asset_code	    string	The code of the asset of interest. E.g. BTC, ETH, USD, INR, etc.
        asset_issuer	string	The issuer of the asset the user wants to deposit with the anchor.
        account	        string	The stellar account ID involved in the transactions.
        no_older_than   string  UTC ISO 8601 string	(optional) The response should contain transactions starting on or after this date & time.
        limit	        int	    (optional) The response should contain at most limit transactions.
        kind	        string	(optional) The kind of transaction that is desired. Should be either deposit or withdrawal.
        paging_id	    string	(optional) The response should contain transactions starting prior to this ID (exclusive).

        🥬 🥬 On success the endpoint should return 200 OK HTTP status code and a JSON object with the following fields:

        Name	        Type	Description
        transactions	array	List of transactions as requested by the client, sorted in time-descending order.
        🥬 🥬
     */
    @GetMapping("/transactions")
    public List<GetTransactionsResponse> transactions(@RequestParam String asset_code,
                                                      @RequestParam String asset_issuer,
                                                      @RequestParam String account,
                                                      @RequestParam int limit,
                                                      @RequestParam String no_older_than,
                                                      @RequestParam String kind,
                                                      @RequestParam String paging_id) {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 TransferController:transactions ...");
        List<GetTransactionsResponse> mList = new ArrayList<>();
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 transactions found: " + mList.size());
        return mList;
    }

    @PutMapping(value = "/customer")
    public void putCustomer(@RequestBody Client client) {

    }

    @DeleteMapping(value = "/customer")
    public void deleteCustomer(@RequestBody Client client) {

    }

    class Error {
        String message;

        public Error(String message) {
            this.message = message;
        }
    }
}
