package com.scm.services;

public interface BlackListService {
	
    void addToBlacklist(String token);
    boolean isBlacklisted(String token);
    
}
