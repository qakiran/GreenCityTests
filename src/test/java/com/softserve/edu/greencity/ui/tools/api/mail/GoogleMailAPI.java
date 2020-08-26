package com.softserve.edu.greencity.ui.tools.api.mail;

import com.softserve.edu.greencity.ui.data.User;
import com.softserve.edu.greencity.ui.data.UserRepository;
import com.sun.mail.imap.protocol.FLAGS;
import com.sun.mail.imap.protocol.UIDSet;
import io.qameta.allure.Step;
import lombok.SneakyThrows;

import javax.mail.Message;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class use gmail api to be and take 1st unread msg to be continue
 */
public class GoogleMailAPI {
    private static BaseMailAPI emailUtils;
    @SneakyThrows(Exception.class)
    @Step
    public  GoogleMailAPI connectToEmail(String mail, String pass) {
        emailUtils = new BaseMailAPI(mail, pass, "smtp.gmail.com", BaseMailAPI.EmailFolder.INBOX);
        return this;
    }

    @SneakyThrows(Exception.class)
    @Step("get array of messages")
    public Message[] getMassagesBySubject(String subject, boolean unread, int maxToSearch){
        return emailUtils.getMessagesBySubject(subject, unread,  maxToSearch);
    }
    @SneakyThrows
    @Step("get Messages By Subject")
    public  void clearMail(String mail, String pass) {
        connectToEmail(mail,pass);
        Message[] msg = emailUtils.getAllMessages();
        for (Message message :msg) {
            message.setFlag(FLAGS.Flag.DELETED, true);
        }
    }

    //TODO: split logic to small methods,
    //TODO: split Matcher to another class as individual functional
    @Step("get green city auth confirm link from first mail")
    @SneakyThrows(Exception.class)
    public String getconfirmURL(String mail, String pass,int maxTries) {
        connectToEmail(mail,pass);
        String link = "";
        int count = 0;
        while (true) {
            Message[] email = emailUtils.getMessagesBySubject("Verify your email address", true, 5);
            String mailContent = emailUtils.getMessageContent(email[0]).trim().replaceAll("\\s+", "");
            Pattern pattern = Pattern.compile("https://greencity[^\"]+");
            final Matcher m = pattern.matcher(mailContent);
            m.find();
            link = mailContent.substring( m.start(), m.end() )
                    .replace("3D","")
                    .replace("amp;","")
                    .replace("=","")
                    .replace("token","token=")
                    .replace("user_id","user_id=");
            if (++count == maxTries) {
                return null;
            }
            return link;
        }
    }

    @Step("get green city auth confirm link from first mail")
    @SneakyThrows(Exception.class)
    public String getconfirmURL(String mail, String pass,String regex,int maxTries) {
        connectToEmail(mail,pass);
        String link = "";
        int count = 0;
        while (true) {
            Message[] email = emailUtils.getMessagesBySubject("Verify your email address", true, 5);
            String mailContent = emailUtils.getMessageContent(email[0]).trim().replaceAll("\\s+", "");
            Pattern pattern = Pattern.compile(regex);
            final Matcher m = pattern.matcher(mailContent);
            m.find();
            link = mailContent.substring( m.start(), m.end() )
                    .replace("3D","")
                    .replace("amp;","")
                    .replace("=","")
                    .replace("token","token=")
                    .replace("user_id","user_id=");
            if (++count == maxTries) {
                return null;
            }
            return link;
        }
    }

    @SneakyThrows
    public int getNumberMailsBySubject(String mail, String password, String subject, int maxTries) {
        connectToEmail(mail, password);
        int count = 0;
        while (true) {
            Message[] emails = emailUtils.getMessagesBySubject(subject, true, 5);
            int num = emails.length;
            if (++count == maxTries) {
                return 0;
            }
            return num;
        }
    }

}

