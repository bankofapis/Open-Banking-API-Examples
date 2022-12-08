package com.bankofapis.constant;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class BrandConstants {
    private static final Map<Brand, Constants> brandConstantsMap;

    static {
        brandConstantsMap = new HashMap<>();
        for (Brand brand : Brand.values()) {
            brandConstantsMap.put(brand, new Constants(brand.fapiId));
        }
    }

    public static Constants getConstantsForBrand(String brand) {
        try {
            Brand brandValue = Brand.valueOf(brand.toUpperCase());
            return brandConstantsMap.get(brandValue);
        } catch(IllegalArgumentException e) {
            System.out.println("Brand not recognised, valid values are:");
            System.out.println(Arrays.stream(Brand.values()).map(Enum::name).collect(Collectors.joining("\n")));
            throw new RuntimeException(e);
        }
    }

    // Placeholder for holding brand specific constant values not intended for ENUM use
    private enum Brand {
        NWB("0015800000jfwxXAAQ"),
        NWBI("0015800001ZEZ1lAAH"),
        RBS("0015800000jfwxXAAQ"),
        UBN(""),
        UBR("");

        public final String fapiId;

        Brand(String fapiId) {
            this.fapiId = fapiId;
        }

    }
}
