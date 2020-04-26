package com.my.blog.website.modal.Vo;

/**
 * @author 邮件发送队列实体类
 */
public class MailVo {

    /**
     * 注释内容
     */
    private static final long serialVersionUID = 1L;

    private String receiver;

    private String subject;

    private String content;

    public MailVo(String receiver, String subject, String content) {
        this.receiver = receiver;
        this.subject = subject;
        this.content = content;
    }

    public MailVo() {
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }
}