package website.web;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GetContactImportanceServlet extends HttpServlet {

	private static final String jdbcURL = "jdbc:oracle:thin:@ora.csc.ncsu.edu:1523:orcl";
	private static final String user = "gapandit";
	private static final String password = "001000715";
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter pw = null;
		try {
			pw = response.getWriter();
			pw.write(getContacts());
		}
		catch (Exception e) {
			e.printStackTrace();
			pw.write("{\"failure\": \"true\"}");
		}
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
		doGet(request, response);
	}
	
	public static Connection connectToDatabase() throws Exception {
		Class.forName("oracle.jdbc.driver.OracleDriver");
		Connection connection = DriverManager.getConnection(jdbcURL, user, password);
		return connection;
	}
	
	public String getContacts() throws Exception {
		Connection connection = connectToDatabase();
		Statement statement = connection.createStatement();
		String contactResultString = null;
		
		StringBuilder contactStringBuilder = new StringBuilder();
		contactStringBuilder.append("{\"IndividualContactImportance\": [");
		ResultSet contactResultSet1 = statement.executeQuery("SELECT CONTACTEMAIL, CONTACTIMP FROM CONTACT WHERE CONTACTEMAIL NOT LIKE '%,%' ORDER BY CONTACTIMP DESC ");
		while(contactResultSet1.next()) {
			contactStringBuilder.append("{\"email_id\":\"" + contactResultSet1.getString("CONTACTEMAIL")+"\","+ "\"importance\":\"" +contactResultSet1.getString("CONTACTIMP") +"\"},"); 
		}
		contactResultSet1.close();
		contactResultString  = contactStringBuilder.toString();
		contactResultString = contactResultString.substring(0, contactResultString.length() -1);
		contactStringBuilder.setLength(0);
		contactStringBuilder.append(contactResultString);
		contactStringBuilder.append("]");
		contactStringBuilder.append(",\"GroupedContactImportance\": [");
		ResultSet contactResultSet2 = statement.executeQuery("SELECT CONTACTEMAIL, CONTACTIMP FROM CONTACT WHERE CONTACTEMAIL LIKE '%,%' ORDER BY CONTACTIMP DESC ");
		while(contactResultSet2.next()) {
			contactStringBuilder.append("{\"email_id\":\"" + contactResultSet2.getString("CONTACTEMAIL")+"\","+ "\"importance\":\"" +contactResultSet2.getString("CONTACTIMP") +"\"},"); 
		}
		contactResultSet2.close();
		
		contactResultString  = contactStringBuilder.toString();
		
		contactResultString = contactResultString.substring(0, contactResultString.length() -1);
		contactResultString = contactResultString + "],\"success\": \"true\"}";
		connection.close();
		return contactResultString;
	}
}