package com.bankofapis.constant;

public class Constants {
    private final String fapiId;
    public Constants(String fapiId) {
        this.fapiId = fapiId;
    }

    /**
    * The unique id of the ASPSP to which the request is issued. The unique id will be issued by OB. The value for NatWest is 0015800000jfwxXAAQ.
    */
    public String getFapiId() {
        return fapiId;
    }
}
