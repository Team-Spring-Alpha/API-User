package br.com.compass.filmes.user.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ResourceNotFoundException extends AuthenticationException{

    public ResourceNotFoundException(String ex) {
        super(ex);
    }
}