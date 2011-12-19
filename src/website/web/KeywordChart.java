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

public class KeywordChart extends HttpServlet {
	private static String dbJDBC, dbUserName, dbPassword;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		HttpSession httpsession = request.getSession(true);
		dbUserName = httpsession.getValue("dbUsername").toString();
		dbPassword = httpsession.getValue("dbPassword").toString();
		dbJDBC = httpsession.getValue("dbJDBC").toString();
		PrintWriter pw = null;
		try {
			String responseString = getKeyWordJSON();
			
			pw = response.getWriter();
			pw.write(responseString);
		} 
		catch (Exception e) {
			e.printStackTrace();
			pw.write("{\"success\": \"false\"}");
		}
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
		doGet(request, response);
	}
	
	public static String getKeyWordJSON() throws Exception {
		Connection connection = connectToDatabase();
		Statement statement = connection.createStatement();
		String keywordString = "{Keywords : [";
		boolean flag = false;
		
		ResultSet keywordSet = statement.executeQuery("SELECT SKEYWORD, IMPORTANCE FROM KEYWORD");
		while(keywordSet.next()) {
			flag = true;
			keywordString = keywordString + "{'keyword':'" + keywordSet.getString("SKEYWORD") + "','importance':'" + keywordSet.getFloat("IMPORTANCE") + "'},";
		}
		keywordSet.close();
		
		if(flag) {
			keywordString = keywordString.substring(0, keywordString.length() -1);
		}
		keywordString = keywordString + "],\"success\": \"true\"}";
		
		connection.close();
		return keywordString;
	}
		
	public static Connection connectToDatabase() throws Exception {
		Class.forName("oracle.jdbc.driver.OracleDriver");
		Connection connection = DriverManager.getConnection(dbJDBC, dbUserName, dbPassword);
		return connection;
	}
}
