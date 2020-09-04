package com.anchor.api.data

data class AccountInfoDTO (
    val identifier: String,
    val host: String,
    val name: String,
    val status: String,
    var stellarAccount: String,
    var ripplePublicKey: String,
    var rippleAddress: String

)
