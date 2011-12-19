package website.web;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.mail.Folder;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/* 
 * Sequence of function calls
 * 1. accessGmail is called
 * 2. deleteEverything is only for testing. it deletes all the table contents.
 * 3. loadContacts load existing contacts in the Contacts table into contactsMap
 * 4. getContacts reads the mail and loads the Contacts in the Contacts table database.
 * 5. getParentMails reads the email folders and gets the parent threads
 * 6. getChildMails reads the email folders for replies
 * 7. getAvgResponseTimeForMails updates the average response time column for each mail in the Mail table
 * 8. normalizeMailAverage normalizes the average response time for each mail on importanceHash 
 * 9. updateContactImportance updates the contacts importance using the individual importance
 * 10. getTimestampInterval divides the inbox age into 5 intervals
 */
public class GmailServlet extends HttpServlet {
	Properties databaseConfig = new Properties();
	
	private static String dbJDBC, dbUserName, dbPassword;

	private static int parentMessageCounter = 0;
	private static int childMessageCounter = 0;

	private static HashMap<String, String> contactsMap = new HashMap<String, String>();

	//main batch function: this function triggers all other small batch task 
	public void accessGmail(String userName, String password) throws Exception {
		System.out.println("Access Gmail");
		Properties props = System.getProperties();
		props.setProperty("mail.store.protocol", "imaps");

		Session session = Session.getDefaultInstance(props, null);
		Store store = session.getStore("imaps");
		store.connect("imap.gmail.com", userName, password);
		Folder[] allFolders = store.getDefaultFolder().list();

		deleteEverything(); //initialize database; only for testing purposes

		loadContacts(); //load contacts into contact table

		for (Folder f : allFolders) {
			if (!f.getName().equals("[Gmail]")) {
				if (f.getName().equals("INBOX")) { //access only the INBOX folder for parent threads
					System.out.println("Parent INBOX");
					getContacts(f);
					getParentMails(f);
				}
			} 
			else {
				Folder[] gmailSubFolders = f.list();
				for (Folder gmailSubFolder : gmailSubFolders) {
					if(gmailSubFolder.getName().equals("Sent Mail")) { //access only the Sent Mail for parent threads
						System.out.println("Parent Sent mail");
						getContacts(gmailSubFolder);
						getParentMails(gmailSubFolder);
					}
				}
			}
		}

		for (Folder f : allFolders) {
			if (!f.getName().equals("[Gmail]")) {
				if (f.getName().equals("INBOX")) { //access only the INBOX for child threads
					System.out.println("Child INBOX");
					getContacts(f);
					getChildMails(f);
				}
			} 
			else {
				Folder[] gmailSubFolders = f.list();
				for (Folder gmailSubFolder : gmailSubFolders) {
					if(gmailSubFolder.getName().equals("Sent Mail")) { //access only Sent Mail for child threads
						System.out.println("Child Sent Mail");
						getContacts(gmailSubFolder);
						getChildMails(gmailSubFolder);
					}
				}
			}
		}
//		 enterBatchTime();
		getAvgResponseTimeForMails(userName);
		normalizeMailAverage();
		updateContactImportance();
		getTimestampInterval();

		store.close();
	}

	//    public static void enterBatchTime() throws Exception {
	//        Connection connection = connectToDatabase();
	//        Statement statement = connection.createStatement();
	//        Timestamp timestamp = new java.sql.Timestamp(System.currentTimeMillis());
	//        System.out.println("INSERT INTO BATCHTIMES VALUES '" + timestamp + "'");
	//        statement.executeUpdate("INSERT INTO BATCHTIMES VALUES '" + timestamp + "'");
	//        statement.execute("COMMIT");
	//    }
	//    

	public static Connection connectToDatabase() throws Exception {
		Class.forName("oracle.jdbc.driver.OracleDriver");
		Connection connection = DriverManager.getConnection(dbJDBC, dbUserName, dbPassword);
		return connection;
	}

