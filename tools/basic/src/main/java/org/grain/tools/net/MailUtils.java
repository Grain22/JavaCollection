package org.grain.tools.net;

import com.sun.mail.util.MailSSLSocketFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

/**
 * @author laowu
 */
public class MailUtils {

    static String account = "";
    static String password = "";
    static String sender = "";
    static String receiver = "laowu875054886@live.com";
    static String alias = "grain";
    static String subject = "subject";
    static String content = "content";
    static String MAIL_TRANSPORT_PROTOCOL = "smtp";
    static String MAIL_SMTP_HOST = "smtp.exmail.qq.com";
    static String MAIL_SMTP_PORT = "465";
    static String MAIL_SMTP_AUTH = "true";

    public static void sendMailDemo() throws Exception {
        Session session = Session.getDefaultInstance(setTencentExEmail(), new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(account, password);
            }
        });
        MimeMessage mimeMessage = new MimeMessage(session);
        mimeMessage.setFrom(new InternetAddress(sender, alias));
        mimeMessage.addRecipient(Message
                        .RecipientType
                        .TO,
                new InternetAddress(
                        receiver
                ));
        mimeMessage.setSubject(subject);
        mimeMessage.setSentDate(new Date());
        mimeMessage.setText(content);
        mimeMessage.saveChanges();
        Transport.send(mimeMessage);
    }

    public static Properties setTencentExEmail() {
        Properties prop = new Properties();
        try {
            //协议
            prop.setProperty("mail.transport.protocol", MAIL_TRANSPORT_PROTOCOL);
            //服务器
            prop.setProperty("mail.smtp.host", MAIL_SMTP_HOST);
            //端口
            prop.setProperty("mail.smtp.port", MAIL_SMTP_PORT);
            //使用smtp身份验证
            prop.setProperty("mail.smtp.auth", MAIL_SMTP_AUTH);
            MailSSLSocketFactory sf = new MailSSLSocketFactory();
            sf.setTrustAllHosts(true);
            prop.put("mail.smtp.ssl.enable", "true");
            prop.put("mail.smtp.ssl.socketFactory", sf);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return prop;
    }
}
