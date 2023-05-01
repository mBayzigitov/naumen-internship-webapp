package ru.naumen.naumeninternshipwebapp.controller.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.naumen.naumeninternshipwebapp.exception.NamePatternException;

@RestControllerAdvice
public class PersonExcHandler {

    @ExceptionHandler
    private ResponseEntity<String> handleInvalidNamePattern(NamePatternException npe) {
        return new ResponseEntity<>("Неверный формат ввода, введите имя одним словом с заглавной буквы кириллицей или латиницей",
                HttpStatus.NOT_FOUND);
    }

}
