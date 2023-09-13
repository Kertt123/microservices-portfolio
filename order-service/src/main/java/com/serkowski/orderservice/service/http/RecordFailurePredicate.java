package com.serkowski.orderservice.service.http;

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
        return (throwable instanceof TimeoutException || throwable instanceof IOException
                || throwable instanceof WebClientException);
    }
}
