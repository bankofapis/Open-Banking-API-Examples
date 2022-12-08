package com.bankofapis.dto.accounts;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

@Getter
@Setter
public class GetAccountOffersDTO {

    @JsonProperty("Data")
    private Data data;

    @JsonProperty("Links")
    private Links links;

    @JsonProperty("Meta")
    private Meta meta;

    @Override
    public String toString() {
        return "GetAccountOffersDTO{" +
                "data=" + data +
                ",\nlinks=" + links +
                ",\nmeta=" + meta +
                '}';
    }

    @Getter
    @Setter
    public static class Data {

        @JsonProperty("Offer")
        Offer[] offer;

        @Override
        public String toString() {
            return "Data{" +
                    "\noffer=" + Arrays.toString(offer) +
                    '}';
        }

        @Getter
        @Setter
        public static class Offer {

            @JsonProperty("AccountId")
            String accountId;

            @JsonProperty("OfferId")
            String offerId;

            @JsonProperty("Description")
            String description;

            @JsonProperty("StartDateTime")
            String startDateTime;

            @JsonProperty("EndDateTime")
            String endDateTime;

            @JsonProperty("Rate")
            String rate;

            @JsonProperty("Term")
            String term;

            @JsonProperty("TransferFee")
            String transferFee;

            @Override
            public String toString() {
                return "Offer{" +
                        "\naccountId='" + accountId + '\'' +
                        ",\nofferId='" + offerId + '\'' +
                        ",\ndescription='" + description + '\'' +
                        ",\nstartDateTime='" + startDateTime + '\'' +
                        ",\nendDateTime='" + endDateTime + '\'' +
                        ",\nrate='" + rate + '\'' +
                        ",\nterm='" + term + '\'' +
                        ",\ntransferFee='" + transferFee + '\'' +
                        '}';
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
                    "totalPages=" + totalPages +
                    ",\nfirstAvailableDateTime='" + firstAvailableDateTime + '\'' +
                    ",\nlastAvailableDateTime='" + lastAvailableDateTime + '\'' +
                    '}';
        }
    }
}
