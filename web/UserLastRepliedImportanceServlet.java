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

public class UserLastRepliedImportanceServlet extends HttpServlet {
	private static String jdbcURL;
	private static String user;
	private static String password;
	
	static String param1 = null;
	static String param2 = null;
	
	public void initializeDBConnectionParameters() {
		jdbcURL = getServletConfig().getInitParameter("JDBC_URL");
		user = getServletConfig().getInitParameter("user");
		password = getServletConfig().getInitParameter("password");
	}
	
	protected void doGet(HttpServletRequest request,HttpServletResponse response) {
		PrintWriter pw = null;
		initializeDBConnectionParameters();

		HttpSession session = request.getSession(true);
		param1 = (String) session.getAttribute("email_id");
		param2 = (String) session.getAttribute("password");
		
		try {
			pw = response.getWriter();
			pw.write(getLastReply());
		}
		catch (Exception e) {
			e.printStackTrace();
			pw.write("{\"failure\": \"true\"}");
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

	public static String getLastReply() throws Exception {
		String jsonreply = "";
		Connection connection = connectToDatabase();		
		String[] user = new String[100];
		Float[] importance = new Float[100];
		int i=0;
		
		jsonreply = jsonreply + "{\"" + "Importance\":[";
		boolean flag = false;
		
		Statement imp = connection.createStatement();
		ResultSet simp = imp.executeQuery("SELECT DISTINCT CONTACTEMAIL, CONTACTIMP FROM CONTACT WHERE CONTACTEMAIL NOT LIKE '%com,%' ORDER BY CONTACTIMP ASC");
		while(simp.next()) {
			flag = true;
			importance[i] = simp.getFloat("CONTACTIMP");
			user[i] = simp.getString("CONTACTEMAIL");
			jsonreply += "{\"email_id\":\""+user[i]+"\",\"user_imp\":\""+importance[i]+"\"},";
			i++;

		}
		
		if(flag == true) {
			jsonreply = jsonreply.substring(0, jsonreply.length() -1);
		}
		
		jsonreply+= "]}";
		
		connection.close();
		return jsonreply;
	}

}