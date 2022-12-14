package br.com.compass.filmes.user.enums;

import java.util.Random;

public enum PaymentVendorEnum {
    NOEEME("036dbc65-d1e3-4070-ba99-d7b658fb216b", "client_id_noeeme", "5a90a7ec-dd17-470e-9f12-b4a8143c9cc0"),
    PEDRO("adde23eb-2bc3-437f-bdba-f8f96f8c4014", "client_id_pedro", "2ef97ff2-1df7-471a-9b6a-8a296790aa69");

    private final String apiKey;
    private final String clientId;
    private final String sellerId;

    PaymentVendorEnum(String apiKey, String clientId, String sellerId) {
        this.apiKey = apiKey;
        this.clientId = clientId;
        this.sellerId = sellerId;
    }

    public static PaymentVendorEnum getRandomClientEnum() {
        Random random = new Random();
        int randomIndice = random.nextInt(PaymentVendorEnum.values().length);
        return PaymentVendorEnum.values()[randomIndice];
    }

    public String getApiKey() {
        return this.apiKey;
    }

    public String getClientId() {
        return this.clientId;
    }

    public String getSellerId() {
        return this.sellerId;
    }
    }
