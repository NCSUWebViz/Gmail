package website.web;

import java.io.PrintWriter;
import java.util.Properties;

import javax.mail.Session;
import javax.mail.Store;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginServlet extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		Store store;
		PrintWriter pw = null;
		try {
			pw = response.getWriter();
			
			String userName = request.getParameter("loginUsername");
			String password = request.getParameter("loginPassword");
		
			System.out.println("Access Gmail");
			Properties props = System.getProperties();
			props.setProperty("mail.store.protocol", "imaps");
			
			Session session = Session.getDefaultInstance(props, null);
			
			store = session.getStore("imaps");
			store.connect("imap.gmail.com", userName, password);
			store.close();
			
			HttpSession httpsession = request.getSession(true);
			httpsession.setAttribute("email_id", userName);
			httpsession.setAttribute("password", password);
			
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
