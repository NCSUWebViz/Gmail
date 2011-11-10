package inbox;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.Enumeration;
import java.util.Properties;
import javax.mail.Folder;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

public class InboxReader {
	private static final String jdbcURL = "jdbc:oracle:thin:@ora.csc.ncsu.edu:1523:orcl";
	private static final String user = "gapandit";
	private static final String password = "001000715";

	public static Connection connectToDatabase() throws Exception {
		Class.forName("oracle.jdbc.driver.OracleDriver");
		Connection connection = DriverManager.getConnection(jdbcURL, user,
				password);
		return connection;
	}

	public static void main(String args[]) {
		Properties props = System.getProperties();
		props.setProperty("mail.store.protocol", "imaps");

		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					System.in));
			System.out.println("Enter the username");
			String userName = in.readLine();

			System.out.println("Enter the password");
			String password = in.readLine();

			Session session = Session.getDefaultInstance(props, null);

			Store store = session.getStore("imaps");
			store.connect("imap.gmail.com", userName, password);
			Folder[] allFolders = store.getDefaultFolder().list();

			for (Folder f : allFolders) {
				if (!f.getName().equals("[Gmail]")) {
					if (f.getName().equals("INBOX")) {
						System.out.println("Parent INBOX");
						getParentMails(f);
					}
				} else {
					Folder[] gmailSubFolders = f.list();
					for (Folder gmailSubFolder : gmailSubFolders) {
						if (gmailSubFolder.getName().equals("Sent Mail")) {
							System.out.println("Parent Sent mail");
							getParentMails(gmailSubFolder);
						}
					}
				}
			}
			for (Folder f : allFolders) {
				if (!f.getName().equals("[Gmail]")) {
					if (f.getName().equals("INBOX")) {
						System.out.println("Child INBOX");
						getChildMails(f);
					}
				} else {
					Folder[] gmailSubFolders = f.list();
					for (Folder gmailSubFolder : gmailSubFolders) {
						if (gmailSubFolder.getName().equals("Sent Mail")) {
							System.out.println("Child Sent Mail");
							getChildMails(gmailSubFolder);
						}
					}
				}
			}
			store.close();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (MessagingException e) {
			e.printStackTrace();
			System.exit(2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void getParentMails(Folder folder) throws Exception {
		folder.open(Folder.READ_ONLY);
		Message messages[] = folder.getMessages();
		dumpParentMails(messages, folder.getName());
		folder.close(true);
	}

	private static void getChildMails(Folder folder) throws Exception {
		folder.open(Folder.READ_ONLY);
		Message messages[] = folder.getMessages();
		dumpChildMails(messages, folder.getName());
		folder.close(true);
	}

	@SuppressWarnings("unchecked")
	public static void dumpParentMails(Message[] messages, String folderName)
			throws Exception {
		Connection connection = connectToDatabase();
		Integer messageCount = 0;

		String senderName = null;
		String receiverName = null;

		if (messages.length > 0) {
			for (Message message : messages) {
				Enumeration<Header> headerEnumeration = message.getAllHeaders();
				while (headerEnumeration.hasMoreElements()) {
					Header header = headerEnumeration.nextElement();
					if (header.getName().equals("From")) {
						senderName = header.getValue();
					}
					if (header.getName().equals("To")) {
						receiverName = header.getValue();
					}
				}
				if (senderName != null) {
					if (message.getSubject() == null) {
						System.out.println("Subject NULL "
								+ message.getMessageNumber() + "  "
								+ message.getSubject());
						if (senderName.indexOf("<") != -1) {
							insert(message, senderName.substring(
									senderName.indexOf("<") + 1,
									senderName.indexOf(">")), receiverName,
									message.getMessageNumber(), connection,
									folderName);
						} else {
							insert(message, senderName, receiverName,
									messageCount, connection, folderName);
						}
					} else if (message.getSubject() != null
							&& (!message.getSubject().startsWith("Re: "))) {
						System.out.println("Subject NOT NULL "
								+ message.getMessageNumber() + "  "
								+ message.getSubject());
						if (senderName.indexOf("<") != -1) {
							insert(message, senderName.substring(
									senderName.indexOf("<") + 1,
									senderName.indexOf(">")), receiverName,
									message.getMessageNumber(), connection,
									folderName);
						} else {
							insert(message, senderName, receiverName,
									messageCount, connection, folderName);
						}
					}
				}
				messageCount++;
			}
		}
		connection.close();
	}

	@SuppressWarnings("unchecked")
	public static void dumpChildMails(Message[] messages, String folderName)
			throws Exception {
		Connection connection = connectToDatabase();
		Integer messageCount = 0;

		String senderName = null;
		String receiverName = null;

		if (messages.length > 0) {
			for (Message message : messages) {
				Enumeration<Header> headerEnumeration = message.getAllHeaders();
				while (headerEnumeration.hasMoreElements()) {
					Header header = headerEnumeration.nextElement();

					if (header.getName().equals("From")) {
						senderName = header.getValue();
					}
					if (header.getName().equals("To")) {
						receiverName = header.getValue();
					}
				}
				if (senderName != null) {
					if ((message.getSubject() != null)
							&& (message.getSubject().startsWith("Re:"))) {
						System.out.println("Reply : "
								+ message.getMessageNumber() + "  "
								+ message.getSubject());
						if (senderName.indexOf("<") != -1) {
							insertReply(message, senderName.substring(
									senderName.indexOf("<") + 1,
									senderName.indexOf(">")), receiverName,
									message.getMessageNumber(), connection,
									folderName);
						} else {
							insertReply(message, senderName, receiverName,
									messageCount, connection, folderName);
						}
					}
				}
				messageCount++;
			}
		}
		connection.close();
	}

	private static String findParentMessageID(String subject,
			Connection connection) {
		try {
			System.out.println("ParentMessage Subject: " + subject);
			Statement statement = connection.createStatement();
			ResultSet parentMessage = statement
					.executeQuery("SELECT DISTINCT MESSAGEID FROM MAIL WHERE SUBJECT LIKE '"
							+ subject + "%'");

			if (parentMessage.next()) {
				return parentMessage.getString("MESSAGEID");
			}
			parentMessage.close();
		} catch (Exception e) {

		}
		return "";
	}

	private static void insertReply(Message message, String senderName,
			String receiverName, Integer messageCount, Connection connection,
			String folderName) {
		try {
			Statement statement = connection.createStatement();
			System.out.println(message.getSubject().replaceAll("^Re: ", ""));

			String parentMessageID = findParentMessageID(message.getSubject()
					.replaceAll("^Re: ", ""), connection);
			System.out.println("ParentMessage : " + parentMessageID);

			if (parentMessageID.equals("")) {
				System.out.println("Cant find parent");
			} else {
				System.out
						.println("INSERT INTO REPLYMAIL (PARENTMESSAGEID, MESSAGEID, SUBJECT, SENDERNAME, RECEIVERNAME, CONTENTTYPE, FLAGS, RECEIVEDATE, SENDDATE, MAILSIZE) VALUES('"
								+ parentMessageID
								+ "','"
								+ messageCount
								+ "','"
								+ message.getSubject()
								+ "','"
								+ senderName
								+ "','"
								+ receiverName
								+ "','"
								+ message.getContentType()
								+ "','"
								+ message.getFlags()
								+ "',to_date('"
								+ new java.sql.Date(message.getReceivedDate()
										.getTime())
								+ "','YYYY-MM-DD HH:MI:SS PM'),to_date('"
								+ new java.sql.Date(message.getSentDate()
										.getTime())
								+ "','YYYY-MM-DD HH:MI:SS PM'),'"
								+ message.getSize() + "')");
				statement
						.executeUpdate("INSERT INTO REPLYMAIL (PARENTMESSAGEID, MESSAGEID, SUBJECT, SENDERNAME, RECEIVERNAME, CONTENTTYPE, FLAGS, RECEIVEDATE, SENDDATE, MAILSIZE) VALUES('"
								+ parentMessageID
								+ "','"
								+ messageCount
								+ "','"
								+ message.getSubject()
								+ "','"
								+ senderName
								+ "','"
								+ receiverName
								+ "','"
								+ message.getContentType()
								+ "','"
								+ message.getFlags()
								+ "',to_date('"
								+ new java.sql.Date(message.getReceivedDate()
										.getTime())
								+ "','YYYY-MM-DD HH:MI:SS PM'),to_date('"
								+ new java.sql.Date(message.getSentDate()
										.getTime())
								+ "','YYYY-MM-DD HH:MI:SS PM'),'"
								+ message.getSize() + "')");
				statement.executeUpdate("commit");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void insert(Message message, String senderName,
			String receiverName, int messageCount, Connection connection,
			String folderName) {

		try {
			Statement statement = connection.createStatement();

			statement
					.executeUpdate("INSERT INTO MAIL (MESSAGEID, SUBJECT, SENDERNAME, RECEIVERNAME, CONTENTTYPE, FLAGS, RECEIVEDATE, SENDDATE, MAILSIZE) VALUES('"
							+ folderName
							+ messageCount
							+ "','"
							+ message.getSubject()
							+ "','"
							+ senderName
							+ "','"
							+ receiverName
							+ "','"
							+ message.getContentType()
							+ "','"
							+ message.getFlags()
							+ "',to_date('"
							+ new java.sql.Date(message.getReceivedDate()
									.getTime())
							+ "','YYYY-MM-DD HH:MI:SS PM'),to_date('"
							+ new java.sql.Date(message.getSentDate().getTime())
							+ "','YYYY-MM-DD HH:MI:SS PM'),'"
							+ message.getSize() + "')");

			statement.executeUpdate("commit");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}