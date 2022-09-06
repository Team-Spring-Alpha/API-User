package br.com.compass.filmes.user.builders;

import br.com.compass.filmes.user.dto.user.request.RequestUserDTO;
import br.com.compass.filmes.user.entities.UserEntity;
import br.com.compass.filmes.user.entities.CreditCardEntity;
import br.com.compass.filmes.user.enums.UserCategoryEnum;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserEntityBuilder {

    private UserEntity userEntity;

    public UserEntityBuilder() {
    }

    public static UserEntityBuilder one() {
        UserEntityBuilder builder = new UserEntityBuilder();
        builder.userEntity = new UserEntity();

        builder.userEntity.setId("1");
        builder.userEntity.setName("Jetosvaldo");
        builder.userEntity.setCpf("686.751.800-12");
        builder.userEntity.setEmail("yosope1626@wnpop.com");
        builder.userEntity.setPassword("123");
        builder.userEntity.setBlocked(false);
        builder.userEntity.setBirthDate(LocalDate.now());

        List<UserCategoryEnum> categoryEnumList = new ArrayList<>();
        categoryEnumList.add(UserCategoryEnum.ACAO);
        builder.userEntity.setCategories(categoryEnumList);

        List<CreditCardEntity> creditCardEntityList = CreditCardEntityBuilder.one().list();
        builder.userEntity.setCards(creditCardEntityList);

        return builder;
    }

    public UserEntityBuilder withId(String id) {
        this.userEntity.setId(id);
        return this;
    }

    public UserEntityBuilder withClientName(String clientName) {
        this.userEntity.setName(clientName);
        return this;
    }

    public UserEntityBuilder withClientCpf(String clientCpf) {
        this.userEntity.setCpf(clientCpf);
        return this;
    }

    public UserEntityBuilder withClientEmail(String clientEmail) {
        this.userEntity.setEmail(clientEmail);
        return this;
    }

    public UserEntityBuilder withClientPassword(String clientPassword) {
        this.userEntity.setPassword(clientPassword);
        return this;
    }

    public UserEntityBuilder withClientIsBlocked(boolean clientIsBlocked) {
        this.userEntity.setBlocked(clientIsBlocked);
        return this;
    }

    public UserEntityBuilder withClientBirthDate(LocalDate clientBirthDate) {
        this.userEntity.setBirthDate(clientBirthDate);
        return this;
    }

    public UserEntityBuilder withClientCategory(List<UserCategoryEnum> categoryEnumList) {
        this.userEntity.setCategories(categoryEnumList);
        return this;
    }

    public UserEntityBuilder withCreditCard(List<CreditCardEntity> creditCardEntity) {
        this.userEntity.setCards(creditCardEntity);
        return this;
    }

    public UserEntityBuilder withRequestClient(RequestUserDTO requestUserDTO) {
        this.userEntity.setId("idTeste");
        this.userEntity.setName(requestUserDTO.getName());
        this.userEntity.setCpf(requestUserDTO.getCpf());
        this.userEntity.setEmail(requestUserDTO.getEmail());
        this.userEntity.setPassword(requestUserDTO.getPassword());
        this.userEntity.setBlocked(false);
        this.userEntity.setBirthDate(requestUserDTO.getBirthDate());

        this.userEntity.setCategories(requestUserDTO.getCategory().stream().map(
                UserCategoryEnum::valueOf
        ).collect(Collectors.toList()));


        this.userEntity.setCards(requestUserDTO.getCards().stream().map(
                requestCreditCard -> CreditCardEntityBuilder.one().withRequestCreditCard(requestCreditCard).now()
                ).collect(Collectors.toList()));

        return this;
    }

    public UserEntity now() {
        return this.userEntity;
    }

}