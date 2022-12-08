package com.bankofapis.dto.accounts;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.Date;

@Getter @Setter
public class GetAccountTransactionsDTO {
    @JsonProperty("Data")
    Data data;

    @JsonProperty("Links")
    Links links;

    @JsonProperty("Meta")
    Meta meta;

    @Override
    public String toString() {
        return "GetAccountTransactionsDTO{" +
                "data=" + data +
                ", \nlinks=" + links +
                ", \nmeta=" + meta +
                '}';
    }
    @Getter @Setter
    public static class Data {
        @JsonProperty("Transaction")
        public Transaction[] transactions;

        @Override
        public String toString() {
            return "Data{" +
                    "transactions=" + Arrays.toString(transactions) +
                    '}';
        }
    }
    @Getter @Setter
    public static class Transaction {
        public enum CreditDebitIndicators {
            Credit("Credit"),
            Debit("Debit");
            @JsonValue
            private String value;

            CreditDebitIndicators(String value) {
                this.value = value;
            }
        }

        public enum Statuses {
            Booked("Booked"),
            Pending("Pending");
            @JsonValue
            private String value;

            Statuses(String value) {
                this.value = value;
            }
        }
        @JsonProperty("AccountId")
        private String accountId;

        @JsonProperty("TransactionId")
        String transactionId;

        @JsonProperty("Amount")
        Amount amount;

        @JsonProperty("CreditDebitIndicator")
        CreditDebitIndicators creditDebitIndicator;

        @JsonProperty("Status")
        Statuses status;

        @JsonProperty("BookingDateTime")
        String bookingDateTime;

        @JsonProperty("ValueDateTime")
        String valueDateTime;

        @JsonProperty("TransactionInformation")
        String transactionInformation;

        @JsonProperty("AddressLine")
        String addressLine;

        @JsonProperty("BankTransactionCode")
        BankTransactionCode bankTransactionCode;

        @JsonProperty("ProprietaryBankTransactionCode")
        ProprietaryBankTransactionCode proprietaryBankTransactionCode;

        @JsonProperty("Balance")
        Balance balance;

        @JsonProperty("MerchantDetails")
        MerchantDetails merchantDetails;

        @Override
        public String toString() {
            return "Transaction{" +
                    "accountId='" + accountId + '\'' +
                    ", transactionId='" + transactionId + '\'' +
                    ", amount=" + amount +
                    ", creditDebitIndicator=" + creditDebitIndicator +
                    ", status=" + status +
                    ", bookingDateTime='" + bookingDateTime + '\'' +
                    ", valueDateTime='" + valueDateTime + '\'' +
                    ", transactionInformation='" + transactionInformation + '\'' +
                    ", addressLine='" + addressLine + '\'' +
                    ", bankTransactionCode=" + bankTransactionCode +
                    ", proprietaryBankTransactionCode=" + proprietaryBankTransactionCode +
                    ", balance=" + balance +
                    ", merchantDetails=" + merchantDetails +
                    '}';
        }
    }
    @Getter @Setter
    public static class Links {
        @JsonProperty("Self")
        private String self;

        @JsonProperty("Prev")
        private String prev;

        @JsonProperty("Next")
        private String next;

        @Override
        public String toString() {
            return "Links{" +
                    "self='" + self + '\'' +
                    ", previous='" + prev + '\'' +
                    ", next='" + next + '\'' +
                    '}';
        }
    }
    @Getter @Setter
    public static class Meta {
        @JsonProperty("TotalPages")
        private int totalPages;

        @Override
        public String toString() {
            return "Meta{" +
                    "totalPages=" + totalPages +
                    '}';
        }
    }
    @Getter @Setter
    public static class MerchantDetails {
        @JsonProperty("MerchantName")
        String merchantName;

        @JsonProperty("MerchantCategoryCode")
        String merchantCategoryCode;

        @Override
        public String toString() {
            return "MerchantDetails{" +
                    "merchantName='" + merchantName + '\'' +
                    ", merchantCategoryCode='" + merchantCategoryCode + '\'' +
                    '}';
        }
    }
    @Getter @Setter
    public static class Balance {
        public enum CreditDebitIndicators {
            Credit("Credit"),
            Debit("Debit");
            @JsonValue
            private String value;

            CreditDebitIndicators(String value) {
                this.value = value;
            }
        }

        public enum Types {
            ClosingAvailable("ClosingAvailable"),
            ClosingBooked("ClosingBooked"),
            ClosingCleared("ClosingCleared"),
            Expected("Expected"),
            ForwardAvailable("ForwardAvailable"),
            Information("Information"),
            InterimAvailable("InterimAvailable"),
            InterimBooked("InterimBooked"),
            InterimCleared("InterimCleared"),
            OpeningAvailable("OpeningAvailable"),
            OpeningBooked("OpeningBooked"),
            OpeningCleared("OpeningCleared"),
            PreviouslyClosedBooked("PreviouslyClosedBooked");

            @JsonValue
            private String value;

            Types(String value) {
                this.value = value;
            }
        }
        @JsonProperty("Amount")
        Amount amount;

        @JsonProperty("CreditDebitIndicator")
        CreditDebitIndicators creditDebitIndicator;

        @JsonProperty("Type")
        Types type;

        @Override
        public String toString() {
            return "Balance{" +
                    "amount=" + amount +
                    ", creditDebitIndicator=" + creditDebitIndicator +
                    ", type=" + type +
                    '}';
        }
    }
    @Getter @Setter
    public static class BankTransactionCode {
        @JsonProperty("Code")
        String code;

        @JsonProperty("SubCode")
        String subCode;

        @Override
        public String toString() {
            return "BankTransactionCode{" +
                    "code='" + code + '\'' +
                    ", subCode='" + subCode + '\'' +
                    '}';
        }
    }
    @Getter @Setter
    public static class ProprietaryBankTransactionCode {
        @JsonProperty("Code")
        String code;

        @JsonProperty("Issuer")
        String issuer;

        @Override
        public String toString() {
            return "ProprietaryBankTransactionCode{" +
                    "code='" + code + '\'' +
                    ", issuer='" + issuer + '\'' +
                    '}';
        }
    }
    @Getter @Setter
    public static class Amount {
        @JsonProperty("Amount")
        String amount;

        @JsonProperty("Currency")
        String currency;

        @Override
        public String toString() {
            return "Amount{" +
                    "amount='" + amount + '\'' +
                    ", currency='" + currency + '\'' +
                    '}';
        }
    }
}
