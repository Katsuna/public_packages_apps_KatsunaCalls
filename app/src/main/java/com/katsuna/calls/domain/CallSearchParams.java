package com.katsuna.calls.domain;

public class CallSearchParams {

    public String number;
    public Integer limit;
    public Long idToExclude;

    public CallSearchParams() {
    }

    public CallSearchParams(String number, Integer limit, Long idToExclude) {
        this.number = number;
        this.limit = limit;
        this.idToExclude = idToExclude;
    }
}
