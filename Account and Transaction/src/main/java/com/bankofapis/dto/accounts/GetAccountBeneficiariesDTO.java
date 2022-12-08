package com.bankofapis.dto.accounts;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

@Getter
@Setter
public class GetAccountBeneficiariesDTO {
    @JsonProperty("Data")
    private Data data;

    @JsonProperty("Links")
    private Links links;

    @JsonProperty("Meta")
    private Meta meta;

    @Override
    public String toString() {
        return "GetAccountBeneficiariesDTO{" +
                "data=" + data +
                ", \nlinks=" + links +
                ", \nmeta=" + meta +
                '}';
    }

    @Getter
    @Setter
    public static class Data {

        @JsonProperty("Beneficiary")
        private Beneficiary[] beneficiary;

        @Override
        public String toString() {
            return "Data{" +
                    "\nbeneficiary=" + Arrays.toString(beneficiary) +
                    '}';
        }

        @Getter
        @Setter
        public static class Beneficiary {

            public enum BeneficiaryTypes {
                Trusted("Trusted"),
                Ordinary("Ordinary");

                @JsonValue
                private String value;

                BeneficiaryTypes(String value) {
                    this.value = value;
                }
            }

            @JsonProperty("AccountId")
            private String accountId;

            @JsonProperty("BeneficiaryId")
            private String beneficiaryId;

            @JsonProperty("Reference")
            private String reference;

            @JsonProperty("BeneficiaryType")
            private BeneficiaryTypes beneficiaryType;

            @JsonProperty("CreditorAgent")
            private CreditorAgent creditorAgent;

            @JsonProperty("CreditorAccount")
            private CreditorAccount creditorAccount;

            @Override
            public String toString() {
                return "Beneficiary{" +
                        "\naccountId='" + accountId + '\'' +
                        ", \nbeneficiaryId='" + beneficiaryId + '\'' +
                        ", \nreference='" + reference + '\'' +
                        ", \ncreditorAgent=" + creditorAgent +
                        ", \ncreditorAccount=" + creditorAccount +
                        ", \nbeneficiaryType=" + beneficiaryType +
                        '}';
            }

            @Getter
            @Setter
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
                            "\nschemeName=" + schemeName +
                            ", \nidentification='" + identification + '\'' +
                            '\n' + '}';
                }

            }

            @Getter
            @Setter
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
                            "\nschemeName=" + schemeName +
                            ", \nidentification='" + identification + '\'' +
                            ", \nname'" + name + '\'' +
                            '\n' + '}';
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
