package com.katsuna.calls.domain;

public class CallSearchParams {

    public final String number;
    public final Integer limit;
    public final Long idToExclude;

    public CallSearchParams(String number, Integer limit, Long idToExclude) {
        this.number = number;
        this.limit = limit;
        this.idToExclude = idToExclude;
    }
}
