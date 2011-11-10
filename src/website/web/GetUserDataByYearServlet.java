package website.web;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class GetUserDataByYearServlet extends HttpServlet {

	private static final String jdbcURL = "jdbc:oracle:thin:@ora.csc.ncsu.edu:1523:orcl";
	private static final String user = "gapandit";
	private static final String password = "001000715";
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter pw = null;
		String finduser = request.getParameter("emailid");
		System.out.println("emailid:"+finduser);
		HttpSession session = request.getSession(true);
		String param = (String) session.getAttribute("email_id");
		if(param!=null){
			session.setAttribute("email_id", new String(finduser));
            param = (String) session.getAttribute("email_id");
		}else {
			param = finduser;
            session.setAttribute("email_id", param);
        }
		try {
			pw = response.getWriter();
			pw.write(getMessageByYear(finduser));
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
	public static String getMessageByYear(String finduser){
		//<1990,resultset><1991,Resultset>
		HashMap<String,String> collectByYear =new HashMap<String,String>();
		HashMap<String,String> collectByYearrec =new HashMap<String,String>();
         String ResultString="";
				
		System.out.println("getMessageByYear");
		Connection connection;
		try {
			connection = connectToDatabase();
			Statement tofinduserstmt1 = connection.createStatement();
			Statement tofinduserstmt2 = connection.createStatement();
			Statement fromfinduserstmt1=connection.createStatement();
			Statement fromfinduserstmt2=connection.createStatement();
			Statement yearstmt=connection.createStatement();

			//from= me to = finduser
			ResultSet tofinduserSet1 = tofinduserstmt1.executeQuery("SELECT EXTRACT(YEAR FROM SENDDATE)AS YEAR, COUNT(*) AS SENTCOUNT " +
																	"FROM MAIL " +
																	"WHERE ((MESSAGEID LIKE'Sent Mail%') AND (RECEIVERNAME LIKE '%"+finduser+"%'))" +
																	"GROUP BY EXTRACT(YEAR FROM SENDDATE)");
			ResultSet tofinduserSet2 = tofinduserstmt2.executeQuery("SELECT EXTRACT(YEAR FROM SENDDATE)AS YEAR, COUNT(*) AS SENTCOUNT " +
																	"FROM REPLYMAIL " +
																	"WHERE ((MESSAGEID LIKE'Sent Mail%') AND (RECEIVERNAME LIKE '%"+finduser+"%'))" +
																	"GROUP BY EXTRACT(YEAR FROM SENDDATE)");
			System.out.println("sent ::set1");
			while(tofinduserSet1.next()){
				collectByYear.put(tofinduserSet1.getString("YEAR"), tofinduserSet1.getString("SENTCOUNT"));
				System.out.println("year1 : 2011 value: "+collectByYear.get("2011"));
			}
			System.out.println("sent ::set2");
			while(tofinduserSet2.next())
			{
				if(collectByYear.containsKey(tofinduserSet2.getString("YEAR"))){
					System.out.println("IF contains");
					String temp=collectByYear.get(tofinduserSet2.getString("YEAR"));
				    int temp1= Integer.parseInt(temp);
				    int temp2=Integer.parseInt(tofinduserSet2.getString("SENTCOUNT"));
				    int temp3=temp1+temp2;
				    collectByYear.remove(tofinduserSet2.getString("YEAR"));
				    collectByYear.put(tofinduserSet2.getString("YEAR"),Integer.toString(temp3));
					System.out.println("year2 : 2011 value: "+collectByYear.get("2011"));
				}
				else{
					System.out.println("ELSE contains");
					collectByYear.put(tofinduserSet2.getString("YEAR"), tofinduserSet2.getString("SENTCOUNT"));
					System.out.println("year : 2011 value: "+collectByYear.get("2011"));
				}
			}						
			
			tofinduserSet1.close();
			tofinduserSet2.close();

			//from = finduser to=me
			
			ResultSet fromfinduserSet1 = fromfinduserstmt1.executeQuery("SELECT EXTRACT(YEAR FROM RECEIVEDATE)AS YEAR, COUNT(*) AS RECEIVECOUNT " +
																		"FROM MAIL " +
																		"WHERE ((MESSAGEID LIKE 'INBOX%') AND (SENDERNAME LIKE '%"+finduser+"%'))" +
																		"GROUP BY EXTRACT(YEAR FROM RECEIVEDATE)");
			ResultSet fromfinduserSet2 = fromfinduserstmt2.executeQuery("SELECT EXTRACT(YEAR FROM RECEIVEDATE)AS YEAR, COUNT(*) AS RECEIVECOUNT " +
																		"FROM REPLYMAIL " +
																		"WHERE ((MESSAGEID LIKE 'INBOX%') AND (SENDERNAME LIKE '%"+finduser+"%'))" +
																		"GROUP BY EXTRACT(YEAR FROM RECEIVEDATE)");
			System.out.println("received:set1");
			while(fromfinduserSet1.next()){
				//System.out.println("year : "+ fromfinduserSet1.getString("YEAR") +" receivecount: "+ fromfinduserSet1.getString("RECEIVECOUNT"));
				collectByYearrec.put(fromfinduserSet1.getString("YEAR"),fromfinduserSet1.getString("RECEIVECOUNT"));
				System.out.println("year3 : 2011 value: "+collectByYearrec.get("2011"));
			}
			System.out.println("received:set2");
			while(fromfinduserSet2.next())
			{
				if(collectByYearrec.containsKey(fromfinduserSet2.getString("YEAR"))){
					String temp=collectByYearrec.get(fromfinduserSet2.getString("YEAR"));
				    int temp1= Integer.parseInt(temp);
				    int temp2=Integer.parseInt(fromfinduserSet2.getString("RECEIVECOUNT"));
				    int temp3=temp1+temp2;
				    //System.out.println("year : "+ fromfinduserSet2.getString("YEAR") +" receivecount: "+ Integer.toString(temp3));
				    collectByYearrec.remove(fromfinduserSet2.getString("YEAR"));
				    collectByYearrec.put(fromfinduserSet2.getString("YEAR"),Integer.toString(temp3));
					System.out.println("year4 : 2011 value: "+collectByYearrec.get("2011"));
				}
				else{
					//System.out.println("year : "+ fromfinduserSet2.getString("YEAR") +" receivecount: "+ fromfinduserSet2.getString("SENTCOUNT"));
					collectByYearrec.put(fromfinduserSet2.getString("YEAR"), fromfinduserSet2.getString("RECEIVECOUNT"));
					System.out.println("year5 : 2011 value: "+collectByYearrec.get("2011"));
				}
			}					
			//groupByMailType.put("RECEIVED",collectByYearrec);//received
				
			fromfinduserSet1.close();
			fromfinduserSet2.close();
			
			
			ResultSet yearset= yearstmt.executeQuery("SELECT DISTINCT(EXTRACT(YEAR FROM SENDDATE))AS YEAR FROM MAIL WHERE ((MESSAGEID LIKE'Sent Mail%') AND (RECEIVERNAME LIKE '%"+finduser+"%')) " +
													"UNION SELECT DISTINCT(EXTRACT(YEAR FROM SENDDATE))AS YEAR FROM REPLYMAIL WHERE ((MESSAGEID LIKE'Sent Mail%') AND (RECEIVERNAME LIKE '%"+finduser+"%')) " +
													"UNION SELECT DISTINCT(EXTRACT(YEAR FROM RECEIVEDATE)) AS YEAR FROM MAIL WHERE ((MESSAGEID LIKE 'INBOX%') AND (SENDERNAME LIKE '%"+finduser+"%')) " +
												    "UNION SELECT DISTINCT(EXTRACT(YEAR FROM RECEIVEDATE)) AS YEAR FROM REPLYMAIL WHERE ((MESSAGEID LIKE 'INBOX%') AND (SENDERNAME LIKE '%"+finduser+"%'))");
			
			ResultString="{\"UserYearlyData\":[";
			while(yearset.next()){
				if(collectByYear.containsKey(yearset.getString("YEAR")) && collectByYearrec.containsKey(yearset.getString("YEAR"))){
					
					ResultString+="{\"Year\":\""+yearset.getString("YEAR") + "\"" + ",\"Sent\":\""+collectByYear.get(yearset.getString("YEAR"))+ "\"," + "\"Received\":\""+collectByYearrec.get(yearset.getString("YEAR")) + "\"},";
				}
				else if(collectByYear.containsKey(yearset.getString("YEAR")) && !(collectByYearrec.containsKey(yearset.getString("YEAR")))){
					
					ResultString+="{\"Year\":\""+yearset.getString("YEAR")+ "\"" + ",\"Sent\":\""+collectByYear.get(yearset.getString("YEAR"))+ "\"," + "\"Received\":\"0\"},";
				}
				else if(!(collectByYear.containsKey(yearset.getString("YEAR"))) && collectByYearrec.containsKey(yearset.getString("YEAR"))){
					
					ResultString+="{\"Year\":\""+yearset.getString("YEAR") + "\"" + ",\"Sent\":\"0\"" + "," + "\"Received\":\""+collectByYearrec.get(yearset.getString("YEAR")) + "\"},";
				}
			}
			
			ResultString = ResultString.substring(0, ResultString.length() -1);
			ResultString+="],\"success\": \"true\"}";
			System.out.println("finish");
			connection.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ResultString;
	}
}