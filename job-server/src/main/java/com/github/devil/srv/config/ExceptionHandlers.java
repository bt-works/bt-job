package com.github.devil.srv.config;

import com.github.devil.srv.dto.response.Resp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;

/**
 * @author eric.yao
 * @date 2021/2/18
 **/
@Slf4j
@RestControllerAdvice
public class ExceptionHandlers {

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = TypeMismatchException.class)
    public Resp handleMethodError(TypeMismatchException exception){
        log.error("params error,",exception);
        return new Resp(HttpStatus.BAD_REQUEST.value(),null,exception.getMessage());
    }

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Resp handleValid(MethodArgumentNotValidException exception){
        log.error("params error,",exception);
        return new Resp(HttpStatus.BAD_REQUEST.value(),null,exception.getMessage());
    }


    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = ServletRequestBindingException.class)
    public Resp handleValid(ServletRequestBindingException exception){
        log.error("params error,",exception);
        return new Resp(HttpStatus.BAD_REQUEST.value(),null,exception.getMessage());
    }


    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = Exception.class)
    public Resp handleValid(Exception exception){
        log.error("params error,",exception);
        return new Resp(HttpStatus.INTERNAL_SERVER_ERROR.value(),null,exception.getMessage());
    }

}
