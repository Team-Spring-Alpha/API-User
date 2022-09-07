package br.com.compass.filmes.cliente.service;

import br.com.compass.filmes.cliente.dto.apiMovie.ResponseApiMovieManager;
import br.com.compass.filmes.cliente.enums.GenresEnum;
import br.com.compass.filmes.cliente.enums.ProvidersEnum;
import br.com.compass.filmes.cliente.client.MovieSearchProxy;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class MovieService {
    @Autowired
    private MovieSearchProxy movieSearchProxy;

    public List<ResponseApiMovieManager> findMoviesRecommendations(Long movieId) {
        try {
            return movieSearchProxy.getMovieByRecommendation(movieId);
        } catch (FeignException.FeignClientException.NotFound exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    public List<ResponseApiMovieManager> findByFilters(GenresEnum movieGenre, LocalDate dateGte, LocalDate dateLte, ProvidersEnum movieProvider,
                                                       List<String> moviePeoples, String movieName) {
        return movieSearchProxy.getMovieSearchByFilters(movieGenre, dateGte, dateLte, movieProvider, moviePeoples, movieName);
    }
}
