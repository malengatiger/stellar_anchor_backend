package com.anchor.api.services.payments;

import com.anchor.api.data.models.ozow.OzowPaymentRequest;
import com.anchor.api.data.models.payfast.PayFastRequest;
import com.anchor.api.util.E;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class HashCheckGenerator {
    public HashCheckGenerator() {
        LOGGER.info(E.BASKET_BALL.concat(E.BASKET_BALL.concat("HashCheckGenerator up and ready")));
    }

    public static final Logger LOGGER = LoggerFactory.getLogger(HashCheckGenerator.class.getSimpleName());
    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();

    @Value("${ozowPrivateKey}")
    private String privateKey;

    public String generateOzowHash(OzowPaymentRequest request) throws Exception {

        AtomicReference<StringBuilder> sb = new AtomicReference<>(new StringBuilder());
        sb.get().append(request.getSiteCode());
        sb.get().append(request.getCountryCode());
        sb.get().append(request.getCurrencyCode());
        sb.get().append(request.getAmount());
        sb.get().append(request.getTransactionReference());
        sb.get().append(request.getBankReference());
        //        if (request.getTokenNotificationUrl() != null) {
//            sb.append(request.getTokenNotificationUrl());
//        }
//        if (request.getOptional1() != null) {
//            sb.append(request.getOptional1());
//        }
//        if (request.getOptional2() != null) {
//            sb.append(request.getOptional2());
//        }
//        if (request.getOptional3() != null) {
//            sb.append(request.getOptional3());
//        }
//        if (request.getOptional4() != null) {
//            sb.append(request.getOptional4());
//        }
//        if (request.getOptional5() != null) {
//            sb.append(request.getOptional5());
//        }
        sb.get().append(request.getCustomer());
        sb.get().append(request.getCancelUrl());
        sb.get().append(request.getErrorUrl());
        sb.get().append(request.getSuccessUrl());
        sb.get().append(request.getNotifyUrl());
        sb.get().append(request.isTest());
        sb.get().append(privateKey);

        String toHash = sb.toString().toLowerCase();
        System.out.println(E.FLOWER_YELLOW + ".... String to Hash: ".concat(toHash));

        return generateOzowHash(toHash);
    }

    @Value(value = "${payfast.passPhrase}")
    private String payfastPassPhrase;

    private  String generatePayfastSignature(String stringToHash) throws Exception {


        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.update(stringToHash.getBytes());
        byte[] digest = messageDigest.digest();
        String hashedOutput = DatatypeConverter.printHexBinary(digest).toLowerCase();
//        String md5Hex = DigestUtils
//                .md5Hex(stringToHash);

//        assertThat(md5Hex.equals(hash)).isTrue();
//        MessageDigest md = MessageDigest.getInstance("MD5");
//        md.update(payfastPassPhrase.getBytes());
//        byte[] digest = md.digest(stringToHash.getBytes());
//        String myHash = DatatypeConverter
//                .printString(digest);

        System.out.println("\uD83D\uDD35 \uD83D\uDD35 PayFast Signature: ".concat(hashedOutput)
        .concat(" ".concat(E.PIG.concat(E.PIG))));
        return hashedOutput;
    }

    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#0.00");
    public String generatePayfastSignature(PayFastRequest request) throws Exception {
        //52575c55bc6e0ffc4e21480ce72af873 - my result
        //e45ec6cda1472a9ced3798e87ff0566d - payfast format
        AtomicReference<StringBuilder> sb = new AtomicReference<>(new StringBuilder());
        String mId = URLEncoder.encode(request.getMerchant_id(), StandardCharsets.UTF_8.toString());
        String mKey = URLEncoder.encode(request.getMerchant_key(), StandardCharsets.UTF_8.toString());
        String ret = URLEncoder.encode(request.getReturn_url(), StandardCharsets.UTF_8.toString());
        String can = URLEncoder.encode(request.getCancel_url(), StandardCharsets.UTF_8.toString());
        String not = URLEncoder.encode(request.getNotify_url(), StandardCharsets.UTF_8.toString());
/*
        ////////////////  🍎 🍎 🍎 mine
        //merchant_id=10017569
        // &merchant_key=x9wtl6t7tnk40
        // &return_url=https%3A%2F%2Fnpservices-c4kwri5qva-ew.a.run.app%2Fpayfast_return
        // &cancel_url=https%3A%2F%2Fnpservices-c4kwri5qva-ew.a.run.app%2Fpayfast_cancel
        // &notify_url=https%3A%2F%2Fnpservices-c4kwri5qva-ew.a.run.app%2Fpayfast_notify
        // &m_payment_id=12345678
        // &amount=13.50
        // &item_name=TaxiYam
        //////////////// calculated by me
        merchant_id=10017569
        &merchant_key=x9wtl6t7tnk40
        &return_url=https%3A%2F%2Fnpservices-c4kwri5qva-ew.a.run.app%2Fpayfast_return
        &cancel_url=https%3A%2F%2Fnpservices-c4kwri5qva-ew.a.run.app%2Fpayfast_cancel
        &notify_url=https%3A%2F%2Fnpservices-c4kwri5qva-ew.a.run.app%2Fpayfast_notify
        &&m_payment_id=12345678
        &amount=13.50
        &item_name=TaxiYam
         */
        sb.get().append("merchant_id=").append(mId).append("&");
        sb.get().append("merchant_key=").append(mKey).append("&");
        sb.get().append("return_url=").append(ret).append("&");
        sb.get().append("cancel_url=").append(can).append("&");
        sb.get().append("notify_url=").append(not).append("&");
        if (request.getBuyerDetails() != null) {
            if (request.getBuyerDetails().getName_first() != null) {
                String first = URLEncoder.encode(request.getBuyerDetails().getName_first(), StandardCharsets.UTF_8.toString());
                sb.get().append("name_first=").append(first).append("&");
            }
            if (request.getBuyerDetails().getName_last() != null) {
                String last = URLEncoder.encode(request.getBuyerDetails().getName_last(), StandardCharsets.UTF_8.toString());
                sb.get().append("name_last=").append(last).append("&");
            }
            if (request.getBuyerDetails().getEmail_address() != null) {
                String email = URLEncoder.encode(request.getBuyerDetails().getEmail_address(), StandardCharsets.UTF_8.toString());
                sb.get().append("email_address=").append(email).append("&");
            }
            if (request.getBuyerDetails().getCell_number() != null) {
                String cell = URLEncoder.encode(request.getBuyerDetails().getCell_number(), StandardCharsets.UTF_8.toString());
                sb.get().append("cell_number=").append(cell).append("&");
            }
        }
        if (request.getTransactionDetails() != null) {
            if (request.getTransactionDetails().getM_payment_id() != null) {
                sb.get().append("m_payment_id=").append(request.getTransactionDetails().getM_payment_id());
            }
            String amt = DECIMAL_FORMAT.format(request.getTransactionDetails().getAmount());
            sb.get().append("&amount=").append(amt);
            String item = URLEncoder.encode(request.getTransactionDetails().getItem_name(), StandardCharsets.UTF_8.toString());
            sb.get().append("&item_name=").append(item);
            if (request.getTransactionDetails().getItem_description() != null) {
                sb.get().append("&item_description=").append(request.getTransactionDetails().getItem_description());
            }
        }

        String toHash = sb.toString();
        System.out.println(E.FLOWER_YELLOW + ".... String to create Signature, check encoding and sequence: ".concat(toHash));

        return generatePayfastSignature(toHash);
    }


    public static String generateOzowHash(String stringToHash) throws Exception {

        MessageDigest digest = MessageDigest.getInstance("SHA-512");
        digest.update(stringToHash.getBytes(StandardCharsets.UTF_8));
        byte[] messageDigest = digest.digest();
        StringBuilder hexString = new StringBuilder();
        for (byte b : messageDigest) {
            StringBuilder h = new StringBuilder(Integer.toHexString(0xFF & b));
            while (h.length() < 2)
                h.insert(0, "0");
            hexString.append(h);
        }
        String result = hexString.toString().toLowerCase();
        System.out.println("\uD83D\uDD35 \uD83D\uDD35 Hashed String: ".concat(result));
        return result;
    }


}
