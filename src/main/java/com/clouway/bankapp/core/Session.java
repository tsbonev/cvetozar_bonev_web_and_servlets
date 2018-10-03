package com.clouway.bankapp.core;

import java.sql.Timestamp;

public class Session {

    private int userId;
    private String sessionId;
    private Timestamp expiresOn;
    private boolean authenticated;

    public Session(){}

    public Session(int userId, String sessionId, Timestamp expiresOn, boolean authenticated) {
        this.userId = userId;
        this.sessionId = sessionId;
        this.expiresOn = expiresOn;
        this.authenticated = authenticated;
    }

    public int getUserId() {
        return userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public Timestamp getExpiresOn() {
        return expiresOn;
    }

    public void setExpiresOn(Timestamp expiresOn) {
        this.expiresOn = expiresOn;
    }

    public void setAuthenticated(boolean authentication){
        this.authenticated = authentication;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }
}
