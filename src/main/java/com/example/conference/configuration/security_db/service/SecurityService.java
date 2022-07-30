//package com.example.conference.configuration.security_db.service;
//
//import com.example.conference.configuration.security_db.service.MongoAuthUserDetailService;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Service;
//
//@Service
//public class SecurityService {
//
//    private final AuthenticationManager authenticationManager;
//
//    private final MongoAuthUserDetailService userDetailsService;
//
//    public SecurityService(AuthenticationManager authenticationManager, MongoAuthUserDetailService userDetailsService) {
//        this.authenticationManager = authenticationManager;
//        this.userDetailsService = userDetailsService;
//    }
//
//    public boolean login(String username, String password) {
//        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//
//        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
//
//        authenticationManager.authenticate(usernamePasswordAuthenticationToken);
//
//        if (usernamePasswordAuthenticationToken.isAuthenticated()) {
//            SecurityContextHolder.getContext()
//                    .setAuthentication(usernamePasswordAuthenticationToken);
//
//            return true;
//        }
//
//        return false;
//    }
//}
