package com.bankofapis.dto.accounts;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

@Getter
@Setter
public class GetAccountDirectDebitsDTO {

    @JsonProperty("Data")
    private Data data;

    @JsonProperty("Links")
    private Links links;

    @JsonProperty("Meta")
    private Meta meta;

    @Override
    public String toString() {
        return "GetAccountDirectDebitsDTO{" +
                "data=" + data +
                ", \nlinks=" + links +
                ", \nmeta=" + meta +
                '}';
    }

    @Getter
    @Setter
    public static class Data {

        @JsonProperty("DirectDebit")
        DirectDebit[] directDebit;

        @Override
        public String toString() {
            return "Data{" +
                    "\ndirectDebit=" + Arrays.toString(directDebit) +
                    '}';
        }

        @Getter
        @Setter
        public static class DirectDebit {

            public enum DirectDebitStatusCode {
                Active("Active"),
                Inactive("Inactive");

                @JsonValue
                private String value;

                DirectDebitStatusCode(String value) {
                    this.value = value;
                }
            }

            @JsonProperty("Frequency")
            String frequency;

            @JsonProperty("AccountId")
            String accountId;

            @JsonProperty("MandateIdentification")
            String mandateIdentification;

            @JsonProperty("DirectDebitStatusCode")
            DirectDebitStatusCode directDebitStatusCode;

            @JsonProperty("Name")
            String name;

            @JsonProperty("PreviousPaymentDateTime")
            String previousPaymentDateTime;

            @JsonProperty("PreviousPaymentAmount")
            PreviousPaymentAmount previousPaymentAmount;

            @Override
            public String toString() {
                return "DirectDebit{" +
                        "accountId='" + accountId + '\'' +
                        ",\nmandateIdentification='" + mandateIdentification + '\'' +
                        ",\ndirectDebitStatusCode=" + directDebitStatusCode +
                        ",\nname='" + name + '\'' +
                        ",\npreviousPaymentDateTime='" + previousPaymentDateTime + '\'' +
                        ",\npreviousPaymentAmount=" + previousPaymentAmount +
                        ",\nfrequency=" + frequency +
                        "\n}";
            }

            @Getter
            @Setter
            public static class PreviousPaymentAmount {
                @JsonProperty("Amount")
                String amount;

                @JsonProperty("Currency")
                String currency;

                @Override
                public String toString() {
                    return "PreviousPaymentAmount{" +
                            "\namount='" + amount + '\'' +
                            ",\ncurrency='" + currency + '\'' +
                            '}';
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

        @JsonProperty("FirstAvailableDateTime")
        private String firstAvailableDateTime;

        @JsonProperty("LastAvailableDateTime")
        private String lastAvailableDateTime;

        @Override
        public String toString() {
            return "Meta{" +
                    "\ntotalPages=" + totalPages +
                    ", \nfirstAvailableDateTime='" + firstAvailableDateTime + '\'' +
                    ", \nlastAvailableDateTime'" + lastAvailableDateTime + '\'' +
                    '\n' + '}';
        }
    }
}
