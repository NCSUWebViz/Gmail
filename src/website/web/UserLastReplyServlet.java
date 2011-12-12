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

public class UserLastReplyServlet extends HttpServlet {
	private static String jdbcURL;
	private static String user;
	private static String password;
	private static final SimpleDateFormat yearformat = new SimpleDateFormat("yyyy");
	private static final SimpleDateFormat monthformat = new SimpleDateFormat("MM");
	private static final SimpleDateFormat dayformat = new SimpleDateFormat("dd");
	
	public void initialize() {
		jdbcURL = getServletConfig().getInitParameter("JDBC_URL");
		user = getServletConfig().getInitParameter("user");
		password = getServletConfig().getInitParameter("password");
	}
	
	protected void doGet(HttpServletRequest request,HttpServletResponse response) {
		PrintWriter pw = null;
		HttpSession httpsession = request.getSession(true);
		try {
			initialize();
			String userName = httpsession.getValue("email_id").toString();
			
			pw = response.getWriter();
			pw.write(getLastReply(userName));
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
		Connection connection = DriverManager.getConnection(jdbcURL, user,password);
		return connection;
	}
	
	public static String getLastReply(String userName) throws Exception {		
		Connection connection = connectToDatabase();
		Timestamp[] lastreply = new Timestamp[100];

		String[] user = new String[100];
		
		int i = 0, j;
		String jsonreply = "";
		
		Statement stmtd = connection.createStatement();
		ResultSet setd = stmtd.executeQuery("SELECT CONTACTEMAIL FROM CONTACT WHERE CONTACTEMAIL NOT LIKE '%,%' AND ROWNUM <= 10 ORDER BY CONTACTIMP DESC");
		while(setd.next()) {
				user[i] = setd.getString("CONTACTEMAIL");
				i++;
		}
		setd.close();
		
		Statement stmt = connection.createStatement();		
		for(j=0; j<i; j++) {
			ResultSet set = stmt.executeQuery("SELECT * FROM MAIL WHERE RECEIVERNAME LIKE '" +  user[j] + "' ORDER BY RECEIVEDATE DESC");
			boolean flag = false;
			while(set.next()) {
				if(set.getString("SENDERNAME").contains(userName)) {
					flag = true;
					lastreply[j] = set.getTimestamp("SENDDATE");
				}
			}
			if(!flag) {
				lastreply[j] = null;
			}
			set.close();
		}
		stmt.close();
		
		jsonreply = jsonreply + "{\"" + "UserLastReplied\":[";
		boolean flag = false;
		
		for(j=0; j<=i; j++) {
			flag = true;
			jsonreply += "{\"email_id\":\"" + user[j] + "\"," + 
						"\"last_replied_time\":\"" + lastreply[j] + "\"," + 
						"\"year\":\"" + timestampToYear(lastreply[j]) + "\"," + 
						"\"month\":\"" + timestampToMonth(lastreply[j]) + "\"," + 
						"\"day\":\"" + timestampToDay(lastreply[j]) + "\",";
			
			if(lastreply[j] != null) {
				jsonreply += "\"msec\":\"" + lastreply[j].getTime()+ "\"},";
			}
			else {
				jsonreply += "\"msec\":\"\"},";
			}
		}
		
		if(flag == true) {
			jsonreply = jsonreply.substring(0, jsonreply.length() -1);
		}
		jsonreply+= "],\"success\": \"true\"}";
		connection.close();
		return jsonreply;
	}
	
	public static String timestampToYear(Timestamp timestamp) {
		if (timestamp == null) {
			return "";
		}
		else {
			return yearformat.format((java.util.Date) timestamp);
		}
	}
	
	public static String timestampToMonth(Timestamp timestamp) {
		if (timestamp == null) {
			return "";
		}
		else {
			return monthformat.format((java.util.Date) timestamp);
		}
	}

	public static String timestampToDay(Timestamp timestamp) {
		if (timestamp == null) {
			return "";
		}
		else {
			return dayformat.format((java.util.Date) timestamp);
		}
	}
}