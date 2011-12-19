package website.web;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class SendContactServlet extends HttpServlet {
	private static String dbJDBC, dbUserName, dbPassword;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		HttpSession httpsession = request.getSession(true);
		dbUserName = httpsession.getValue("dbUsername").toString();
		dbPassword = httpsession.getValue("dbPassword").toString();
		dbJDBC = httpsession.getValue("dbJDBC").toString();
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
		Connection connection = DriverManager.getConnection(dbJDBC, dbUserName, dbPassword);
		return connection;
	}
	
	public String getContacts() throws Exception {
		Connection connection = connectToDatabase();
		Statement statement = connection.createStatement();
		String contactResultString = null;
		
		StringBuilder contactStringBuilder = new StringBuilder();
		contactStringBuilder.append("{\"IndividualContactImportance\": [");
		ResultSet contactResultSet1 = statement.executeQuery("SELECT CONTACTEMAIL, CONTACTIMP FROM CONTACT WHERE CONTACTEMAIL NOT LIKE '%,%' AND CONTACTIMP > 0 ORDER BY CONTACTIMP DESC ");
		while(contactResultSet1.next()) {
			contactStringBuilder.append("{\"email_id\":\"" + contactResultSet1.getString("CONTACTEMAIL")+"\","+ "\"importance\":\"" +contactResultSet1.getString("CONTACTIMP") +"\"},"); 
		}
		contactResultSet1.close();
		contactResultString  = contactStringBuilder.toString();
		contactResultString = contactResultString.substring(0, contactResultString.length() -1);
		contactStringBuilder.setLength(0);
		contactStringBuilder.append(contactResultString);
		contactStringBuilder.append("],\"success\": \"true\"}");
		connection.close();
		return contactStringBuilder.toString();
	}
}