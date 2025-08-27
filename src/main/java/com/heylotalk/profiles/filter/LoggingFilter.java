package com.heylotalk.profiles.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

@Component
@Order(1)
@Slf4j
public class LoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {


        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        long startTime = System.currentTimeMillis();

        String requestBody = new String(requestWrapper.getContentAsByteArray());

        log.info("Request received is: {} {}, Body: {}",
                request.getMethod(),
                request.getRequestURI(),
                requestBody);

        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            String responseBody = new String(responseWrapper.getContentAsByteArray());
            long endTime = System.currentTimeMillis();
            log.info("Response is: {} {}, StatusCode: {}, Body: {}, Time taken: {} ms",
                    request.getMethod(),
                    request.getRequestURI(),
                    responseWrapper.getStatus(),
                    responseBody, endTime - startTime);

            responseWrapper.copyBodyToResponse();
        }
    }
}
