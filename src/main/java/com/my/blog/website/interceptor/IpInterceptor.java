package com.my.blog.website.interceptor;

import com.my.blog.website.service.ILogService;
import com.my.blog.website.utils.IpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Jesse-liu
 * @description: Ip黑名单拦截器
 * @date 2020/5/13 11:12
 */
@Component
public class IpInterceptor implements HandlerInterceptor {

    @Resource
    private ILogService logService;

    private static final Logger LOGGE = LoggerFactory.getLogger(IpInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        LOGGE.info("进入IP黑名单拦截器：{}", IpUtils.getIpAddress(request));
        String ip = IpUtils.getIpAddress(request);
        if (IpUtils.chickIpBreak(ip)) {
            logService.insertLog("进入IP黑名单拦截器", IpUtils.getIpAddress(request), request.getRemoteAddr(), null);
            response.setHeader("Content-Type", "text/html; charset=UTF-8");
            response.getWriter().write("<p>欢迎使用.</p>");
            response.getWriter().flush();
            response.getWriter().close();
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }

}
