package com.bankofapis.dto.accounts;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

@Getter
@Setter
public class GetAccountScheduledPaymentsDTO {
    @JsonProperty("Data")
    private Data data;

    @JsonProperty("Links")
    private Links links;

    @JsonProperty("Meta")
    private Meta meta;

    @Override
    public String toString() {
        return "" +
                "data=" + data +
                ", \nlinks=" + links +
                ", \nmeta=" + meta +
                '}';
    }

    @Getter
    @Setter
    public static class Data {

        @JsonProperty("ScheduledPayment")
        private ScheduledPayment[] scheduledPayment;

        @Override
        public String toString() {
            return "Data{" +
                    "\nscheduledPayment=" + Arrays.toString(scheduledPayment) +
                    '}';
        }

        @Getter
        @Setter
        public static class ScheduledPayment {

            public enum ScheduledType {
                Arrival("Arrival"),
                Execution("Execution");

                @JsonValue
                private String value;

                ScheduledType(String value) {
                    this.value = value;
                }
            }

            @JsonProperty("AccountId")
            String accountId;

            @JsonProperty("ScheduledPaymentDateTime")
            String scheduledPaymentDateTime;

            @JsonProperty("ScheduledType")
            ScheduledType scheduledType;

            @JsonProperty("Reference")
            String reference;

            @JsonProperty("InstructedAmount")
            InstructedAmount instructedAmount;

            @JsonProperty("CreditorAgent")
            CreditorAgent creditorAgent;

            @JsonProperty("CreditorAccount")
            CreditorAccount creditorAccount;

            @Override
            public String toString() {
                return "ScheduledPayment{" +
                        "\naccountId='" + accountId + '\'' +
                        ",\nscheduledPaymentDateTime='" + scheduledPaymentDateTime + '\'' +
                        ",\nreference='" + reference + '\'' +
                        ",\ninstructedAmount=" + instructedAmount +
                        ",\ncreditorAgent=" + creditorAgent +
                        ",\ncreditorAccount=" + creditorAccount +
                        '}';
            }

            @Getter
            @Setter
            public static class InstructedAmount {
                @JsonProperty("Amount")
                String amount;

                @JsonProperty("Currency")
                String currency;

                @Override
                public String toString() {
                    return "InstructedAmount{" +
                            "\namount='" + amount + '\'' +
                            ",\ncurrency='" + currency + '\'' +
                            '}';
                }
            }

            @Getter
            @Setter
            public static class CreditorAgent {
                public enum SchemeName {
                    BICFI("UK.OBIE.BICFI");

                    @JsonValue
                    String value;

                    SchemeName(String value) {
                        this.value = value;
                    }
                }

                @JsonProperty("SchemeName")
                SchemeName schemeName;

                @JsonProperty("Identification")
                String identification;

                @Override
                public String toString() {
                    return "CreditorAgent{" +
                            "\nschemeName=" + schemeName +
                            ",\nidentification='" + identification + '\'' +
                            '}';
                }
            }

            @Getter
            @Setter
            public static class CreditorAccount {
                public enum SchemeName {
                    IBAN("UK.OBIE.IBAN"),
                    SortCodeAccountNumber("UK.OBIE.SortCodeAccountNumber"),
                    Other("UK.NWB.Other");

                    @JsonValue
                    String value;

                    SchemeName(String value) {
                        this.value = value;
                    }
                }

                @JsonProperty("SchemeName")
                SchemeName schemeName;

                @JsonProperty("Identification")
                String identification;

                @JsonProperty("Name")
                String name;

                @Override
                public String toString() {
                    return "CreditorAccount{" +
                            "\nschemeName=" + schemeName +
                            ",\nidentification='" + identification + '\'' +
                            ",\nname='" + name + '\'' +
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

        @Override
        public String toString() {
            return "Meta{" +
                    "\ntotalPages=" + totalPages +
                    '\n' + '}';
        }
    }
}
