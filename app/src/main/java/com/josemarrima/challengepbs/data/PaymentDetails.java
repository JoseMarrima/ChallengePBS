package com.josemarrima.challengepbs.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "payment_details")
public class PaymentDetails {

    @PrimaryKey
    @ColumnInfo
    @NonNull
    @Expose
    @SerializedName("amount")
    private int amount;

    @ColumnInfo
    @NonNull
    @Expose
    @SerializedName("paymentType")
    private String paymentType;

    @ColumnInfo
    @NonNull
    @Expose
    @SerializedName("provider")
    private String provider;

    @ColumnInfo
    @NonNull
    @Expose
    @SerializedName("transactionReference")
    private String transactionReference;

    public PaymentDetails(int amount, @NonNull String paymentType) {
        this.amount = amount;
        this.paymentType = paymentType;
    }

    public PaymentDetails(int amount, @NonNull String paymentType, @NonNull String provider, @NonNull String transactionReference) {
        this.amount = amount;
        this.paymentType = paymentType;
        this.provider = provider;
        this.transactionReference = transactionReference;
    }

    public int getAmount() {
        return amount;
    }

    @NonNull
    public String getPaymentType() {
        return paymentType;
    }

    @NonNull
    public String getProvider() {
        return provider;
    }

    @NonNull
    public String getTransactionReference() {
        return transactionReference;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setPaymentType(@NonNull String paymentType) {
        this.paymentType = paymentType;
    }

    public void setProvider(@NonNull String provider) {
        this.provider = provider;
    }

    public void setTransactionReference(@NonNull String transactionReference) {
        this.transactionReference = transactionReference;
    }
}
