package com.anchor.api.data.models

import com.bfn.client.data.TradeMatrixItemDTO

data class NetworkOperatorDTO(
        var issuedBy: String,
        var accountId: String,
        var minimumInvoiceAmount: Double,
        var maximumInvoiceAmount: Double,
        var maximumInvestment: Double,
        var tradeFrequencyInMinutes: Int,
        var defaultOfferDiscount: Double,
        var tradeMatrixItems: MutableList<TradeMatrixItemDTO>,
        var date: String,
        var name: String,
        var email: String,
        var cellphone: String,
        var password: String,
        var uid: String? = null
) {
}
