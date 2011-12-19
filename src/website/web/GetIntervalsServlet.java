package website.web;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Properties;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.text.SimpleDateFormat;

public class GetIntervalsServlet extends HttpServlet {
	Properties databaseConfig = new Properties();
	private static String dbJDBC, dbUserName, dbPassword;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		HttpSession httpsession = request.getSession(true);
		dbUserName = httpsession.getValue("dbUsername").toString();
		dbPassword = httpsession.getValue("dbPassword").toString();
		dbJDBC = httpsession.getValue("dbJDBC").toString();
		PrintWriter pw = null;
		try {
			pw = response.getWriter();
			pw.write(getIntervals());
		}
		catch (Exception e) {
			e.printStackTrace();
			pw.write("{\"success\": \"false\"}");
		}
	}
	
	private static final SimpleDateFormat monthDayYearformatter = new SimpleDateFormat("MMM dd, yyyy");
	private static final SimpleDateFormat monthDayformatter = new SimpleDateFormat("MMMMM dd");
	
	public static String timestampToMonthDayYear(Timestamp timestamp) {
		if (timestamp == null) {      
			return null;
		}
		else {
			return monthDayYearformatter.format((java.util.Date) timestamp);
		}
	}
	  
	public static String timestampToMonthDay(Timestamp timestamp) {
		if (timestamp == null) {
			return null;
		}
		else {
			return monthDayformatter.format((java.util.Date) timestamp);
		}
	}
	
	public static String getIntervals() throws Exception {
		Connection connection = connectToDatabase();
		Statement statement = connection.createStatement();
		
		StringBuilder jsonString = new StringBuilder();
		jsonString.append("{\"Intervals\":[");
		Timestamp timestamp;
		
		boolean flag = false;
		
		ResultSet resultSet2 = statement.executeQuery("SELECT MAX(TOINTERVAL) AS MAX FROM INBOXAGE");
		while(resultSet2.next()) {
			flag = true;
			timestamp = resultSet2 .getTimestamp("MAX");
			jsonString.append("{\"timestamp\":\"" + timestampToMonthDayYear(timestamp) + "\", \"msec\":\"" + timestamp.getTime() + "\", \"timestampString\":\"" + timestamp + "\"},");
		}
		resultSet2.close();
		
		ResultSet resultSet1 = statement.executeQuery("SELECT FROMINTERVAL FROM INBOXAGE ORDER BY FROMINTERVAL DESC");
		while(resultSet1.next()) {
			flag = true;
			timestamp = resultSet1.getTimestamp("FROMINTERVAL");
			jsonString.append("{\"timestamp\":\"" + timestampToMonthDayYear(timestamp) + "\", \"msec\":\"" + timestamp.getTime() + "\", \"timestampString\":\"" + timestamp + "\"},");
		}
		resultSet1.close();
		
		String json = jsonString.toString();
		if(flag == true) {
			json = jsonString.substring(0, jsonString.length()-1);
		}
		
		json = json + "], \"success\": \"true\"}";
		return json;
	}
	
	public static Connection connectToDatabase() throws Exception {
		Class.forName("oracle.jdbc.driver.OracleDriver");
		Connection connection = DriverManager.getConnection(dbJDBC, dbUserName, dbPassword);
		return connection;
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
		doGet(request, response);
	}
}
