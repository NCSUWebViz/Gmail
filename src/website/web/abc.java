package website.web;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class abc extends HttpServlet{
	Properties databaseConfig = new Properties();

	private static String jdbcURL;
	private static String user;
	private static String password;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter pw = null;
		try {
			initializeDBConnectionParameters();
			updateContactImportance();
			pw = response.getWriter();
			pw.write("{\"success\": \"true\"}");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	//accept post request
	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
		doGet(request, response);
	}
	
	public void initializeDBConnectionParameters() {
		jdbcURL = getServletConfig().getInitParameter("JDBC_URL");
		user = getServletConfig().getInitParameter("user");
		password = getServletConfig().getInitParameter("password");
	}
	
	public static Connection connectToDatabase() throws Exception {
		Class.forName("oracle.jdbc.driver.OracleDriver");
		Connection connection = DriverManager.getConnection(jdbcURL, user, password);
		return connection;
	}
	
	public void updateContactImportance() throws Exception {
		System.out.println("Update contact importance");
		Connection connection = connectToDatabase();
		Statement statement = connection.createStatement();

		ResultSet contactReceiverResultSet1 = statement.executeQuery("SELECT DISTINCT CONTACTEMAIL as contactemail FROM CONTACT WHERE CONTACTEMAIL NOT LIKE '%,%'");
		while(contactReceiverResultSet1.next()) {
				Double importance1 = 0.0;
				Double importance2 = 0.0;
				System.out.println("name : " + contactReceiverResultSet1.getString("contactemail"));
				
				ResultSet contactReceiverResultSet = statement.executeQuery("SELECT MAX(IMPORTANCE) AS MAX FROM MAIL WHERE RECEIVERNAME LIKE '%" + contactReceiverResultSet1.getString("contactemail") + "%' and MESSAGEID LIKE 'Sent Mail%'");
				if(contactReceiverResultSet.next()) {
					importance1 = (double) contactReceiverResultSet.getFloat("MAX");
				}
				contactReceiverResultSet.close();
				ResultSet contactSenderResultSet = statement.executeQuery("SELECT MAX(IMPORTANCE) AS MAX FROM MAIL WHERE SENDERNAME LIKE '%" + contactReceiverResultSet1.getString("contactemail") + "%' and MESSAGEID LIKE 'INBOX%'");
	
				if(contactSenderResultSet.next()) {
					importance2 = (double) contactReceiverResultSet.getFloat("MAX");
				}
				updateContactImportance(contactReceiverResultSet1.getString(0), ((importance1 + importance2)/2));
				statement.executeUpdate("COMMIT");
				contactSenderResultSet.close();
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

}
