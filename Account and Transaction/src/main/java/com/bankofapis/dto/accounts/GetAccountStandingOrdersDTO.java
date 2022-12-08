package com.bankofapis.dto.accounts;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
@Getter @Setter
public class GetAccountStandingOrdersDTO {
    @JsonProperty("Data")
    private Data data;

    @JsonProperty("Links")
    private Links links;

    @JsonProperty("Meta")
    private Meta meta;

    public Data getData() {
        return data;
    }
    @Getter @Setter
    public static class Data {

        @JsonProperty("StandingOrder")
        private StandingOrder[] standingOrders;

        @Override
        public String toString() {
            return "Data{" +
                    "standingOrders=" + Arrays.toString(standingOrders) +
                    '}';
        }
    }
    @Getter @Setter
    public static class StandingOrder {

        public enum StandingOrderStatusCodes {
            Active("Active"),
            Inactive("Inactive");

            @JsonValue
            private String value;

            StandingOrderStatusCodes(String value) {
                this.value = value;
            }
        }

        @JsonProperty("AccountId")
        private String accountId;

        @JsonProperty("Frequency")
        private String frequency;

        @JsonProperty("Reference")
        private String reference;

        @JsonProperty("FirstPaymentDateTime")
        private String firstPaymentDateTime;

        @JsonProperty("NextPaymentDateTime")
        private String nextPaymentDateTime;

        @JsonProperty("FinalPaymentDateTime")
        private String finalPaymentDateTime;

        @JsonProperty("FirstPaymentAmount")
        private FirstPaymentAmount firstPaymentAmount;

        @JsonProperty("NextPaymentAmount")
        private NextPaymentAmount nextPaymentAmount;

        @JsonProperty("FinalPaymentAmount")
        private FinalPaymentAmount finalPaymentAmount;

        @JsonProperty("StandingOrderStatusCode")
        private StandingOrderStatusCodes standingOrderStatusCode;

        @JsonProperty("CreditorAgent")
        private CreditorAgent creditorAgent;

        @JsonProperty("CreditorAccount")
        private CreditorAccount creditorAccount;


        @Override
        public String toString() {
            return "StandingOrder{" +
                    "accountId='" + accountId + '\'' +
                    ", frequency='" + frequency + '\'' +
                    ", reference='" + reference + '\'' +
                    ", firstPaymentDateTime='" + firstPaymentDateTime + '\'' +
                    ", nextPaymentDateTime='" + nextPaymentDateTime + '\'' +
                    ", finalPaymentDateTime='" + finalPaymentDateTime + '\'' +
                    ", firstPaymentAmount=" + firstPaymentAmount +
                    ", nextPaymentAmount=" + nextPaymentAmount +
                    ", finalPaymentAmount=" + finalPaymentAmount +
                    ", standingOrderStatusCode=" + standingOrderStatusCode +
                    ", creditorAgent=" + creditorAgent +
                    ", creditorAccount=" + creditorAccount +
                    '}';
        }
    }
    @Getter @Setter
    public static class FirstPaymentAmount {
        @JsonProperty("Amount")
        private String amount;

        @JsonProperty("Currency")
        private String currency;

        @Override
        public String toString() {
            return "FirstPaymentAmount{" +
                    "amount='" + amount + '\'' +
                    ", currency='" + currency + '\'' +
                    '}';
        }
    }
    @Getter @Setter
    public static class NextPaymentAmount {
        @JsonProperty("Amount")
        private String amount;

        @JsonProperty("Currency")
        private String currency;

        @Override
        public String toString() {
            return "NextPaymentAmount{" +
                    "amount='" + amount + '\'' +
                    ", currency='" + currency + '\'' +
                    '}';
        }
    }
    @Getter @Setter
    public static class FinalPaymentAmount {
        @JsonProperty("Amount")
        private String amount;

        @JsonProperty("Currency")
        private String currency;

        @Override
        public String toString() {
            return "FinalPaymentAmount{" +
                    "amount='" + amount + '\'' +
                    ", currency='" + currency + '\'' +
                    '}';
        }
    }
    @Getter @Setter
    public static class CreditorAgent {

        public enum SchemeNames {
            BICFI("UK.OBIE.BICFI");

            @JsonValue
            private String value;

            SchemeNames(String value) {
                this.value = value;
            }
        }

        @JsonProperty("SchemeName")
        private SchemeNames schemeName;

        @JsonProperty("Identification")
        private String identification;

        @Override
        public String toString() {
            return "CreditorAgent{" +
                    "schemeName=" + schemeName +
                    ", identification='" + identification + '\'' +
                    '}';
        }
    }
    @Getter @Setter
    public static class CreditorAccount {

        public enum SchemeNames {
            IBAN("UK.OBIE.IBAN"),
            SortCodeAccountNumber("UK.OBIE.SortCodeAccountNumber");

            @JsonValue
            private String value;

            SchemeNames(String value) {
                this.value = value;
            }
        }

        @JsonProperty("SchemeName")
        private SchemeNames schemeName;

        @JsonProperty("Identification")
        private String identification;

        @JsonProperty("Name")
        private String name;

        @Override
        public String toString() {
            return "CreditorAccount{" +
                    "schemeName=" + schemeName +
                    ", identification='" + identification + '\'' +
                    ", name='" + name + '\'' +
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
                    "\nself='" + self + '\'' +
                    ",\n prev='" + prev + '\'' +
                    ",\n next='" + next + '\'' + '\n' +
                    '}' + '\n';
        }
    }
    @Getter @Setter
    public static class Meta {

        @JsonProperty("TotalPages")
        private int totalPages;


        @JsonProperty("FirstAvailableDateTime")
        private String firstAvailableDateTime;

        @JsonProperty("LastAvailableDateTime")
        private String lastAvailableDateTime;

        @Override
        public String toString() {
            return "Meta{" +
                    "totalPages=" + totalPages +
                    ", firstAvailableDateTime='" + firstAvailableDateTime + '\'' +
                    ", lastAvailableDateTime='" + lastAvailableDateTime + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "GetAccountStandingOrdersDTO{" +
                "data=" + data +
                ", links=" + links +
                ", meta=" + meta +
                '}';
    }
}
