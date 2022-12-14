package br.com.compass.filmes.user.dto.payment.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode
public class RequestPaymentDTO {
    @NotBlank
    @JsonProperty("seller_id")
    private String sellerId;
    @NotNull
    private RequestPaymentCustomerDTO customer;
    @NotBlank
    @JsonProperty("payment_type")
    private String paymentType = "CREDIT_CARD";
    @NotBlank
    private String currency = "BRL";
    @JsonProperty("transaction_amount")
    private double transactionAmount;
    @NotBlank
    private RequestPaymentCreditCardDTO card;
}
