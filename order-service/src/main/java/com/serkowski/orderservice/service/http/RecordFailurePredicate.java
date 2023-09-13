package com.serkowski.orderservice.service.http;

import com.serkowski.orderservice.model.error.ApiCallException;
import org.springframework.web.reactive.function.client.WebClientException;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;

public class RecordFailurePredicate implements Predicate<Throwable> {
    @Override
    public boolean test(Throwable e) {
        return recordFailures(e);
    }

    private boolean recordFailures(Throwable throwable) {
        return (throwable instanceof ApiCallException ex && ex.getStatus().is5xxServerError())
                || throwable instanceof TimeoutException || throwable instanceof IOException || throwable instanceof WebClientException;
    }
}
