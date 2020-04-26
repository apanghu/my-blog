package com.my.blog.website.modal.Vo;

import java.io.Serializable;

/**
 * @author Jesse-liu
 * @description: TODO
 * @date 2020/4/21 14:42
 */
public class MailBoxVo implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private String SMTPServer;//SMTP服务器
    private Integer SMTPPort;//SMTP端口
    private String emailAddress;//邮箱账号
    private String emailPassword;//邮箱密码
    private String receiver;//接收人

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSMTPServer() {
        return SMTPServer;
    }

    public void setSMTPServer(String SMTPServer) {
        this.SMTPServer = SMTPServer;
    }

    public Integer getSMTPPort() {
        return SMTPPort;
    }

    public void setSMTPPort(Integer SMTPPort) {
        this.SMTPPort = SMTPPort;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getEmailPassword() {
        return emailPassword;
    }

    public void setEmailPassword(String emailPassword) {
        this.emailPassword = emailPassword;
    }
}
