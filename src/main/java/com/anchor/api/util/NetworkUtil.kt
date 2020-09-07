package com.anchor.api.util

import com.anchor.api.data.models.NetworkOperatorDTO
import com.google.gson.GsonBuilder
import khttp.get
import khttp.post
import org.json.JSONArray
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

private val logger = LoggerFactory.getLogger(NetworkUtil::class.java)
private val gson = GsonBuilder().setPrettyPrinting().create()

@Service
class NetworkUtil {

     fun createBFNNetworkOperator(url: String, operator: NetworkOperatorDTO): NetworkOperatorDTO {
        logger.info("\n\uD83C\uDF30 Getting invoices from Node: \uD83E\uDDE9 }  " +
                "\uD83C\uDF30 $url")
        val response2 = get(
                timeout = 990000000.0,
                url = url)

        val result = post(url = url, data = operator);

        logger.info("\uD83C\uDF4E RESPONSE: statusCode: ${result.statusCode}  ")
        logger.info("\uD83C\uDF4E RESPONSE: result text: ${result.text}  ")
        val m = gson.fromJson<NetworkOperatorDTO>(result.text, NetworkOperatorDTO::class.java)
        if (response2.statusCode == 200) {
            logger.info("\uD83C\uDF51 \uD83C\uDF51 Network Operator created")

        } else {
            logger.error("We fucked up! : ${response2.text}")
        }
        return m;
    }
}