package com.example.music8027;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendEmailTask extends AsyncTask<Void, Void, Void> {
    private static final String TAG = "SendEmailTask";

    private final String name;
    private final String email;
    private final String subject;
    private final String otp;
    private final String msg;
    private final int length;

    public SendEmailTask(String name, String email, String subject,String otp, String msg, int length) {
        this.name = name;
        this.email = email;
        this.subject = subject;
        this.otp = otp;
        this.length = length;
        this.msg = msg;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        sendEmail();
        return null;
    }

    private void sendEmail() {
        final String username = "MS_U1TuEK@trial-x2p0347omkpgzdrn.mlsender.net";
        final String password = "WZKz0cXoIRGWPAbF";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.mailersend.net");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject(subject);
            message.setText(subject + "\n" +msg + otp);

            Transport.send(message);
            Log.d(TAG, "Email sent successfully");
        } catch (MessagingException e) {
            Log.e(TAG, "Failed to send email", e);
        }
    }
}