package com.camellia.mail;

public class Email {
    private String from;
    private String to;
    private String subject;
    private String text;

    public Email() {}

    public Email(String from, String to) {
        this.from = from;
        this.to = to;
    }

    public String getFrom() {
        return this.from;
    }

    public String getTo() {
        return this.to;
    }

    public String getSubject() {
        return this.subject;
    }

    public String getText() {
        return this.text;
    }


    public void setSubject(String sub) {
        this.subject = sub;
    }

    public void setText(String text) {
        this.text = text;
    }
    
}