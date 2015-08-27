package main.java;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;

public class SmtpSend {

	final private static String USERNAME = "mail.username";
	final private static String PASSWORD = "mail.password";

	final private static String HOST = "mail.smtp.host";
	final private static String BODY = "mail.body";

	public static void main(String[] args) {
		System.out.println("#####################");
		System.out.println("Start running");
		System.out.println("#####################");

		if (args.length != 3) {
			System.err.println("Please enter correct parameters!");
			System.out.println("Usage: java -jar testSmtp.jar [properties file name] [from address] [to address] ");
			System.out.println("");
			return;
		}

		String fileName = args[0];
		String from = args[1];
		String to = args[2];
		Properties props = loadProperties(fileName);
		sendEmail(props, from, to);

		System.out.println("#####################");
		System.out.println("Completed!");
		System.out.println("#####################");
	}

	private static Properties loadProperties(String fileName) {
		Properties props = new Properties();

		try {
			InputStream inStream = new FileInputStream(fileName);

			props.load(inStream);
		} catch (FileNotFoundException e) {
			System.err.println("Cannot load properties file, due to " + e.getMessage());
		} catch (IOException e) {
			System.err.println("Cannot load properties , due to " + e.getMessage());
		}

		System.out.println();
		System.out.println("-----------------------");
		String body = "";
		for (Object key : props.keySet()) {
			String keyStr = String.valueOf(key);
			if (StringUtils.isBlank(keyStr))
				continue;

			if (keyStr.startsWith("mail.")) {
				String property = keyStr + "=" + props.getProperty(keyStr);
				System.out.println(property);
				body += property + "\n";
			}

		}
		System.out.println("-----------------------");
		System.out.println();

		props.put(BODY, body);

		return props;
	}

	private static void sendEmail(Properties props, String from, String to) {
		final String username = props.getProperty(USERNAME);
		final String password = props.getProperty(PASSWORD);

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});
		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
			message.setSubject("Test " + props.getProperty(HOST));
			message.setText(props.getProperty(BODY));
			Transport.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

}
