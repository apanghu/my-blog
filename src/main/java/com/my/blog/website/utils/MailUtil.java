package com.my.blog.website.utils;

import com.my.blog.website.modal.Vo.MailBoxVo;
import com.my.blog.website.modal.Vo.MailVo;
import org.apache.commons.mail.HtmlEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MailUtil {
    private static final Logger logger = LoggerFactory.getLogger(MailUtil.class);


    public MailUtil() {
    }

    public static boolean sendMail(MailVo mailVo) {
        MailBoxVo mailBoxVo = (MailBoxVo) MapCache.single().get("mailBoxVo");
        String fromAddress = mailBoxVo.getEmailAddress();
        String fromPassword = mailBoxVo.getEmailPassword();
        String fromHostName = mailBoxVo.getSMTPServer();
        String sslOnConnect = "false";
        String sslSmtpPort = mailBoxVo.getSMTPPort().toString();
        return send(fromAddress, fromPassword, fromHostName, sslOnConnect, sslSmtpPort, mailVo.getReceiver(), mailVo.getSubject(), mailVo.getContent());
    }

    public static boolean send(String fromAddress, String fromPassword, String fromHostName, String sslOnConnect, String sslSmtpPort, String toAddress, String subject, String content) {
        try {
            HtmlEmail htmlEmail = new HtmlEmail();
            htmlEmail.setFrom(fromAddress);
            htmlEmail.setAuthentication(fromAddress, fromPassword);
            htmlEmail.setHostName(fromHostName);
            if ("true".equals(sslOnConnect)) {
                htmlEmail.setSSLOnConnect(true);
                htmlEmail.setSslSmtpPort(sslSmtpPort);
            }

            htmlEmail.addTo(toAddress);
            htmlEmail.setSubject(subject);
            htmlEmail.setMsg(content);
            htmlEmail.setCharset("utf-8");
            htmlEmail.send();
            return true;
        } catch (Exception var9) {
            logger.error(var9.getMessage(), var9);
            return false;
        }
    }
}