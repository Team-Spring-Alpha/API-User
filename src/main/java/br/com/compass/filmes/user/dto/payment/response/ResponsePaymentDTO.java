package br.com.compass.filmes.user.dto.payment.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponsePaymentDTO {

    @JsonProperty("payment_id")
    private String paymentId;
    @JsonProperty("seller_id")
    private String sellerId;
    @JsonProperty("transaction_amount")
    private Double transactionAmount;
    private String currency;
    private String status;
    @JsonProperty("received_at")
    private LocalDateTime receivedAt;
    private ResponseProcessPaymentDTO authorization;
}