	public static void getkeywordImportance() throws Exception {
		Connection connection = connectToDatabase();
		Statement mstmt = connection.createStatement();
		String s1,s2;
		ResultSet mailset = mstmt.executeQuery("SELECT * from MAIL");
		while(mailset.next())
		{
			Statement kstmt = connection.createStatement();
			ResultSet keywordset = kstmt.executeQuery("SELECT * FROM KEYWORD");

			while(keywordset.next())
			{
				s1=mailset.getString("SUBJECT");
				s2=keywordset.getString("SKEYWORD");
				if(s1.contains(s2))
				{
					kstmt.executeUpdate("UPDATE KEYWORD SET IMPORTANCE = IMPORTANCE + 1.0 WHERE SKEYWORD = '" + s2 + "'");    
					kstmt.executeUpdate("COMMIT");
				}
			}
			keywordset.close();
			kstmt.close();
		}
		mailset.close();
		mstmt.close();
		connection.close();
	}

	public static void getTimestampInterval() throws Exception {
		Connection connection = connectToDatabase();
		Statement statement = connection.createStatement();

		java.sql.Timestamp minTimeStamp = null;
		java.sql.Timestamp maxTimeStamp = null;

		ResultSet minTimeStampResultSet = statement.executeQuery("SELECT MIN(RECEIVEDATE) AS MINRECEIVE FROM MAIL");
		if(minTimeStampResultSet.next()) {
			minTimeStamp = minTimeStampResultSet.getTimestamp("MINRECEIVE");
		}
		minTimeStampResultSet.close();

		ResultSet maxTimeStampResultSet = statement.executeQuery("SELECT MAX(RECEIVEDATE) AS MAXRECEIVE FROM MAIL");
		if(maxTimeStampResultSet.next()) {
			maxTimeStamp = maxTimeStampResultSet.getTimestamp("MAXRECEIVE");
		}
		maxTimeStampResultSet.close();
		maxTimeStamp.setTime(getMidnightTime(maxTimeStamp).getTime());

		java.sql.Timestamp toTimeStamp, fromTimeStamp = null;

		toTimeStamp = maxTimeStamp;
		fromTimeStamp = new Timestamp(maxTimeStamp.getTime() - (24*60*60*1000));            
		insertTimestampsIntoDB(statement, fromTimeStamp, toTimeStamp);

		toTimeStamp.setTime(fromTimeStamp.getTime() - 1000);
		fromTimeStamp.setTime(toTimeStamp.getTime() + 1000 - (24*60*60*1000));
		insertTimestampsIntoDB(statement, fromTimeStamp, toTimeStamp);

		toTimeStamp.setTime(fromTimeStamp.getTime() - 1000);
		fromTimeStamp.setTime(getWeekStartTime(toTimeStamp).getTime());
		insertTimestampsIntoDB(statement, fromTimeStamp, toTimeStamp);

		toTimeStamp.setTime(fromTimeStamp.getTime() - 1000);
		fromTimeStamp.setTime(getMonthStartTime(toTimeStamp).getTime());
		insertTimestampsIntoDB(statement, fromTimeStamp, toTimeStamp);

		toTimeStamp.setTime(fromTimeStamp.getTime() - 1000);
		fromTimeStamp.setTime(minTimeStamp.getTime());
		insertTimestampsIntoDB(statement, fromTimeStamp, toTimeStamp);
		statement.close();
	}

