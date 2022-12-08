package com.bankofapis.dto.accounts;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

@Getter
@Setter
public class GetAccountBalancesDTO {
    @JsonProperty("Data")
    private Data data;

    @JsonProperty("Links")
    private Links links;

    @JsonProperty("Meta")
    private Meta meta;

    @Override
    public String toString() {
        return "GetAccountBalancesDTO{" +
                "data=" + data +
                ", \nlinks=" + links +
                ", \nmeta=" + meta +
                '}';
    }

    @Getter
    @Setter
    public static class Data {

        @JsonProperty("Balance")
        private Balance[] balance;

        @Override
        public String toString() {
            return "Data{" +
                    "\nbalance=" + Arrays.toString(balance) +
                    '}';
        }

        @Getter
        @Setter
        public static class Balance {

            public enum CreditDebitIndicator {
                Credit("Credit"),
                Debit("Debit");

                @JsonValue
                private String value;

                CreditDebitIndicator(String value) {
                    this.value = value;
                }
            }

            public enum Type {
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

                Type(String value) {
                    this.value = value;
                }

            }

            @JsonProperty("AccountId")
            private String accountId;

            @JsonProperty("CreditDebitIndicator")
            private CreditDebitIndicator creditDebitIndicator;

            @JsonProperty("Type")
            private Type type;

            @JsonProperty("DateTime")
            private String dateTime;

            @JsonProperty("Amount")
            private Balance_Amount balanceAmount;

            @JsonProperty("CreditLine")
            private CreditLine[] creditLine;

            @Override
            public String toString() {
                return "Balance{" +
                        "accountId='" + accountId + '\'' +
                        ",\ncreditDebtIndicator=" + creditDebitIndicator +
                        ",\ntype=" + type +
                        ",\ndateTime='" + dateTime + '\'' +
                        ",\nbalanceAmount=" + balanceAmount +
                        ",\ncreditLine=" + Arrays.toString(creditLine) +
                        '}';
            }

            @Getter
            @Setter
            public static class Balance_Amount {

                @JsonProperty("Amount")
                private String amount;

                @JsonProperty("Currency")
                private String currency;

                @Override
                public String toString() {
                    return "Balance_Amount{" +
                            "amount='" + amount + '\'' +
                            ",\ncurrency='" + currency + '\'' +
                            '}';
                }
            }

            @Getter
            @Setter
            public static class CreditLine {

                public enum Type {
                    PreAgreed("Pre-Agreed"),
                    Emergency("Emergency"),
                    Temporary("Temporary"),
                    Available("Available"),
                    // Minor tweaks for InvalidFormatException while testing code for SIT(Non-Prod) Environment
                    Credit("Credit");

                    @JsonValue
                    private String value;

                    Type(String value) {
                        this.value = value;
                    }
                }

                @JsonProperty("Included")
                private boolean included;

                @JsonProperty("Amount")
                private Balance_CreditLine_Amount balanceCreditLineAmount;

                @JsonProperty("Type")
                private Type type;

                @Override
                public String toString() {
                    return "CreditLine{" +
                            "included=" + included +
                            ",\nbalanceCreditLineAmount=" + balanceCreditLineAmount +
                            ",\ntype=" + type +
                            '}';
                }

                @Getter
                @Setter
                public static class Balance_CreditLine_Amount {

                    @JsonProperty("Amount")
                    private String amount;

                    @JsonProperty("Currency")
                    private String currency;

                    @Override
                    public String toString() {
                        return "Balance_CreditLine_Amount{" +
                                "amount='" + amount + '\'' +
                                ",\ncurrency='" + currency + '\'' +
                                '}';
                    }
                }
            }
        }
    }

    @Getter
    @Setter
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

    @Getter
    @Setter
    public static class Meta {

        @JsonProperty("TotalPages")
        private int totalPages;

        @Override
        public String toString() {
            return "Meta{" +
                    "\ntotalPages=" + totalPages +
                    '\n' + '}';
        }
    }
}
