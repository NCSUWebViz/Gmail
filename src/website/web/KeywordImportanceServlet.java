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

public class KeywordImportanceServlet extends HttpServlet {
	private static String dbJDBC, dbUserName, dbPassword;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		HttpSession httpsession = request.getSession(true);
		dbUserName = httpsession.getValue("dbUsername").toString();
		dbPassword = httpsession.getValue("dbPassword").toString();
		dbJDBC = httpsession.getValue("dbJDBC").toString();
		PrintWriter pw = null;
		String keywordString = request.getParameter("keywords");
		keywordString = keywordString.substring(0, keywordString.length()-1);
		
		String[] keywords = keywordString.split(",");
		
		try {
			pw = response.getWriter();
			pw.write(setKeywordImportance(keywords));
		} 
		catch (Exception e) {
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
	
	public static String setKeywordImportance(String[] keywords) throws Exception {
		String responseString;
		
		Connection connection = connectToDatabase();
		Statement statement = connection.createStatement();
		
		for (String keyword : keywords) {
			if(!checkKeywordExists(keyword)) {
				insertIntoDB(keyword,getCount(keyword, statement), statement);
			}			
		}
		statement.executeUpdate("COMMIT");
		responseString = "{\"success\": \"true\"}";
		return responseString;
	}
	
	private static boolean checkKeywordExists(String keyword) throws Exception {
		return false;
	}
	
	public static void insertIntoDB(String keyword, int count, Statement statement) throws Exception {
		statement.executeUpdate("INSERT INTO KEYWORD VALUES('" + keyword + "','" + count + "')");		
	}
	
	public static int getCount(String keyword, Statement statement) throws Exception {
		int count = 0;
		ResultSet mailResultSet = statement.executeQuery("SELECT COUNT(*) AS COUNT FROM MAIL WHERE SUBJECT LIKE '%" + keyword + "%'");
		if(mailResultSet.next()) {
			count = mailResultSet.getInt("COUNT");
		}
		mailResultSet.close();
		return count;
	}
}