	private static Timestamp getMonthStartTime(Timestamp timeStamp) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timeStamp.getTime());
		cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		return new Timestamp(cal.getTimeInMillis());
	}

	public static Timestamp getWeekStartTime(Timestamp timestamp) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timestamp.getTime());
		cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		return new Timestamp(cal.getTimeInMillis());
	}

	public static Timestamp getMidnightTime(Timestamp timestamp) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timestamp.getTime());

		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return new Timestamp(cal.getTimeInMillis());
	}
	public static void insertTimestampsIntoDB(Statement statement, Timestamp from, Timestamp to) throws Exception {
		statement.executeUpdate("alter session set NLS_TIMESTAMP_FORMAT = 'YYYY-MM-DD HH24:MI:SS.FF'");
		statement.executeUpdate("COMMIT");
		statement.executeUpdate("INSERT INTO INBOXAGE (FROMINTERVAL, TOINTERVAL) VALUES('" + from + "','" + to + "')");        
	}

	//accept get request
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		HttpSession httpsession = request.getSession(true);
		dbUserName = httpsession.getValue("dbUsername").toString();
		dbPassword = httpsession.getValue("dbPassword").toString();
		dbJDBC = httpsession.getValue("dbJDBC").toString();
		String userName = httpsession.getValue("email_id").toString();
		String password = httpsession.getValue("password").toString();
		//HttpSession session = request.getSession(true);
		PrintWriter pw = null;
		try {
			pw = response.getWriter();
			accessGmail(userName, password);
			pw.write("{\"success\": \"true\"}");
		}
		catch (Exception e) {
			e.printStackTrace();
			pw.write("{\"failure\": \"true\"}");
		}
	}

	//accept post request
	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
		doGet(request, response);
	}

	//delete the database records
	private void deleteEverything() throws Exception {
		System.out.println("Deleteeverything");
		Connection connection = connectToDatabase();
		Statement statement = connection.createStatement();

		statement.executeUpdate("DELETE FROM REPLYMAIL");
		statement.executeUpdate("DELETE FROM MAIL");
		statement.executeUpdate("DELETE FROM CONTACT");
		statement.executeUpdate("DELETE FROM INBOXAGE");

		statement.executeUpdate("COMMIT");
		connection.close();
	}

	//load contacts into local hashMap
	private static void loadContacts() throws Exception {
		Connection connection = connectToDatabase();
		Statement statement = connection.createStatement();

		ResultSet contactResultSet = statement.executeQuery("SELECT contactemail FROM contact");

		while(contactResultSet.next()) {
			String email = contactResultSet.getString("contactemail");
			contactsMap.put(email, email);
		}
		statement.close();
		connection.close();
	}

	//get parent thread mails
	public static void getParentMails(Folder folder) throws Exception {
		folder.open(Folder.READ_ONLY);
		Message messages[] = folder.getMessages();

		dumpParentMails(messages, folder.getName());
		folder.close(true);
	}

	@SuppressWarnings("unchecked")
	private static void getContacts(Folder folder) throws Exception {        
		String senderNames = null, receiverNames = null;

		folder.open(Folder.READ_ONLY);
		Connection connection = connectToDatabase();
		for(Message message: folder.getMessages()) {
			//access message header to get From and To headers.
			Enumeration<Header> headerEnumeration = message.getAllHeaders();
			while (headerEnumeration.hasMoreElements()) {
				Header header = headerEnumeration.nextElement();
				if (header.getName().equals("From")) {
					senderNames = header.getValue();
				}
				if (header.getName().equals("To")) {
					receiverNames = header.getValue();
				}
			}
			String[] senderName = senderNames.split(",");
			String[] receiverName = receiverNames.split(",");

			java.util.Arrays.sort(receiverName);

			//put sender name in local hashMap
			for(String sender : senderName) {
				String senderEmail = sender.trim();
				if(sender.indexOf("<") != -1) {
					senderEmail = sender.substring(sender.indexOf("<") + 1,    sender.indexOf(">")).trim();
				}

				if(!contactsMap.containsKey(senderEmail)) {
					putContactsIntoDB(senderEmail, connection);
					contactsMap.put(senderEmail, senderEmail);        
				}    
			}

			//put receiver name in local hashMap
			receiverNames = "";
			for(String receiver : receiverName) {
				if(receiver!=null){
					String receiverEmail = receiver.trim();
					if(receiver.indexOf("<") != -1) {
						receiverEmail = receiver.substring(receiver.indexOf("<") + 1, receiver.indexOf(">")).trim();
					}
					receiverNames = receiverNames + receiverEmail + ",";

					if(!contactsMap.containsKey(receiverEmail)) {
						putContactsIntoDB(receiverEmail, connection);
						contactsMap.put(receiverEmail, receiverEmail);
					}
				}
			}
			receiverNames = receiverNames.substring(0, receiverNames.length() -1);

			if((!contactsMap.containsKey(receiverNames))&&(receiverNames!=null)) {
				putContactsIntoDB(receiverNames, connection);
				contactsMap.put(receiverNames, receiverNames);
			}
		}
		connection.close();
		folder.close(true);
	}

	//put Contact in the contact table
	private static void putContactsIntoDB(String contact, Connection connection) throws Exception {
		Statement statement = connection.createStatement();
		if(!(contact.length()==0)){
			statement.executeUpdate("INSERT INTO CONTACT (contactemail, contactimp) VALUES ('" + contact + "', 0)");
		}
		statement.close();
	}

	//get all replies from the parent threads
	private static void getChildMails(Folder folder) throws Exception {
		folder.open(Folder.READ_ONLY);
		Message messages[] = folder.getMessages();
		dumpChildMails(messages, folder.getName());        
		folder.close(true);
	}

	@SuppressWarnings("unchecked")
	public static void dumpParentMails(Message[] messages, String folderName) throws Exception {
		Connection connection = connectToDatabase();
		String senderName = null;
		String receiverName = null;

		for (Message message : messages) {
			Enumeration<Header> headerEnumeration = message.getAllHeaders();

			while (headerEnumeration.hasMoreElements()) {
				Header header = headerEnumeration.nextElement();
				if (header.getName().equals("From")) {
					senderName = header.getValue();
					if(senderName.indexOf("<") != -1) {
						senderName = senderName.substring(senderName.indexOf("<") + 1, senderName.indexOf(">")).trim();                            
					}
				}
				if (header.getName().equals("To")) {                    
					receiverName = header.getValue().trim();
					String [] receivers = receiverName.split(",");
					java.util.Arrays.sort(receivers);

					receiverName = "";
					for(int i=0; i<receivers.length; i++) {
						if(receivers[i].indexOf("<") != -1) {
							receiverName = receiverName + receivers[i].substring(receivers[i].indexOf("<") + 1, receivers[i].indexOf(">")).trim() + ",";
						}
						else {
							receiverName = receiverName + receivers[i].trim() + ",";
						}
					}
					if(receiverName != "") {
						receiverName = receiverName.substring(0, receiverName.length() -1).trim();
					}
				}
			}
			if (senderName.length()!=0 && receiverName.length()!=0) {
				if (message.getSubject() == null) {
					insert(message, senderName, receiverName, parentMessageCounter, connection, folderName);
					parentMessageCounter++;
				} 
				else if ((message.getSubject() != null) && (!message.getSubject().startsWith("Re: "))) {
					insert(message, senderName,    receiverName, parentMessageCounter, connection, folderName);
					parentMessageCounter++;
				}
			}
		}
		connection.close();
	}

	@SuppressWarnings("unchecked")
	public static void dumpChildMails(Message[] messages, String folderName) throws Exception {
		Connection connection = connectToDatabase();

		String senderName = null;
		String receiverName = null;

		if (messages.length > 0) {
			for (Message message : messages) {
				Enumeration<Header> headerEnumeration = message.getAllHeaders();
				while (headerEnumeration.hasMoreElements()) {
					Header header = headerEnumeration.nextElement();

					if (header.getName().equals("From")) {
						senderName = header.getValue();
						if(senderName.indexOf("<") != -1) {
							senderName = senderName.substring(senderName.indexOf("<") + 1, senderName.indexOf(">")).trim();                            
						}
					}
					if (header.getName().equals("To")) {
						receiverName = header.getValue();
						String [] receivers = receiverName.split(",");
						java.util.Arrays.sort(receivers);

						receiverName = "";
						for(int i=0; i<receivers.length; i++) {
							if(receivers[i].indexOf("<") != -1) {
								receiverName = receiverName + receivers[i].substring(receivers[i].indexOf("<") + 1, receivers[i].indexOf(">")).trim() + ",";
							}
							else {
								receiverName = receiverName + receivers[i].trim() + ",";
							}                                
						}
						if(receiverName != "") {
							receiverName = receiverName.substring(0, receiverName.length() -1).trim();
						}
					}
				}
				if (senderName.length()!=0 && receiverName.length()!=0){
					if ((message.getSubject() != null) && (message.getSubject().startsWith("Re:"))) {
						if (senderName.indexOf("<") != -1) {
							insertReply(message, senderName, receiverName, childMessageCounter, connection, folderName);
							childMessageCounter++;
						}
						else {
							insertReply(message, senderName, receiverName, childMessageCounter, connection, folderName);
							childMessageCounter++;
						}
					}
				}
			}
		}
		connection.close();
	}

	private static String findParentMessageID(String subject, Connection connection) {
		try {
			Statement statement = connection.createStatement();
			ResultSet parentMessage = statement.executeQuery("SELECT DISTINCT MESSAGEID FROM MAIL WHERE SUBJECT LIKE '" + subject + "%'");

			if(parentMessage.next()) {
				return parentMessage.getString("MESSAGEID");
			}
			parentMessage.close();
			statement.close();
		}
		catch (Exception e) {

		}
		return "";
	}

	private static void insertReply(Message message, String senderName,    String receiverName, Integer messageCount, Connection connection, String folderName) throws Exception {
		Statement statement = connection.createStatement();
		String  parentMessageID = findParentMessageID(message.getSubject().replaceAll("^Re: ", ""), connection);
		String subjectmsg;
		if(message.getSubject()!=null){
			subjectmsg=message.getSubject().replace('\'',':');
		}
		else{
			subjectmsg=null;	
		}

		if (parentMessageID.equals("")) {

		} 
		else {
			statement.executeUpdate("alter session set NLS_TIMESTAMP_FORMAT = 'YYYY-MM-DD HH24:MI:SS.FF'");
			statement.executeUpdate("commit");

			statement.executeUpdate("INSERT INTO REPLYMAIL (PARENTMESSAGEID, MESSAGEID, SUBJECT, SENDERNAME, RECEIVERNAME, CONTENTTYPE, FLAGS, RECEIVEDATE, SENDDATE, MAILSIZE) VALUES('"
					+ parentMessageID
					+ "','"
					+ folderName + messageCount
					+ "','"
					+ subjectmsg
					+ "','"
					+ senderName
					+ "','"
					+ receiverName
					+ "','"
					+ "gmailviz"
					+ "','"
					+ message.getFlags()
					+ "','"
					+ new java.sql.Timestamp(message.getReceivedDate().getTime())
					+ "','"
					+ new java.sql.Timestamp(message.getSentDate().getTime())
					+ "','"
					+ message.getSize() + "')");
			statement.executeUpdate("commit");
		}
		statement.close();
	}

	public static void insert(Message message, String senderName, String receiverName, int messageCount, Connection connection, String folderName) throws Exception {
		Statement statement = connection.createStatement();
		String subjectmsg;
		if(message.getSubject()!=null){
			subjectmsg=message.getSubject().replace('\'',':');
		}
		else{
			subjectmsg=null;	
		}

		statement.executeUpdate("alter session set NLS_TIMESTAMP_FORMAT = 'YYYY-MM-DD HH24:MI:SS.FF'");
		statement.executeUpdate("commit");

		
		statement.executeUpdate("INSERT INTO MAIL (MESSAGEID, SUBJECT, SENDERNAME, RECEIVERNAME, CONTENTTYPE, FLAGS, RECEIVEDATE, SENDDATE, MAILSIZE,IMPORTANCE) VALUES('"
				+ folderName + messageCount
				+ "','"
				+ subjectmsg
				+ "','"
				+ senderName
				+ "','"
				+ receiverName
				+ "','"
				+ "gmailviz"
				+ "','"
				+ message.getFlags()
				+ "','"
				+ new java.sql.Timestamp(message.getReceivedDate().getTime())
				+ "','"
				+ new java.sql.Timestamp(message.getSentDate().getTime())
				+ "','"
				+ message.getSize() +"','0')");

		statement.executeUpdate("commit");
		statement.close();
	}

	public static void getAvgResponseTimeForMails(String username) throws Exception
	{
		System.out.println("Get average response time");
		Connection connection = connectToDatabase();
		Statement parentstmt = connection.createStatement();
		ResultSet parentMessage = parentstmt.executeQuery("SELECT * from MAIL");
		long time1 = 0, sign = 0;

		while(parentMessage.next()) {
			int c = 0, evenc = 0;
			String j = "";
            boolean noreplyflag=false;
			Statement replystmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet replyMessage = replystmt.executeQuery("SELECT * from REPLYMAIL where PARENTMESSAGEID = '" + parentMessage.getString("MESSAGEID") + "'");
			
			int replyMessageCount = 0;
			if(!(parentMessage.getString("SENDERNAME").contains(username)))   // When user receives a message first
			{

				while(replyMessage.next()) {
					noreplyflag=true;
					replyMessageCount ++;
					if(c==0)   // for calculating the time between his reply time - message received time  
					{
						time1 = replyMessage.getTimestamp("SENDDATE").getTime() - parentMessage.getTimestamp("RECEIVEDATE").getTime();
						c++;
						sign = 1;
					}
					else   
					{                        
						evenc++; 
						if(sign == 1) {
							sign = -1;
							j = "RECEIVEDATE";
						}
						else if(sign != 1) {
							sign = 1;
							j = "SENDDATE";
						}    

						if(replyMessage.isLast() && (evenc%2)!=0) {

						}
						else {
							time1 = time1 + (sign * replyMessage.getTimestamp(j).getTime());
						}
					}
				}
				replyMessage.close();
			}
			else {             // When user sends a message first
				sign = -1;
				while(replyMessage.next()) {
					noreplyflag=true;
					replyMessageCount ++;
					evenc++;
					if(sign == 1) {
						sign = -1;
						j = "RECEIVEDATE";
					}
					else {
						sign = 1;
						j = "SENDDATE";
					}    

					if(replyMessage.isLast() && (evenc%2)!=0) {

					}
					else {
						time1 = time1 + (sign * replyMessage.getTimestamp(j).getTime());
					}
				}
				replyMessage.close();
			}
			replyMessage = null;

			Double average = (double) (0);
			if(replyMessageCount != 0) {
				average = (double) time1/replyMessageCount;
			}
			if(average==0&& noreplyflag==false){
				average=(double)-1;
			}
			Statement updateImportance = connection.createStatement();
			updateImportance.executeUpdate("UPDATE MAIL SET AVG_RESPONSE_TIME = '" + average + "' WHERE MESSAGEID = '" + parentMessage.getString("MESSAGEID") + "'");

			updateImportance.close();
			replystmt.close();
		}
		parentstmt.executeUpdate("COMMIT");
		parentMessage.close();
		parentstmt.close();
		connection.close();
	}

	public void normalizeMailAverage() throws Exception {
		System.out.println("Normalize average response time");
		Connection connection = connectToDatabase();
		Statement statement = connection.createStatement();

		Double maxAvgResponse = 0.0;
		ResultSet importanceSet = statement.executeQuery("SELECT MAX(AVG_RESPONSE_TIME) AS MAX FROM MAIL WHERE AVG_RESPONSE_TIME <> '-1'");
		if(importanceSet.next()) {
			maxAvgResponse = (double) importanceSet.getFloat("MAX");
		}
		importanceSet.close();

		ResultSet individualSet = statement.executeQuery("SELECT MESSAGEID, SUM(AVG_RESPONSE_TIME) AS SUM FROM MAIL WHERE AVG_RESPONSE_TIME <> '-1' GROUP BY MESSAGEID");
		HashMap<String, Double> importanceHash = new HashMap<String, Double>();

		while(individualSet.next()) {
			if((maxAvgResponse - individualSet.getFloat("SUM")) > 0.0) {
				importanceHash.put(individualSet.getString("MESSAGEID"), (double) (maxAvgResponse - individualSet.getFloat("SUM")));
			}
			else {
				importanceHash.put(individualSet.getString("MESSAGEID"), (double) (0));
			}
		}
		individualSet.close();

		updateMailNormalizedImportance(importanceHash);

		statement.executeUpdate("COMMIT");
		statement.close();
		connection.close();
	}

	private void updateMailNormalizedImportance(HashMap<String, Double> importanceHash) throws Exception {
		Connection connection = connectToDatabase();
		Statement statement = connection.createStatement();

		Set<Map.Entry<String, Double>> set = importanceHash.entrySet();
		Iterator<Map.Entry<String, Double>> setIterator1 = set.iterator();
		//Double sum = 0.0;
		Double max =0.0;
		Double min=Double.MAX_VALUE;
		Double interval_size;
		Double [] interval_arr= new Double[10];
		//find max (max_avg_resp_time-avg_rsp_time)
		while(setIterator1.hasNext()) {
			Map.Entry<String, Double> me = (Map.Entry<String, Double>) setIterator1.next();
			if(me.getValue()> max){
			max = me.getValue();
			}
		}
		//find min (max_avg_resp_time-avg_rsp_time)
		Iterator<Map.Entry<String, Double>> setIterator2 = set.iterator();
		while(setIterator2.hasNext()) {
			Map.Entry<String, Double> me = (Map.Entry<String, Double>) setIterator2.next();
		   if(me.getValue()<min){
			   min=me.getValue();
		   }
		}
		//find interval size and construct temporary array
		interval_size=(max-min)/10;
		for(int i=0;i<10;i++){
			interval_arr[i]=min+i*interval_size;
		}
		// Assign importance
		Iterator<Map.Entry<String, Double>> setIterator3 = set.iterator();
		while(setIterator3.hasNext()) {
			Map.Entry<String, Double> me = (Map.Entry<String, Double>) setIterator3.next();
			Float finalvalue;
			Float decimal;
			int intervalfrom=0;
			int intervalto=0;
			for(int i=0;i<10;i++){
				if(interval_arr[i]>me.getValue()){
					intervalfrom=i-1;
					intervalto=i;
					break;
				}
			}
			decimal=(float)((me.getValue()-interval_arr[intervalfrom])/interval_size);
			while(decimal>1){
				decimal=decimal/10;
			}
			finalvalue=intervalfrom+decimal;
			DecimalFormat twoDForm = new DecimalFormat("#.##");
			statement.executeUpdate("UPDATE MAIL SET IMPORTANCE = '" + Double.valueOf(twoDForm.format(finalvalue)) + "' WHERE MESSAGEID='" + me.getKey() + "'");	
		}
		statement.close();
		connection.close();
	}

	private void updateContactImportance(String contact, Double importance) throws Exception {
		Connection connection = connectToDatabase();

		Statement statement = connection.createStatement();
		statement.executeUpdate("UPDATE CONTACT SET CONTACTIMP = (CONTACTIMP + " + importance + ")/2 WHERE CONTACTEMAIL ='" + contact + "'");
		statement.close();

		connection.close();
	}

	public void updateContactImportance() throws Exception {
		System.out.println("Update contact importance");
		Connection connection = connectToDatabase();
		Statement statement = connection.createStatement();

		Set<Map.Entry<String, String>> contactSet = contactsMap.entrySet();
		Iterator<Map.Entry<String, String>> contactIterator = contactSet.iterator();
		while(contactIterator.hasNext()) {
			Double importance1 = 0.0;
			Double importance2 = 0.0;

			Map.Entry<String, String> me = contactIterator.next();            

			ResultSet contactReceiverResultSet = statement.executeQuery("SELECT MAX(IMPORTANCE) AS MAX FROM MAIL WHERE RECEIVERNAME LIKE '%" + me.getKey() + "%' and MESSAGEID LIKE 'Sent Mail%'");
			if(contactReceiverResultSet.next()) {
				importance1 = (double) contactReceiverResultSet.getFloat("MAX");
			}
			contactReceiverResultSet.close();
			ResultSet contactSenderResultSet = statement.executeQuery("SELECT MAX(IMPORTANCE) AS MAX FROM MAIL WHERE SENDERNAME LIKE '%" + me.getKey() + "%' and MESSAGEID LIKE 'INBOX%'");

			if(contactSenderResultSet.next()) {
				importance2 = (double) contactReceiverResultSet.getFloat("MAX");
			}
			updateContactImportance(me.getValue(), ((importance1 + importance2)/2));
			statement.executeUpdate("COMMIT");
			contactSenderResultSet.close();
		}
		statement.close();
		connection.close();
	}
}