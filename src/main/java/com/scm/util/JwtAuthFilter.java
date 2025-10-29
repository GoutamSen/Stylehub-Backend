package com.scm.util;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.scm.services.BlackListService;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;
	@Autowired private BlackListService blackListService;

	public JwtAuthFilter(JwtUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException, java.io.IOException {
		
		
	    // 🚨 Skip filtering for login/signup
		String path = request.getServletPath();
	    if (path.startsWith("/auth")) {
	        filterChain.doFilter(request, response);
	        return;
	    }
		
		String authHeader = request.getHeader("Authorization");
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);
			
			// 🚨 Check if token is blacklisted
		    if (blackListService.isBlacklisted(token)) {
		        throw new RuntimeException("Token is invalid (logged out)");
		    }
		    
			if (jwtUtil.validateToken(token)) {
				String username = jwtUtil.extractUsername(token);
				String role = jwtUtil.extractRole(token);

				UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, null,
						List.of(new SimpleGrantedAuthority("ROLE_" + role)));
				SecurityContextHolder.getContext().setAuthentication(auth);
			}
		}
		filterChain.doFilter(request, response);
	}

}
