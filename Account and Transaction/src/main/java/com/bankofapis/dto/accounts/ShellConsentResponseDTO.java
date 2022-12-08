package com.bankofapis.dto.accounts;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;

import java.util.Date;
import java.util.List;

public class ShellConsentResponseDTO {
    @JsonProperty("Data")
    private DataDTO data;

    @JsonProperty("Risk")
    @JsonSerialize(using = NullSerializer.class)
    private RiskDTO risk;

    @JsonProperty("Links")
    @JsonSerialize(using = NullSerializer.class)
    private LinksDTO links;

    @JsonProperty("Meta")
    @JsonSerialize(using = NullSerializer.class)
    private MetaDTO meta;

    public ShellConsentResponseDTO() {
        this.data = new DataDTO();
        this.risk = new RiskDTO();
        this.links = new LinksDTO();
        this.meta = new MetaDTO();
    }

    public String getConsentId() {
        return data.getConsentId();
    }

    public void setConsentId(String consentId) {
        data.setConsentId(consentId);
    }

    private static class DataDTO {
        @JsonProperty("ConsentId")
        private String consentId;

        @JsonProperty("Permissions")
        private List<String> permissions;

        @JsonProperty("CreationDateTime")
        private Date creationDate;

        @JsonProperty("StatusUpdateDateTime")
        private Date statusUpdateTime;

        @JsonProperty("Status")
        private String status;

        public DataDTO() {
            consentId = "";
        }

        public String getConsentId() {
            return consentId;
        }

        public void setConsentId(String consentId) {
            this.consentId = consentId;
        }
    }

    private static class RiskDTO {

    }

    private static class LinksDTO {
        @JsonProperty("Self")
        private String self;
    }

    private static class MetaDTO {

    }
}
