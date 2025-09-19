package com.example.bankcards.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CardBalanceException.class)
    public ResponseEntity<String> handleCardBalanceException(CardBalanceException ex) {
        return new ResponseEntity<>("Недостаточный баланс", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CardBlockRequestNotFoundException.class)
    public ResponseEntity<String> handleCardBlockRequestNotFoundException(CardBlockRequestNotFoundException ex) {
        return new ResponseEntity<>("Запрос на блокирование карты не найден", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CardNotFoundException.class)
    public ResponseEntity<String> handleCardNotFoundException(CardNotFoundException ex) {
        return new ResponseEntity<>("Карта не найдена", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CardOwnerException.class)
    public ResponseEntity<String> handleCardOwnerException(CardOwnerException ex) {
        return new ResponseEntity<>("Карта принадлежит другому пользователю", HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(IncorrectPasswordException.class)
    public ResponseEntity<String> handleIncorrectPasswordException(IncorrectPasswordException ex) {
        return new ResponseEntity<>("Не верный пароль", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<String> handleUserAlreadyExistException(UserAlreadyExistException ex) {
        return new ResponseEntity<>("Пользователь с таким логином уже существует", HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException ex) {
        return new ResponseEntity<>("Пользователь не найден", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<String> handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
        return new ResponseEntity<>("Ошибка доступа", HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleAllExceptions(RuntimeException ex) {
        return new ResponseEntity<>("Ошибка системы: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
