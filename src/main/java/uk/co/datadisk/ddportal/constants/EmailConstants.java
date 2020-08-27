package uk.co.datadisk.ddportal.constants;

public class EmailConstants {
    public static final String SIMPLE_MAIL_TRANSFER_PROTOCOL = "smtp";
    public static final String USERNAME = "paul.valle@example.com";
    public static final String PASSWORD = "password";
    public static final String FROM_EMAIL = "paul.valle@datadisk.co.uk";
    public static final String CC_EMAIL = "";
    public static final String EMAIL_SUBJECT = "Datadisk Portal - New Password";
    public static final String LOCAL_SMTP_SERVER = "localhost";
    public static final String SMTP_HOST = "mail.smtp.host";
    public static final String SMTP_AUTH = "mail.smtp.auth";
    public static final String SMTP_PORT = "mail.smtp.port";
    public static final int DEFAULT_PORT = 587;
    public static final String SMTP_STARTTLS_ENABLE = "mail.smtp.starttls.enable";
    public static final String SMTP_STARTTLS_REQUIRED = "mail.smtp.starttls.required";

//    ## Using local windows 10 hmailserver which will relay to my actual email address
//    spring.mail.host=localhost
//    spring.mail.port=587
//    spring.mail.username=paul.valle@example.com
//    spring.mail.password=password
//    spring.mail.properties.mail.smtp.auth=true
//    spring.mail.properties.mail.smtp.starttls.enable=true
//    support.email=paul.valle@example.com
}
