package com.frsi.itoss;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class SessionFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
        try {
//            var collectorID = request.getHeader("x-collector-id");
//            if (collectorID != null && !collectorID.isBlank()) {
//                var remoteIP = request.getRemoteAddr();
//
//
//                System.out.println(collectorID + " " + remoteIP);
//            }
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                var user = (User) auth.getPrincipal();
//                System.out.println(user.getUsername());
            }
        } catch (Exception ignore) {

        }
        filterChain.doFilter(request, response);

	}

}
