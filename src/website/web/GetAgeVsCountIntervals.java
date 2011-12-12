package website.web;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

public class GetAgeVsCountIntervals extends HttpServlet {	
	private static String jdbcURL;
	private static String user;
	private static String password;
	
	@SuppressWarnings("unused")
	private static final SimpleDateFormat monthDayYearformatter = new SimpleDateFormat("MMMMM dd, yyyy");
	@SuppressWarnings("unused")
	private static final SimpleDateFormat monthDayformatter = new SimpleDateFormat("MMMMM dd");
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter pw = null;
		String importance = request.getParameter("importance");
		String fromTimestamp = request.getParameter("from");
		String toTimestamp = request.getParameter("to");
		initializeDBConnectionParameters();
		
		HashMap<String, String> importanceMap = new HashMap<String, String>();
		importanceMap.put("Very Important", "10-8");
		importanceMap.put("Important", "7-5");
		importanceMap.put("Less Important", "4-2");
		importanceMap.put("Not Important", "1-0");
		
		String importanceKey = importanceMap.get(importance);		
		try {
			pw = response.getWriter();
			String responseString = getAgeVsCountScatterPlot(fromTimestamp, toTimestamp, Double.valueOf(importanceKey.substring(0, importanceKey.indexOf("-"))).doubleValue() , Double.valueOf(importanceKey.substring(importanceKey.indexOf("-") + 1)).doubleValue());
			pw.write(responseString);
		}
		catch (Exception e) {
			e.printStackTrace();
			pw.write("{\"success\": \"false\"}");
		}
	}
	
	public void initializeDBConnectionParameters() {
		jdbcURL = getServletConfig().getInitParameter("JDBC_URL");
		user = getServletConfig().getInitParameter("user");
		password = getServletConfig().getInitParameter("password");
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
		doGet(request, response);
	}
	
	public static Connection connectToDatabase() throws Exception {
		Class.forName("oracle.jdbc.driver.OracleDriver");
		Connection connection = DriverManager.getConnection(jdbcURL, user, password);
		return connection;
	}
	
	public static String getAgeVsCountScatterPlot(String start, String end, double fromimp,double toimp) throws Exception {
		String ResultString="";
		Timestamp maxtimestamp=new Timestamp((new java.util.Date()).getTime());
		Timestamp startinterval=gettimeStamp(start);
		Timestamp endinterval= gettimeStamp(end);
	    
		Connection connection = connectToDatabase();
		Statement statement1 = connection.createStatement();
		Statement statement2 = connection.createStatement();		
		statement1.executeUpdate("alter session set NLS_TIMESTAMP_FORMAT = 'YYYY-MM-DD HH24:MI:SS.FF'");
		statement2.executeUpdate("alter session set NLS_TIMESTAMP_FORMAT = 'YYYY-MM-DD HH24:MI:SS.FF'");
		statement1.execute("commit");
		statement2.execute("commit");
		String query = "SELECT IMPORTANCE, SENDERNAME, SUBJECT, RECEIVEDATE FROM MAIL WHERE(RECEIVEDATE BETWEEN '"+new java.sql.Timestamp(endinterval.getTime())+"' AND '"+new java.sql.Timestamp(startinterval.getTime())+"') AND (IMPORTANCE BETWEEN " + toimp + " AND " + fromimp + ")";
		ResultSet maxtimestampset = statement2.executeQuery("SELECT MAX(RECEIVEDATE) AS MAXTM FROM MAIL WHERE(RECEIVEDATE BETWEEN '"+new java.sql.Timestamp(endinterval.getTime())+"' AND '"+new java.sql.Timestamp(startinterval.getTime())+"') AND (IMPORTANCE BETWEEN " + toimp + " AND " + fromimp + ")");

		if(maxtimestampset.next()){
			maxtimestamp=maxtimestampset.getTimestamp("MAXTM");
		}
		maxtimestampset.close();
		
		ResultSet scatterset = statement1.executeQuery(query);
		ResultString="{\"InboxAgeData\":[";
		boolean flag = false;
		
		while(scatterset.next()){
			flag = true;
			ResultString += "{\"TimeStamp\":\""+scatterset.getTimestamp("RECEIVEDATE")+"\",\"TimeStampValue\":\""+(scatterset.getTimestamp("RECEIVEDATE").getTime())+"\",\"Importance\":\""+scatterset.getString("IMPORTANCE")+"\",\"Subject\":\""+scatterset.getString("SUBJECT")+"\",\"Sendername\":\""+scatterset.getString("SENDERNAME")+"\"},";
		}
		scatterset.close();
		
		if(flag == true) {
			ResultString = ResultString.substring(0, ResultString.length() -1);
		}
		
		ResultString += "],\"success\": \"true\"}";
		
		connection.close();
		return ResultString;
	}
	
	@SuppressWarnings("deprecation")
	public static Timestamp gettimeStamp(String ss) {
		DateTimeFormatter fullformatter = new DateTimeFormatterBuilder()
        .appendYear(4,4).appendLiteral('-')
        .appendMonthOfYear(2).appendLiteral('-')
        .appendDayOfMonth(2).appendLiteral(' ')
		.appendHourOfDay(2).appendLiteral(':')
		.appendMinuteOfHour(2).appendLiteral(':')
		.appendSecondOfMinute(2).appendLiteral('.')
		.appendFractionOfSecond(6,6)
		.toFormatter();

		
        DateTime dt = fullformatter.parseDateTime(ss);
        Timestamp finaltimestamp=new Timestamp(dt.getYear()-1900,dt.getMonthOfYear()-1,dt.getDayOfMonth(),dt.getHourOfDay(),dt.getMinuteOfHour(),dt.getSecondOfMinute(),dt.getMillisOfSecond());
        return finaltimestamp;
     }	
}