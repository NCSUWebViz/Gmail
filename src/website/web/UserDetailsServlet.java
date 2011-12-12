package website.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class UserDetailsServlet extends HttpServlet {
	@SuppressWarnings("null")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		HttpSession httpsession = request.getSession(true);
		String email = "";
		
		if(httpsession.getValue("email_id") != null) {
			email = httpsession.getValue("email_id").toString();
		}
		
		PrintWriter pw = null;
		try {
			pw = response.getWriter();
			pw.write("{\"Details\":{\"email\":\"" + email + "\",\"success\":\"true\"}}");
		} 
		catch (IOException e) {
			e.printStackTrace();
			pw.write("{\"Details\":{\"email\":\"\",\"success\":\"false\"}}");
		}
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
		doGet(request, response);
	}
}
