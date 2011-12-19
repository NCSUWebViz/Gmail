package website.web;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LastReplyTimeServlet extends HttpServlet {
	private static String dbJDBC, dbUserName, dbPassword;
	
	static String param1 = null;
	static String param2 = null;
		
	private static final SimpleDateFormat yearformat = new SimpleDateFormat("yyyy");
	private static final SimpleDateFormat monthformat = new SimpleDateFormat("MM");
	private static final SimpleDateFormat dayformat = new SimpleDateFormat("dd");
	
	protected void doGet(HttpServletRequest request,HttpServletResponse response) {
		HttpSession httpsession = request.getSession(true);
		dbUserName = httpsession.getValue("dbUsername").toString();
		dbPassword = httpsession.getValue("dbPassword").toString();
		dbJDBC = httpsession.getValue("dbJDBC").toString();
		PrintWriter pw = null;
		try {
			String userName = httpsession.getValue("email_id").toString();
			pw = response.getWriter();
			pw.write(getLastReply(userName));
		} catch (Exception e) {
			e.printStackTrace();
			pw.write("{\"success\": \"false\"}");
		}
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) {
		doGet(request, response);
	}

	public static Connection connectToDatabase() throws Exception {
		Class.forName("oracle.jdbc.driver.OracleDriver");
		Connection connection = DriverManager.getConnection(dbJDBC, dbUserName, dbPassword);
		return connection;
	}
	
	public static String getLastReply(String userName) throws Exception
	{
		
		Connection connection = connectToDatabase();
		Timestamp[] lastreply = new Timestamp[100];
		Timestamp[] flastreply = new Timestamp[100];
		long reply;

		String[] user = new String[100];
		String[] fuser = new String[100];
		int i = 0, j, k;
		String jsonreply = "";
		Statement stmtd = connection.createStatement();
		ResultSet setd = stmtd.executeQuery("SELECT DISTINCT RECEIVERNAME FROM REPLYMAIL");
		while(setd.next())
		{
			user[i] = setd.getString("RECEIVERNAME");
			i++;
		}
		setd.close();
		
		Statement stmt = connection.createStatement();
		
		for(j=0;j<i;j++)
		{
			ResultSet set = stmt.executeQuery("SELECT * FROM REPLYMAIL ORDER BY RECEIVEDATE ASC");
			while(set.next())
			{	
				if(set.getString("SENDERNAME").contains(userName) && set.getString("RECEIVERNAME").equals(user[j]))
				{
							lastreply[j] = set.getTimestamp("RECEIVEDATE");
				}
			}
			set.close();
		}
		
		k=0;
		for(j=0;j<i;j++)
		{
			if(lastreply[j] != null)
			{
				fuser[k] = user[j];
				reply = lastreply[j].getTime();
				flastreply[k] = new Timestamp(reply);
				flastreply[k].setTime(reply);
				k++;
			}
		}
		jsonreply = jsonreply + "{\"" + "UserLastReplied\":[";
		
		for(j=0;j<k;j++){
			jsonreply += "{\"email_id\":\"" +fuser[j]+"\","+ "\"last_replied_time\":\"" +flastreply[j]+"\","+"\"year\":\"" +
		     timestampToYear(flastreply[j]) + "\","+"\"month\":\"" + timestampToMonth(flastreply[j]) + "\","+"\"day\":\"" +
		     timestampToDay(flastreply[j]) + "\","+"\"msec\":\"" + flastreply[j].getTime()+ "\"},";
		  }

		jsonreply = jsonreply.substring(0, jsonreply.length() -1);
		jsonreply+= "],\"success\": \"true\"}";
		connection.close();
		return jsonreply;

	}
	
	public static String timestampToYear(Timestamp timestamp) {
		if (timestamp == null) {
			return null;
		}
		else {
			return yearformat.format((java.util.Date) timestamp);
		}
	}
	
	public static String timestampToMonth(Timestamp timestamp) {
		if (timestamp == null) {
			return null;
		}
		else {
			return monthformat.format((java.util.Date) timestamp);
		}
	}

	public static String timestampToDay(Timestamp timestamp) {
		if (timestamp == null) {
			return null;
		}
		else {
			return dayformat.format((java.util.Date) timestamp);
		}
	}


}