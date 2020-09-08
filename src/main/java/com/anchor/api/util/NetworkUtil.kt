package com.anchor.api.util

import com.anchor.api.data.models.NetworkOperatorDTO
import com.google.gson.GsonBuilder
import khttp.post
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import java.time.format.DateTimeFormatterBuilder

private val logger = LoggerFactory.getLogger(NetworkUtil::class.java)
private val gson = GsonBuilder().setPrettyPrinting().create()

@Service
class NetworkUtil {

    fun createBFNNetworkOperator(url: String, operator: NetworkOperatorDTO): NetworkOperatorDTO {
        logger.info("\n\uD83C\uDF30 Sending HTTP call to the BFN network to create a brand new NetworkOperator: \uD83E\uDDE9 }  " +
                "\uD83C\uDF30 $url/createNetworkOperator")

        operator.date = DateTime().toDateTimeISO().toString();
        val headers = mapOf("Content-Type" to MediaType.APPLICATION_JSON_VALUE)
        val operatorJSON = gson.toJson(operator)
        logger.info("\uD83C\uDF4E \uD83C\uDF4E \uD83C\uDF4E \uD83C\uDF4E ... json to be sent over the wire: $operatorJSON");
        val result = post(url = url, data = operatorJSON, timeout = 990000000.0, headers = headers);

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