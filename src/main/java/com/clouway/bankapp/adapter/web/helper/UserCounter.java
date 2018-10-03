package com.clouway.bankapp.adapter.web.helper;

import com.clouway.bankapp.core.SessionRepository;

public class UserCounter {

    private SessionRepository sessionRepo;

    public UserCounter(SessionRepository sessionRepo) {
        this.sessionRepo = sessionRepo;
    }

    public int getCount(){
        return sessionRepo.getActiveSessionsCount();
    }

}
