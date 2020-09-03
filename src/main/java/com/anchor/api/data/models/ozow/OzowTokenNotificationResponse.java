package com.anchor.api.data.models.ozow;

/*
    🌼 🌼 Token Notification Response
        After payment is completed and you have been sent the payment notification response in Step 2, the user will be
        offered the opportunity to create an Ozow PIN to make future payments easier and faster.
        If the user successfully creates their Ozow PIN we will post the token response containing the fields below to the
        TokenNotificationUrl you passed to us in the post variables.

        Property Type Description
        TokenProfileId Guid The token profile ID required for future payments for this user.
        TransactionId Guid The Ozow transaction ID for the payment that was completed.
        TransactionReference String (50) Merchant's transaction reference for the payment that was completed. This
        is the same as the value that was passed through in the post variables.
        Token String (Max) The token required for future payments for this user. This should be treated
        as a sensitive field and should be encrypted and stored securely.
        Error String (250) The error if we could not successfully register the user. No notification will
        be sent if the user opted out of the process.
 */
public class OzowTokenNotificationResponse {
    private String TokenProfileId, TransactionId, TransactionReference, Token, Error;

    public String getTokenProfileId() {
        return TokenProfileId;
    }

    public void setTokenProfileId(String tokenProfileId) {
        TokenProfileId = tokenProfileId;
    }

    public String getTransactionId() {
        return TransactionId;
    }

    public void setTransactionId(String transactionId) {
        TransactionId = transactionId;
    }

    public String getTransactionReference() {
        return TransactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        TransactionReference = transactionReference;
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }

    public String getError() {
        return Error;
    }

    public void setError(String error) {
        Error = error;
    }
}
