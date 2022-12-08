package com.bankofapis.dto.accounts;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

@Getter
@Setter
public class GetAccountsDTO {

    @JsonProperty("Data")
    private Data data;

    @JsonProperty("Links")
    private Links links;

    @JsonProperty("Meta")
    private Meta meta;

    @Getter
    @Setter
    public static class Data {

        @JsonProperty("Account")
        private Data_Account[] accounts;

        @Override
        public String toString() {
            return "Data{\n" +
                    "accounts=" + Arrays.toString(accounts) +
                    '}' + '\n';
        }
    }

    @Getter
    @Setter
    public static class Data_Account {

        @Getter
        public enum AccountTypes {
            Business("Business"),
            Personal("Personal");

            @JsonValue
            @Setter
            private String value;

            AccountTypes(String value) {
                this.value = value;
            }
        }

        public enum AccountSubTypes {
            ChargeCard("ChargeCard"),
            CreditCard("CreditCard"),
            CurrentAccount("CurrentAccount"),
            EMoney("EMoney"),
            Loan("Loan"),
            Mortgage("Mortage"),
            PrePaidCard("PrePaidCard"),
            Savings("Savings");

            @JsonValue
            private String value;

            AccountSubTypes(String value) {
                this.value = value;
            }
        }

        public enum SwitchStatus {
            NotSwitched("UK.CASS.NotSwitched"),
            SwitchCompleted("UK.CASS.SwitchCompleted");

            @JsonValue
            private String value;

            SwitchStatus(String value) {
                this.value = value;
            }
        }

        @JsonProperty("AccountId")
        private String accountId;

        @JsonProperty("Currency")
        private String currency;

        @JsonProperty("AccountType")
        private AccountTypes accountType;

        @JsonProperty("AccountSubType")
        private AccountSubTypes accountSubType;

        @JsonProperty("Description")
        private String description;

        @JsonProperty("Nickname")
        private String nickname;

        @JsonProperty("SwitchStatus")
        private SwitchStatus switchStatus;

        @JsonProperty("Account")
        private Data_Account_Account[] account;

        @JsonProperty("Servicer")
        private Servicer servicer;

        @Override
        public String toString() {
            return "Account{" +
                    "\n accountId='" + accountId + '\'' +
                    ",\n currency='" + currency + '\'' +
                    ",\n accountType='" + accountType + '\'' +
                    ",\n accountSubType='" + accountSubType + '\'' +
                    ",\n description='" + description + '\'' +
                    ",\n nickname='" + nickname + '\'' +
                    ",\n switchStatus='" + switchStatus + '\'' +
                    ",\n account=" + Arrays.toString(account) +
                    ",\n servicer=" + servicer + '\n' +
                    '}';
        }
    }

    @Getter
    @Setter
    public static class Data_Account_Account {

        public enum SchemeNames {
            IBAN("UK.OBIE.IBAN"),
            SortCodeAccountNumber("UK.OBIE.SortCodeAccountNumber"),
            CurrencyAccount("UK.NWB.CurrencyAccount"),
            // Minor tweaks for InvalidFormatException while testing code for SIT(Non-Prod) Environment
            PAN("UK.OBIE.PAN");;

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

        @JsonProperty("SecondaryIdentification")
        private String secondaryIdentification;

        @JsonProperty("Name")
        private String name;

        @Override
        public String toString() {
            return "Data_Account_Account{" +
                    "schemeName='" + schemeName + '\'' +
                    ", identification='" + identification + '\'' +
                    ", secondaryIdentification='" + secondaryIdentification + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    @Getter
    @Setter
    public static class Servicer {

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
            return "Data_Account_Servicer{" +
                    "schemeName='" + schemeName + '\'' +
                    ", identification='" + identification + '\'' +
                    '}';
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
                    "totalPages=" + totalPages +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "AccountsResponseDTO{" +
                "data=" + data +
                ", links=" + links +
                ", meta=" + meta +
                '}';
    }
}
