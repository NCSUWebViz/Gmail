package website.web;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GetContactServlet extends HttpServlet {
	private static String jdbcURL;
	private static String user;
	private static String password;

	public void initialize() {
		jdbcURL = getServletConfig().getInitParameter("JDBC_URL");
		user = getServletConfig().getInitParameter("user");
		password = getServletConfig().getInitParameter("password");
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter pw = null;
		try {
			initialize();
			pw = response.getWriter();
			pw.write(getContacts());
		} catch (Exception e) {
			e.printStackTrace();
			pw.write("{\"success\": \"false\"}");
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
		contactStringBuilder.append("{\"ContactImportance\": [");
		boolean flag = false;
		
		ResultSet contactResultSet = statement.executeQuery("SELECT CONTACTEMAIL FROM CONTACT");
		while (contactResultSet.next()) {
			flag = true;
			contactStringBuilder.append("{\"email_id\":\"" + contactResultSet.getString("CONTACTEMAIL") + "\"},");
		}
		contactResultSet.close();
		contactResultString = contactStringBuilder.toString();
		
		if(flag == true) {
			contactResultString = contactResultString.substring(0, contactResultString.length() - 1);
		}
		
		contactResultString = contactResultString + "],\"success\": \"true\"}";
		connection.close();
		return contactResultString;
	}
}