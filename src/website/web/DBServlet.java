package website.web;

import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class DBServlet extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter pw = null;
		try {
			pw = response.getWriter();			
			HttpSession httpsession = request.getSession(true);
			httpsession.setAttribute("dbJDBC", request.getParameter("dbJDBC"));
			httpsession.setAttribute("dbUsername", request.getParameter("dbUsername"));
			httpsession.setAttribute("dbPassword", request.getParameter("dbPassword"));
			
			pw.write("{\"success\": \"true\"}");			
		}
		catch (Exception e) {
			e.printStackTrace();
			pw.write("{\"success\": \"false\"}");
		}
		finally {
			pw.close();
		}
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
		doGet(request, response);
	}
}