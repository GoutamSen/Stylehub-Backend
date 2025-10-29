package com.scm.servicesimpl;

import java.util.HashSet;
import java.util.Set;
import org.springframework.stereotype.Service;
import com.scm.services.BlackListService;

@Service
public class RedisBlackListServiceImpl implements BlackListService {
    
	 private final Set<String> blacklist = new HashSet<>();

	    @Override
	    public void addToBlacklist(String token) {
	        blacklist.add(token);
	    }

	    @Override
	    public boolean isBlacklisted(String token) {
	        return blacklist.contains(token);
	    }
	    
}
