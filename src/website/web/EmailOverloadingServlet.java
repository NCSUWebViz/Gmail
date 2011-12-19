package website.web;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class EmailOverloadingServlet extends HttpServlet{
	private static String dbUserName, dbPassword, dbJDBC;
	
	public static Connection connectToDatabase() throws Exception {
		Class.forName("oracle.jdbc.driver.OracleDriver");
		Connection connection = DriverManager.getConnection(dbJDBC, dbUserName, dbPassword);
		return connection;
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		HttpSession httpsession = request.getSession(true);
		dbUserName = httpsession.getValue("dbUsername").toString();
		dbPassword = httpsession.getValue("dbPassword").toString();
		dbJDBC = httpsession.getValue("dbJDBC").toString();
		
		PrintWriter pw = null;
		try {
			pw = response.getWriter();
			pw.write(getOverloadedMails());
		}
		catch (Exception e) {
			e.printStackTrace();
			pw.write("{\"success\": \"false\"}");
		}
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
		doGet(request, response);
	}
	
	public String getOverloadedMails() throws Exception{
		String ResultString = "";
		float minimp = 0;
		float maximp = 3;
		java.util.Date today = new java.util.Date();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -1);
		java.util.Date monthback = calendar.getTime();
		Timestamp mintime= new java.sql.Timestamp(monthback.getTime());
		Timestamp maxtime=new java.sql.Timestamp(today.getTime());
		Connection connection = connectToDatabase();
		Statement statement = connection.createStatement();
		statement.executeUpdate("alter session set NLS_TIMESTAMP_FORMAT = 'YYYY-MM-DD HH24:MI:SS.FF'");
		
		ResultString="{\"InboxOverLoading\":[";
		ResultSet lowImpSet=statement.executeQuery("SELECT * FROM MAIL WHERE(RECEIVEDATE BETWEEN '"+new java.sql.Timestamp(mintime.getTime())+"' AND '"+new java.sql.Timestamp(maxtime.getTime())+"') AND (IMPORTANCE >="+minimp+" AND IMPORTANCE <"+maximp+") ");
     	while(lowImpSet.next()){	
			ResultString+="{\"Receivedate\":\""+lowImpSet.getTimestamp("RECEIVEDATE")+"\",\"ReceivedateValue\":\""+lowImpSet.getTimestamp("RECEIVEDATE").getTime()+"\",\"Importance\":\""+lowImpSet.getString("IMPORTANCE")+"\",\"Subject\":\""+lowImpSet.getString("SUBJECT")+"\",\"Sendername\":\""+lowImpSet.getString("SENDERNAME")+"\",\"Receivername\":\""+lowImpSet.getString("RECEIVERNAME")+"\"},";
		}
		ResultString = ResultString.substring(0, ResultString.length() -1);
		ResultString+="],\"success\": \"true\"}";
		connection.close();
		return ResultString;
	}
}
