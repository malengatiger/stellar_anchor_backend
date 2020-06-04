package com.anchor.api.data.transfer.sep26;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/*
    👽 👽 👽 👽 SEP 0026 🛎
    POST TRANSFER_SERVER_SEP0024/transactions/withdraw/interactive
    Content-Type: multipart/form-data

    asset_code=USD&email_address=myaccount@gmail.com&account=GACW7NONV43MZIFHCOKCQJAKSJSISSICFVUJ2C6EZIW5773OU3HD64VI
    1. Success: no additional information needed
    Response code: 200 OK

    🐳  🐳
    This is the correct response if the anchor is able to execute the withdrawal
    and needs no additional information about the user.
    It should also be used if the anchor requires information about the user,
    but the information has previously been submitted and accepted.

    🥬 🥬 🥬 Success: no additional information needed
    🥬 🥬 🥬 Response code: 200 OK

    This is the correct response if the anchor is able to execute the withdrawal and needs no additional information about the user. It should also be used if the anchor requires information about the user, but the information has previously been submitted and accepted.

    The response body should be a JSON object with the following fields:

    🍎 Name	        Type	Description
    account_id	    string	The account the user should send its token back to.
    memo_type	    string	(optional) Type of memo to attach to transaction, one of text, id or hash.
    memo	        string	(optional) Value of memo to attach to transaction, for hash this should be base64-encoded.
    eta	            int	    (optional) Estimate of how long the withdrawal will take to credit in seconds.
    min_amount	    float	(optional) Minimum amount of an asset that a user can withdraw.
    max_amount	    float	(optional) Maximum amount of asset that a user can withdraw.
    fee	            float	(optional) If there is a fee for withdraw. Already calculated. In units of the withdrawn asset.
    extra_info	    array	(optional) JSON array with additional information about the withdrawal process. Each element in the array is formatted as follows {key: KEY, value: VALUE}. Wallets are encouraged to present extra_info in a tabular manner and enable easy copy to clipboard for each line value.
 */
public class WithdrawOKResponse {
    private String id;
    @SerializedName("account_id")
    @Expose
    private String accountId;
    @SerializedName("memo_type")
    @Expose
    private String memoType;

    private String memo;
    private int eta;
    @SerializedName("min_amount")
    @Expose
    private float minAmount;
    @SerializedName("max_amount")
    @Expose
    private float maxAmount;
    private float fee;
    private List<ExtraInfoItem> extra_info;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getMemoType() {
        return memoType;
    }

    public void setMemoType(String memoType) {
        this.memoType = memoType;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public int getEta() {
        return eta;
    }

    public void setEta(int eta) {
        this.eta = eta;
    }

    public float getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(float minAmount) {
        this.minAmount = minAmount;
    }

    public float getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(float maxAmount) {
        this.maxAmount = maxAmount;
    }

    public float getFee() {
        return fee;
    }

    public void setFee(float fee) {
        this.fee = fee;
    }

    public List<ExtraInfoItem> getExtra_info() {
        return extra_info;
    }

    public void setExtra_info(List<ExtraInfoItem> extra_info) {
        this.extra_info = extra_info;
    }
}
