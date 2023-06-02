package com.chromatic.common.interceptor;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //过滤路径中带 ;jsessionid=003EC21DCDFA5169409DB58EE39C0896 的问题
        String requestURI = request.getRequestURI();
        if (requestURI.indexOf(";jsessionid=") != -1) {
            //替换掉路径上的内容, ;jsessionid=003EC21DCDFA5169409DB58EE39C0896
            requestURI = requestURI.replace(";jsessionid=" + request.getSession().getId(),"");
            //重定向一下
            request.getRequestDispatcher(requestURI).forward(request, response);
            return false;
        }

        return super.preHandle(request, response, handler);
    }
}
