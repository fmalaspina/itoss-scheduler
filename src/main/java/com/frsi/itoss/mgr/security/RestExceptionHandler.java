package com.frsi.itoss.mgr.security;

import com.frsi.itoss.mgr.exceptions.IgnoreException;
import com.frsi.itoss.model.baseclasses.CustomValidationException;
import com.frsi.itoss.shared.ApiError;
import lombok.extern.java.Log;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.sql.SQLException;
import java.util.Locale;
import java.util.concurrent.TimeoutException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@Log
public class RestExceptionHandler extends ResponseEntityExceptionHandler {


//    @Override
//    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
//                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
//        String error = "Malformed JSON request";
//        return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, error, ex));
//    }

    @ExceptionHandler(IgnoreException.class)
    protected ResponseEntity<Object> handleIgnoredException(IgnoreException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return buildResponseEntity(new ApiError(HttpStatus.OK, ex.getMessage(), ex));
    }

    private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }



    @ExceptionHandler(CustomValidationException.class)
    public final ResponseEntity<Object> handleCustomValidationException(CustomValidationException ex,
                                                                        WebRequest request) {

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getValidationErrors().toString(),
                ex.getValidationErrors());
        // apiError.setMessage(ex.getMessage());
        return buildResponseEntity(apiError);

    }

    @ExceptionHandler(TimeoutException.class)
    public final ResponseEntity<Object> handleTimeoutException(TimeoutException ex,
                                                               WebRequest request) {

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "Connection timeout.",
                ex);
        // apiError.setMessage(ex.getMessage());
        return buildResponseEntity(apiError);

    }


    @ExceptionHandler(DataAccessException.class)
    public final ResponseEntity<Object> handleDataAccessException(DataAccessException ex,
                                                                  WebRequest request) {

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage().toString(),
                ex.getMessage());
        // apiError.setMessage(ex.getMessage());
        return buildResponseEntity(apiError);

    }

    @ExceptionHandler({ConstraintViolationException.class, ValidationException.class

    })
    public final ResponseEntity<Object> handleViolationException(Exception ex, WebRequest request) {
        // if (log.isLoggable(Level.SEVERE)) log.severe(ex.getMessage() + ex);
        StringBuilder errors = new StringBuilder();

        if (ex instanceof ConstraintViolation) {
            for (ConstraintViolation<?> violation : ((ConstraintViolationException) ex).getConstraintViolations()) {

                errors.append(violation.getRootBeanClass().getName() + " " + violation.getPropertyPath() + ": "
                        + violation.getMessage());

            }
        }

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), errors.toString());
        apiError.setMessage(ex.getMessage());
        return buildResponseEntity(apiError);

    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public final ResponseEntity<Object> handle(Exception e, Locale locale) {
        String error = "Duplicated record.";
        return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, error, e));
    }

    @ExceptionHandler(SQLException.class)
    public final ResponseEntity<Object> handleSQLException(Exception e, Locale locale) {
        String error = "SQL error" + e.getMessage();
        return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, error, e));
    }

    @ExceptionHandler(BadSqlGrammarException.class)
    public final ResponseEntity<Object> handleBadSQLGrammarException(Exception e, Locale locale) {
        String error = "SQL error - check your query or if metric exists in timeseries database";
        return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, error, e));
    }


    @ExceptionHandler(BadCredentialsException.class)
    public final ResponseEntity<Object> handleBadCredentialException(Exception ex, WebRequest request) {
        // if (log.isLoggable(Level.SEVERE)) log.severe(ex.getMessage() + ex);
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND);
        apiError.setMessage(ex.getMessage());
        return buildResponseEntity(apiError);

    }

    @ExceptionHandler(AuthenticationException.class)
    public final ResponseEntity<Object> handleAuthenticationException(Exception ex, WebRequest request) {
        // if (log.isLoggable(Level.SEVERE)) log.severe(ex.getMessage() + ex);
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND);
        apiError.setMessage(ex.getMessage());
        return buildResponseEntity(apiError);

    }

    //        @ExceptionHandler(Exception.class)
//    public final ResponseEntity<Object> handleGenericException(Exception ex, WebRequest request) {
//        // if (log.isLoggable(Level.SEVERE)) log.severe(ex.getMessage() + ex);
//        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND);
//        apiError.setMessage(ex.getMessage());
//        return buildResponseEntity(apiError);
//
//    }
//    @Override
//    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
//        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND);
//        apiError.setMessage(ex.getMessage());
//        return buildResponseEntity(apiError);
//    }
}