package com.hanami.sdk.router;

public enum HttpMethod {
    get("GET"), post("POST"), put("PUT"), patch("PATCH"), delete("DELETE");

    private String verb;

    HttpMethod(String verb) {
        this.verb = verb;
    }

    public String getVerb() {
        return verb;
    }
}
