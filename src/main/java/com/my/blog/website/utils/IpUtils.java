package com.my.blog.website.utils;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

/**
 * @author Jesse-liu
 * @description: ip操作工具类
 * @date 2020/5/13 11:29
 */
public class IpUtils {

    private static String longToIp(Long number) {
        String ip = "";
        for (int i = 3; i >= 0; i--) {
            long tmp = number >> (8 * i);
            ip += String.valueOf((tmp & 0xff));
            if (i != 0) {
                ip += ".";
            }
        }
        return ip;
    }

    private static Long ipToLong(String ip) {
        long ips = 0l;
        String[] ipArr = ip.split("\\.");
        for (int i = 0; i < 4; i++) {
            ips = ips << 8 | Integer.parseInt(ipArr[i]);
        }
        return ips;
    }

    private static String date;
    private static PropertiesUtil p = null;

    /***
     * 校验IP是否加入黑名单
     * @param ip
     * @return true 是在黑名单
     * @throws IOException
     */
    public static boolean chickIpBreak(String ip) throws IOException {
        if (p == null) {
            p = new PropertiesUtil("ip-black.properties");
        } else {
            String str = new SimpleDateFormat("MMddHHmmss").format(new Date());
            str = str.substring(0, 9);
            if (date == null || !date.equals(str)) {
                date = str;
                p = new PropertiesUtil("ip-black.properties");
            }
        }
        Enumeration en = p.getProps().propertyNames();
        while (en.hasMoreElements()) {
            String key = (String) en.nextElement();
            if (StringUtils.isNotBlank(key) && key.contains("~")) {
                String[] ipTemps = key.split("~");
                if (!isLocalIp(ip) && (Long.parseLong(ipTemps[0]) <= IpUtils.ipToLong(ip) &&
                        IpUtils.ipToLong(ip) <= Long.parseLong(ipTemps[1]))) {
                    return true;
                }

            } else if (!isLocalIp(ip) && key.equals(IpUtils.ipToLong(ip).toString())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址;
     *
     * @param request
     * @return
     * @throws IOException
     */
    public final static String getIpAddress(HttpServletRequest request)
            throws IOException {
        // 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址

        String ip = request.getHeader("X-Forwarded-For");
        // if (logger.isInfoEnabled()) {
        // logger.info("getIpAddress(HttpServletRequest) - X-Forwarded-For - String ip="
        // + ip);
        // }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            if (ip == null || ip.length() == 0
                    || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
                // if (logger.isInfoEnabled()) {
                // logger.info("getIpAddress(HttpServletRequest) - Proxy-Client-IP - String ip="
                // + ip);
                // }
            }
            if (ip == null || ip.length() == 0
                    || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
                // if (logger.isInfoEnabled()) {
                // logger.info("getIpAddress(HttpServletRequest) - WL-Proxy-Client-IP - String ip="
                // + ip);
                // }
            }
            if (ip == null || ip.length() == 0
                    || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
                // if (logger.isInfoEnabled()) {
                // logger.info("getIpAddress(HttpServletRequest) - HTTP_CLIENT_IP - String ip="
                // + ip);
                // }
            }
            if (ip == null || ip.length() == 0
                    || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
                // if (logger.isInfoEnabled()) {
                // logger.info("getIpAddress(HttpServletRequest) - HTTP_X_FORWARDED_FOR - String ip="
                // + ip);
                // }
            }
            if (ip == null || ip.length() == 0
                    || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
                // if (logger.isInfoEnabled()) {
                // logger.info("getIpAddress(HttpServletRequest) - getRemoteAddr - String ip="
                // + ip);
                // }
            }
        } else if (ip.length() > 15) {
            String[] ips = ip.split(",");
            for (int index = 0; index < ips.length; index++) {
                String strIp = (String) ips[index];
                if (!("unknown".equalsIgnoreCase(strIp))) {
                    ip = strIp;
                    break;
                }
            }
        }

        return ip;
    }

    /***
     * 服务器是否是本机ip
     * @param ip
     * @return
     */
    public static boolean isLocalIp(String ip) throws IOException {
        return ip.equals("127.0.0.1") || ip.equals("localhost") || ip.equals("0:0:0:0:0:0:0:1");
    }
}
