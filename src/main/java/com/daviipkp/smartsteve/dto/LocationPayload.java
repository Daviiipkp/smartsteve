package com.daviipkp.smartsteve.dto;

public record LocationPayload(
        String _type,
        Double lat,
        Double lon,
        Long tst,
        Integer batt,
        String tid
) {}
