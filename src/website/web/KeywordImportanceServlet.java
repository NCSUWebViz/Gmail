package website.web;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class KeywordImportanceServlet extends HttpServlet {

	private static final String jdbcURL = "jdbc:oracle:thin:@ora.csc.ncsu.edu:1523:orcl";
	private static final String user = "gapandit";
	private static final String password = "001000715";

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) {
		PrintWriter pw = null;
		String[] keywords = null;
		keywords = request.getParameterValues("keywords");
		try {
			pw = response.getWriter();
			pw.write(setKeywordImportance(keywords));
		} catch (Exception e) {
			e.printStackTrace();
			pw.write("{\"failure\": \"true\"}");
		}
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) {
		doGet(request, response);
	}

	public static Connection connectToDatabase() throws Exception {
		Class.forName("oracle.jdbc.driver.OracleDriver");
		Connection connection = DriverManager.getConnection(jdbcURL, user,
				password);
		return connection;
	}

	public String setKeywordImportance(String[] keywords) {
		String responseString = null;
		for (String value : keywords) {
			System.out.println("The value is " + value);
		}
		responseString = "{\"success\": \"true\"}";
		return responseString;
	}
}