package br.com.compass.filmes.user.dto.payment.response;

import lombok.Data;

@Data
public class ResponseMoviePaymentProcessDTO {
    private Long movieId;
    private String title;
    private String link;
}