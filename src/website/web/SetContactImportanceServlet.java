package website.web;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class SetContactImportanceServlet extends HttpServlet {
	private static String dbJDBC, dbUserName, dbPassword;
	HashMap<String, String> contactInfo = new HashMap<String, String>();

	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		HttpSession httpsession = request.getSession(true);
		dbUserName = httpsession.getValue("dbUsername").toString();
		dbPassword = httpsession.getValue("dbPassword").toString();
		dbJDBC = httpsession.getValue("dbJDBC").toString();
		
		String parameter = request.getParameter("obj");
		String[] temp = parameter.split(";");
		for (int i = 0; i < temp.length; i++) {
			contactInfo.put(temp[i].substring(0, temp[i].indexOf(":")),	temp[i].substring(temp[i].indexOf(":") + 1));
		}
		PrintWriter pw = null;
		try {
			pw = response.getWriter();
			pw.write(populateContact(contactInfo));
		} catch (Exception e) {
			pw.write("\"success\": \"false\"");
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

	@SuppressWarnings("unused")
	public String populateContact(HashMap<String, String> contactInfo) throws Exception {
		Connection connection = connectToDatabase();
		Statement statement = connection.createStatement();
		ResultSet contactResultSet = null;
		Set<Map.Entry<String, String>> set = contactInfo.entrySet();
		Iterator<Map.Entry<String, String>> i = set.iterator();
		while (i.hasNext()) {
			Map.Entry<String, String> me = (Map.Entry<String, String>) i.next();
			contactResultSet = statement.executeQuery("UPDATE CONTACT SET CONTACTIMP="
							+ me.getValue() + " WHERE CONTACTEMAIL='"
							+ me.getKey() + "'");
		}
		statement.executeUpdate("COMMIT");
		return("{\"success\": \"true\"}");
	}
}