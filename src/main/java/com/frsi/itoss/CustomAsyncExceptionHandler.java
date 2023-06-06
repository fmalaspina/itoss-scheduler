package com.frsi.itoss;

import lombok.extern.java.Log;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;
import java.util.logging.Level;

@Log
public class CustomAsyncExceptionHandler
        implements AsyncUncaughtExceptionHandler {

    @Override
    public void handleUncaughtException(
            Throwable throwable, Method method, Object... obj) {
        if (log.isLoggable(Level.SEVERE)) log.severe("Problem in itoss async task");
        if (log.isLoggable(Level.SEVERE)) log.severe("Exception message - " + throwable.getMessage());
        if (log.isLoggable(Level.SEVERE)) log.severe("Method name - " + method.getName());
        for (Object param : obj) {
            if (log.isLoggable(Level.SEVERE)) log.severe("Parameter value - " + param);
        }
    }

}