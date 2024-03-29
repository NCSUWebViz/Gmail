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
	private static String dbUserName, dbPassword, dbJDBC, param1, param2;
	
	protected void doGet(HttpServletRequest request,HttpServletResponse response) {
		HttpSession httpsession = request.getSession(true);
		dbUserName = httpsession.getValue("dbUsername").toString();
		dbPassword = httpsession.getValue("dbPassword").toString();
		dbJDBC = httpsession.getValue("dbJDBC").toString();
		
		PrintWriter pw = null;
		param1 = httpsession.getValue("email_id").toString();
		param2 = httpsession.getValue("password").toString();
		try {
			pw = response.getWriter();
			pw.write(getLastReply());
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
	
	public static String getLastReply() throws Exception {
		String jsonreply = "";
		Connection connection = connectToDatabase();		
		String[] user = new String[1000];
		Float[] importance = new Float[1000];
		int i=0;
		
		jsonreply = jsonreply + "{\"" + "Importance\":[";
		Statement imp = connection.createStatement();
		ResultSet simp = imp.executeQuery("SELECT DISTINCT CONTACTEMAIL, CONTACTIMP FROM CONTACT WHERE CONTACTEMAIL NOT LIKE '%,%' AND ROWNUM <= 10 ORDER BY CONTACTIMP DESC");
		boolean flag = false;
		
		while(simp.next()) {
			flag = true;
			importance[i] = simp.getFloat("CONTACTIMP");
			user[i] = simp.getString("CONTACTEMAIL");
			jsonreply += "{\"email_id\":\""+user[i]+"\",\"user_imp\":\""+importance[i]+"\"},";
			i++;
		}
		
		if(flag) {
			jsonreply = jsonreply.substring(0, jsonreply.length() -1);
		}
		
		jsonreply+= "],\"success\": \"true\"}";
		connection.close();
		return jsonreply;
	}

}