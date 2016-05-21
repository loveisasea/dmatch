package com.fym.core.web.filter;

import com.fym.core.util.HttpUtil;
import com.fym.core.util.StringUtil;
import com.fym.core.web.filter.obj.JsonReadHttpServletRequest;
import com.fym.core.web.filter.obj.JsonReadHttpServletResponse;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

/**
 * Owned by Planck System
 * Created by fengy on 2016/2/18.
 */
public class JsonRecordFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(JsonRecordFilter.class);

    private static final Set<String> MULTI_READ_HTTP_METHODS = new TreeSet<String>(
            String.CASE_INSENSITIVE_ORDER) {
        {
            add("PUT");
            add("POST");
        }
    };

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        if (servletRequest instanceof HttpServletRequest) {
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            if (MULTI_READ_HTTP_METHODS.contains(request.getMethod())) {
                String header = ((HttpServletRequest) servletRequest)
                        .getHeader("Content-Type");
                if (LOGGER.isInfoEnabled()) {
                    if (header.contains("application/json")) {
                        JsonReadHttpServletRequest jsonReadHttpServletRequest = new JsonReadHttpServletRequest(
                                request);
                        JsonReadHttpServletResponse jsonReadHttpServletResponse = new JsonReadHttpServletResponse(
                                (HttpServletResponse) servletResponse);

                        this.printRequest(jsonReadHttpServletRequest);
                        filterChain.doFilter(jsonReadHttpServletRequest,
                                jsonReadHttpServletResponse);
                        this.printResponse(jsonReadHttpServletRequest,
                                jsonReadHttpServletResponse);

                        String jsonResponseString = new String(
                                jsonReadHttpServletResponse.getBytes(),
                                servletResponse.getCharacterEncoding());
                        writeJsonIntoResponse(servletResponse,
                                jsonResponseString, null);

                        return;
                    }
                }
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private void writeJsonIntoResponse(final ServletResponse response,
                                       final String responseBody, final ObjectMapper objectMapper)
            throws IOException {

        // escreve o json
        response.getOutputStream().write(
                (responseBody + "\r\n").getBytes(response
                        .getCharacterEncoding()));
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {
    }

    private void printRequest(JsonReadHttpServletRequest request) {
        try {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(String.format("收到请求:%s 方法:%s 参数:%s 从%s",
                        request.getRequestURI(), request.getMethod(),
                        request.getQueryString(), HttpUtil.getIpAddr(request)));
                BufferedReader reader = request.getReader();
                String inLine = null;
                while ((inLine = reader.readLine()) != null) {
                    LOGGER.info(inLine);
                }
            }
        } catch (Exception e) {
            LOGGER.warn("警告：无法打印JsonReadHttpServletRequest");
            LOGGER.warn(e.getMessage());
        }
    }

    private void printResponse(JsonReadHttpServletRequest request,
                               JsonReadHttpServletResponse response) {
        try {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(String.format("响应请求:%s 方法:%s 参数:%s 到%s",
                        request.getRequestURI(), request.getMethod(),
                        request.getQueryString(), HttpUtil.getIpAddr(request)));
                LOGGER.info(StringUtil
                        .ByteToString(((JsonReadHttpServletResponse) response)
                                .getBytes()));
            }
        } catch (Exception e) {
            LOGGER.warn("警告：无法打印JsonReadHttpServletResponse");
            LOGGER.warn(e.getMessage());
        }
    }
}
