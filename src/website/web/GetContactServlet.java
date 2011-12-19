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

public class GetContactServlet extends HttpServlet {
	private static String dbUserName, dbPassword, dbJDBC;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		HttpSession httpsession = request.getSession(true);
		dbUserName = httpsession.getValue("dbUsername").toString();
		dbPassword = httpsession.getValue("dbPassword").toString();
		dbJDBC = httpsession.getValue("dbJDBC").toString();
		PrintWriter pw = null;
		try {
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
		Connection connection = DriverManager.getConnection(dbJDBC, dbUserName, dbPassword);
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