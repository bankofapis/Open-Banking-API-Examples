package com.bankofapis.dto.accounts;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

@Getter @Setter
public class GetAccountProductDTO {
    @JsonProperty("Data")
    private GetAccountProductDTO.Data data;

    @JsonProperty("Links")
    private GetAccountProductDTO.Links links;

    @JsonProperty("Meta")
    private GetAccountProductDTO.Meta meta;

    public GetAccountProductDTO.Data getData() {
        return data;
    }
    @Getter
    @Setter
    public static class Data {

        @JsonProperty("Product")
        private GetAccountProductDTO.Product[] Product;

        @Override
        public String toString() {
            return "Data{" +
                    "Product=" + Arrays.toString(Product) +
                    '}';
        }
    }
    @Getter @Setter
    public static class Product {

        public enum ProductType {
            PersonalCurrentAccount("PersonalCurrentAccount"),
            BusinessCurrentAccount("BusinessCurrentAccount"),
            CommercialCreditCard("CommercialCreditCard"),
            Other("Other");

            @JsonValue
            private String value;

            ProductType(String value) {
                this.value = value;
            }
        }

        @JsonProperty("AccountId")
        private String accountId;

        @JsonProperty("ProductId")
        private String productId;

        @JsonProperty("ProductType")
        private ProductType productType;

        @JsonProperty("MarketingStateId")
        private String marketingStateId;

        @JsonProperty("SecondaryProductId")
        private String secondaryProductId;

        @JsonProperty("OtherProductType")
        private GetAccountProductDTO.OtherProductType otherProductType;

        @JsonProperty("ProductName")
        private String productName;

        @Override
        public String toString() {
            return "Product{" +
                    "accountId='" + accountId + '\'' +
                    ", productId='" + productId + '\'' +
                    ", productType=" + productType +
                    ", marketingStateId='" + marketingStateId + '\'' +
                    ", secondaryProductId='" + secondaryProductId + '\'' +
                    ", otherProductType=" + otherProductType +
                    ", productName='" + productName + '\'' +
                    '}';
        }
    }
    @Getter @Setter
    public static class OtherProductType {
        @JsonProperty("Name")
        private String name;

        @JsonProperty("Description")
        private String description;

        @Override
        public String toString() {
            return "OtherProductType{" +
                    "name='" + name + '\'' +
                    ", description='" + description + '\'' +
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
        return "GetAccountProductDTO{" +
                "data=" + data +
                ", links=" + links +
                ", meta=" + meta +
                '}';
    }
}
