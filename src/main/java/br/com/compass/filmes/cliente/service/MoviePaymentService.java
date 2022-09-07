package br.com.compass.filmes.cliente.service;

import br.com.compass.filmes.cliente.dto.apiAllocationHistory.RequestAllocation;
import br.com.compass.filmes.cliente.dto.apiAllocationHistory.RequestAllocationMovie;
import br.com.compass.filmes.cliente.dto.apiMovieManager.RequestMoviePayment;
import br.com.compass.filmes.cliente.dto.apiPayment.request.RequestPayment;
import br.com.compass.filmes.cliente.dto.apiPayment.request.RequestPaymentCreditCard;
import br.com.compass.filmes.cliente.dto.apiPayment.request.RequestPaymentCustomer;
import br.com.compass.filmes.cliente.dto.apiPayment.response.*;
import br.com.compass.filmes.cliente.dto.user.response.apiMovie.ResponseMovieById;
import br.com.compass.filmes.cliente.entities.UserEntity;
import br.com.compass.filmes.cliente.entities.CreditCardEntity;
import br.com.compass.filmes.cliente.enums.UserEnum;
import br.com.compass.filmes.cliente.enums.MovieLinks;
import br.com.compass.filmes.cliente.exceptions.BuyMovieNotFoundException;
import br.com.compass.filmes.cliente.exceptions.UserNotFoundException;
import br.com.compass.filmes.cliente.exceptions.CreditCardNotFoundException;
import br.com.compass.filmes.cliente.exceptions.RentMovieNotFoundException;
import br.com.compass.filmes.cliente.proxy.GatewayProxy;
import br.com.compass.filmes.cliente.proxy.MovieSearchProxy;
import br.com.compass.filmes.cliente.rabbitMq.MessageHistory;
import br.com.compass.filmes.cliente.repository.UserRepository;
import br.com.compass.filmes.cliente.util.Md5;
import br.com.compass.filmes.cliente.util.ValidRequestMoviePayment;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MoviePaymentService {

    private LocalTime tokenExpirationTime;
    private String authToken;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final MovieSearchProxy movieSearchProxy;
    private final GatewayProxy gatewayProxy;
    private final MessageHistory messageHistory;
    private final Md5 md5;
    private final ValidRequestMoviePayment validRequestMoviePayment;

    public ResponseGatewayReproved post(RequestMoviePayment requestMoviePayment) {
        validRequestMoviePayment.validRequestMoviePayment(requestMoviePayment);
        UserEntity userEntity = userRepository.findById(requestMoviePayment.getUserId()).orElseThrow(() -> new UserNotFoundException("userId: "+ requestMoviePayment.getUserId()));
        CreditCardEntity creditCard = getCreditCard(requestMoviePayment, userEntity);

        List<ResponseMoviePaymentProcess> moviePaymentProcessList = new ArrayList<>();
        Double amount = 0.0;

        if (requestMoviePayment.getMovies().getBuy() != null) {
            amount = processTheBuyMovieList(requestMoviePayment, moviePaymentProcessList, amount);
        }

        if (requestMoviePayment.getMovies().getRent() != null) {
            amount = processTheRentMovieList(requestMoviePayment, moviePaymentProcessList, amount);
        }

        UserEnum randomUserEnum = UserEnum.getRandomClientEnum();
        if (this.tokenExpirationTime == null) {
            getToken(randomUserEnum);
        }

        if (LocalTime.now().isAfter(tokenExpirationTime)) {
            getToken(randomUserEnum);
        }


        RequestPaymentCustomer paymentCustomer = buildCustomer(userEntity);

        RequestPayment requestPayment = buildRequesPayment(creditCard, amount, randomUserEnum, paymentCustomer);

        ResponsePayment payment = gatewayProxy.getPayment(this.authToken, requestPayment);

        List<RequestAllocationMovie> requestAllocationMovieList = getRequestAllocationMovies(moviePaymentProcessList);
        RequestAllocation requestAllocation = buildRequestAllocation(requestMoviePayment, userEntity, payment, requestAllocationMovieList);
        messageHistory.sendMessage(requestAllocation);

        if (payment.getStatus().equals("REPROVED")) {
            String cause = payment.getAuthorization().getReasonMessage();
            return new ResponseGatewayReproved(cause);
        }

        return new ResponseGatewayOk(moviePaymentProcessList);
    }

    private Double processTheRentMovieList(RequestMoviePayment requestMoviePayment, List<ResponseMoviePaymentProcess> moviePaymentProcessList, Double amount) {
        for (int i = 0; i < requestMoviePayment.getMovies().getRent().size(); i++) {
            Long movieId = requestMoviePayment.getMovies().getRent().get(i);
            ResponseMovieById proxyMovieById = movieSearchProxy.getMovieById(requestMoviePayment.getMovies().getRent().get(i));
            String movieTitle = proxyMovieById.getMovieName();
            try {
                String movieStore = proxyMovieById.getJustWatch().getRent().get(0).getStore();
                Double rentPrice = proxyMovieById.getJustWatch().getRent().get(0).getPrice();
                amount += rentPrice;
                buildMoviesProcessList(movieId, movieTitle, movieStore, moviePaymentProcessList);

            } catch (NullPointerException exception) {
                throw new RentMovieNotFoundException("movie id: "+ movieId);
            }
        }
        return amount;
    }

    private Double processTheBuyMovieList(RequestMoviePayment requestMoviePayment, List<ResponseMoviePaymentProcess> moviePaymentProcessList, Double amount) {
        for (int i = 0; i < requestMoviePayment.getMovies().getBuy().size(); i++) {
            Long movieId = requestMoviePayment.getMovies().getBuy().get(i);
            ResponseMovieById proxyMovieById = movieSearchProxy.getMovieById(movieId);
            String movieTitle = proxyMovieById.getMovieName();
            try {
                String movieStore = proxyMovieById.getJustWatch().getBuy().get(0).getStore();
                Double buyPrice = proxyMovieById.getJustWatch().getBuy().get(0).getPrice();
                amount += buyPrice;
                buildMoviesProcessList(movieId, movieTitle, movieStore, moviePaymentProcessList);
            } catch (NullPointerException exception) {
                throw new BuyMovieNotFoundException("movie id: " + movieId);
            }
        }
        return amount;
    }

    private MovieLinks getMovieLinkFromlabel(String label) {
        return MovieLinks.valueOfLabel(label);
    }

    private void buildMoviesProcessList(Long movieId, String movieTitle, String movieStore, List<ResponseMoviePaymentProcess> moviePaymentProcessList) {
        ResponseMoviePaymentProcess moviePaymentProcess = new ResponseMoviePaymentProcess();
        moviePaymentProcess.setMovieId(movieId);
        moviePaymentProcess.setTitle(movieTitle);
        MovieLinks movieLinks = getMovieLinkFromlabel(movieStore);
        moviePaymentProcess.setLink(movieLinks.getLink());
        moviePaymentProcessList.add(moviePaymentProcess);
    }
    private List<RequestAllocationMovie> getRequestAllocationMovies(List<ResponseMoviePaymentProcess> response) {
        List<RequestAllocationMovie> requestAllocationMovieList = new ArrayList<>();
        for (ResponseMoviePaymentProcess responseMoviePaymentProcess : response) {
            RequestAllocationMovie allocationMovie = new RequestAllocationMovie(responseMoviePaymentProcess.getMovieId(), responseMoviePaymentProcess.getTitle());
            requestAllocationMovieList.add(allocationMovie);
        }
        return requestAllocationMovieList;
    }

    private RequestAllocation buildRequestAllocation(RequestMoviePayment requestMoviePayment, UserEntity userEntity, ResponsePayment payment, List<RequestAllocationMovie> requestAllocationMovieList) {
        String userId = userEntity.getId();
        String cardNumber = requestMoviePayment.getCreditCardNumber();
        String status = payment.getStatus();
        return new RequestAllocation(userId, cardNumber, requestAllocationMovieList, status);
    }

    private void getToken(UserEnum randomUserEnum) {
        ResponseAuth authToken = gatewayProxy.getAuthToken(randomUserEnum);
        this.tokenExpirationTime = LocalTime.now().plusSeconds(Long.parseLong(authToken.getExpiresIn()));
        this.authToken = authToken.getToken();
    }

    private RequestPayment buildRequesPayment(CreditCardEntity creditCard, Double amount, UserEnum randomUserEnum, RequestPaymentCustomer paymentCustomer) {
        RequestPayment requestPayment = new RequestPayment();
        requestPayment.setSellerId(randomUserEnum.getSellerId());
        requestPayment.setCustomer(paymentCustomer);
        requestPayment.setTransactionAmount(amount);
        RequestPaymentCreditCard requestCreditCard = modelMapper.map(creditCard, RequestPaymentCreditCard.class);
        requestCreditCard.setClientCreditCardNumber(md5.ToMd5(creditCard.getNumber()));
        requestPayment.setCard(requestCreditCard);
        return requestPayment;
    }

    private CreditCardEntity getCreditCard(RequestMoviePayment requestMoviePayment, UserEntity userEntity) {
        for (int i = 0; i < userEntity.getCards().size(); i++) {
            if (Objects.equals(userEntity.getCards().get(i).getNumber(), requestMoviePayment.getCreditCardNumber())) {
                return userEntity.getCards().get(i);
            }
        }
        throw new CreditCardNotFoundException("credit card id: " + requestMoviePayment.getCreditCardNumber());
    }

    private RequestPaymentCustomer buildCustomer(UserEntity userEntity) {
        return new RequestPaymentCustomer(userEntity.getCpf());
    }
}