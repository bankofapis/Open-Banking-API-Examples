package com.bankofapis.dto.accounts;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

@JsonInclude()
public class ConsentRequestPostDTO {
    @JsonProperty("Data")
    private DataDTO data;

    @JsonProperty("Risk")
    private JsonNode risk;

    public ConsentRequestPostDTO() {
        data = new DataDTO();
        risk = new ObjectMapper().createObjectNode();
    }

    private static class DataDTO {
        @JsonProperty("Permissions")
        public List<String> permissions;

        public DataDTO() {
            permissions = new ArrayList<>();
        }
    }

    public void addPermission(String permission) {
        data.permissions.add(permission);
    }
}
