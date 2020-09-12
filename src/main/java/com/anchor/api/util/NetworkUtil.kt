package com.anchor.api.util

import com.anchor.api.data.AccountInfoDTO
import com.anchor.api.data.UserDTO
import com.anchor.api.data.models.NetworkOperatorDTO
import com.anchor.api.services.misc.TOMLService
import com.anchor.api.services.stellar.AccountService
import com.google.gson.GsonBuilder
import khttp.post
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Service

private val logger = LoggerFactory.getLogger(NetworkUtil::class.java)
private val gson = GsonBuilder().setPrettyPrinting().create()

@Service
class NetworkUtil {

    /*
        fun createAccount(url: String, accountName: String, email: String, cellphone: String) {

     */
    fun createBFNCustomer(url: String, accountName: String, email: String, cellphone: String): AccountInfoDTO {
        logger.info("\n\uD83C\uDF30 Sending HTTP call to the BFN network to create a brand new Customer accountInfo: \uD83E\uDDE9 }  " +
                "\uD83C\uDF30 url before concatenation : $url")

        val headers = mapOf("Content-Type" to MediaType.APPLICATION_JSON_VALUE)
        val user = UserDTO()
        user.name = accountName
        user.email=  email
        user.cellphone = cellphone
        user.password = "pass123"
        val json = gson.toJson(user)
        logger.info("\uD83C\uDF4E \uD83C\uDF4E \uD83C\uDF4E \uD83C\uDF4E ... json to be sent over the wire: $json");
        val path = "startAccountRegistrationFlow"
        val mPath = "$url$path"
        var  accountInfo: AccountInfoDTO? = null
        logger.info("\uD83C\uDF4E \uD83C\uDF4E \uD83C\uDF4E \uD83C\uDF4E ... url : $mPath ${user.name}")
        try {
            val result = post(url = mPath, data = json, timeout = 990000000.0, headers = headers)
            logger.info("\uD83C\uDF4E RESPONSE: statusCode: ${result.statusCode} text: ${result.text}")
            if (result.statusCode == 200) {
                logger.info("\uD83C\uDF51 \uD83C\uDF51 result status is KOOL! BFN Network Customer created on the BFN platform!!" +
                        " \uD83C\uDF51 \uD83C\uDF51")
                accountInfo = gson.fromJson<AccountInfoDTO>(result.text, AccountInfoDTO::class.java)

                logger.info("\uD83D\uDC99 \uD83D\uDC9C Network Customer added to BFN platform: " + gson.toJson(accountInfo) + "\uD83D\uDC99 \uD83D\uDC9C")
                return accountInfo
            } else {
                logger.info("\uD83C\uDF4E  \uD83C\uDF4E NetworkUtil:createBFNCustomer \uD83C\uDF4E  \uD83C\uDF4E fucked up! : " +
                        "${result.text}  \uD83C\uDF4E  \uD83C\uDF4E")
                throw Exception("\uD83C\uDF4E  \uD83C\uDF4E BFN Network Customer creation FAILED ${result.text}")
            }
        } catch (e:Exception) {
            logger.info("\uD83C\uDF4E \uD83C\uDF4E We have a problem, Senor!" + e.message)
            throw Exception("\uD83C\uDF4E  \uD83C\uDF4E BFN Network Customer creation BLEW UP!! ")
        }
    }
    @Autowired
    private lateinit var accountService: AccountService

    @Autowired
    private lateinit var tomlService: TOMLService

    @Value("\${bfnUrl}")
    private lateinit var bfnUrl: String
//
//    @Value("\${startingXLMBalance}")
//    private lateinit var startingXLMBalance: String
//    @Value("\${startingXLMBalance}")
//    private lateinit var startingFiatBalance: String

    fun createBFNNetworkOperator(url: String, operator: NetworkOperatorDTO): NetworkOperatorDTO {
        logger.info("\n\uD83C\uDF30 Sending HTTP call to the BFN network to create a brand new NetworkOperator: \uD83E\uDDE9 }  " +
                "\uD83C\uDF30 $url/createNetworkOperator")

        val toml = tomlService.anchorToml ?: throw Exception("createBFNNetworkOperator: Anchor toml file is missing ")

        //todo - create account on the Anchor platform and add it to the operator

        operator.date = DateTime().toDateTimeISO().toString()

        try {
            logger.info("\uD83C\uDF4E about to createAndFundUserAccount ...(Stellar) ")
            val responseBag = accountService.createAndFundUserAccount(
                    "50", "0.01", "999999999999")
            logger.info("Response Bag from Stellar network: " + gson.toJson(responseBag))
            operator.stellarAccountId = responseBag.accountResponse.accountId
        } catch (e:Exception) {
            logger.info(E.ERROR+E.ERROR + " Stellar account creation for the BFN Network Operator failed. No Biggie! ... for now")
        }

        val headers = mapOf("Content-Type" to MediaType.APPLICATION_JSON_VALUE)
        val operatorJSON = gson.toJson(operator)
        val suffix = "createNetworkOperator"
        logger.info("\uD83C\uDF4E \uD83C\uDF4E \uD83C\uDF4E \uD83C\uDF4E " +
                "... NetworkOperatorDTO json to be sent over the wire: $operatorJSON sent to $url$suffix")

        val result = post(url = "$url$suffix", data = operatorJSON, timeout = 990000000.0, headers = headers)

        logger.info("\uD83C\uDF4E RESPONSE: statusCode: ${result.statusCode}  ")
        logger.info("\uD83C\uDF4E RESPONSE: result text: ${result.text}  ")
        val m = gson.fromJson<NetworkOperatorDTO>(result.text, NetworkOperatorDTO::class.java)
        if (result.statusCode == 200) {
            logger.info("\uD83C\uDF51 \uD83C\uDF51 result status is KOOL! BFN Network Operator created on the BFN platform!!" +
                    " \uD83C\uDF51 \uD83C\uDF51")

        } else {
            logger.info("\uD83C\uDF4E  \uD83C\uDF4E NetworkUtil:createBFNNetworkOperator fucked up! : " +
                    "${result.text}  \uD83C\uDF4E  \uD83C\uDF4E")
            throw Exception("BFN Network Operator creation FAILED ")

        }
        logger.info("\uD83D\uDC99 \uD83D\uDC9C Network Operator added to BFN platform: " + gson.toJson(m) + "\uD83D\uDC99 \uD83D\uDC9C");

        return m;
    }
}