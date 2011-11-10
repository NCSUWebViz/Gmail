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

public class GetUserDataByMonthServlet extends HttpServlet {

	private static final String jdbcURL = "jdbc:oracle:thin:@ora.csc.ncsu.edu:1523:orcl";
	private static final String user = "gapandit";
	private static final String password = "001000715";
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter pw = null;
		HttpSession session = request.getSession(true);
		String finduser = (String) session.getAttribute("email_id");
		System.out.println("emailid:"+finduser);
//		String finduser = 
		String year = request.getParameter("year");
		System.out.println("year:"+finduser);
		try {
			pw = response.getWriter();
			pw.write(getMessageByMonth(finduser,year));
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
	//get data by months
	public static String getMessageByMonth(String finduser, String year){
		
		int monthcount;
		String ResultString="";
		HashMap<String,String> collectByMonthSend =new HashMap<String,String>();
		HashMap<String,String> collectByMonthReceive =new HashMap<String,String>();
		System.out.println("getMessageByMonth");
		Connection connection;
		try {
			connection = connectToDatabase();
			Statement tofinduserstmt1 = connection.createStatement();
			Statement tofinduserstmt2 = connection.createStatement();
			Statement tofinduserstmt3 = connection.createStatement();
			Statement tofinduserstmt4 = connection.createStatement();
			ResultSet SendResultset1 = tofinduserstmt1.executeQuery("SELECT EXTRACT(MONTH FROM SENDDATE)AS MONTH, COUNT(*) AS SENTCOUNT " +
																   "FROM MAIL " +
																   "WHERE ((MESSAGEID LIKE'Sent Mail%') AND (RECEIVERNAME LIKE '%"+finduser+"%') AND (EXTRACT(YEAR FROM SENDDATE) LIKE '"+year+"'))" +
																   "GROUP BY EXTRACT(MONTH FROM SENDDATE)");
			ResultSet SendResultset2 = tofinduserstmt2.executeQuery("SELECT EXTRACT(MONTH FROM SENDDATE)AS MONTH, COUNT(*) AS SENTCOUNT " +
																   "FROM REPLYMAIL " +
																   "WHERE ((MESSAGEID LIKE'Sent Mail%') AND (RECEIVERNAME LIKE '%"+finduser+"%') AND (EXTRACT(YEAR FROM SENDDATE) LIKE '"+year+"'))" +
																   "GROUP BY EXTRACT(MONTH FROM SENDDATE)");
			System.out.println("sent ::set1");
			while(SendResultset1.next()){
				collectByMonthSend.put(SendResultset1.getString("MONTH"), SendResultset1.getString("SENTCOUNT"));
				System.out.println("month1 :11 value: "+collectByMonthSend.get("11"));
			}
			System.out.println("sent ::set2");
			while(SendResultset2.next())
			{
				if(collectByMonthSend.containsKey(SendResultset2.getString("MONTH"))){
					System.out.println("IF contains");
					String temp=collectByMonthSend.get(SendResultset2.getString("MONTH"));
				    int temp1= Integer.parseInt(temp);
				    int temp2=Integer.parseInt(SendResultset2.getString("SENTCOUNT"));
				    int temp3=temp1+temp2;
				    collectByMonthSend.remove(SendResultset2.getString("MONTH"));
				    collectByMonthSend.put(SendResultset2.getString("MONTH"),Integer.toString(temp3));
					System.out.println("month2 :11 value: "+collectByMonthSend.get("11"));
				}
				else{
					System.out.println("ELSE contains");
					collectByMonthSend.put(SendResultset2.getString("MONTH"), SendResultset2.getString("SENTCOUNT"));
					System.out.println("month3 : 11 value: "+collectByMonthSend.get("11"));
				}
			}						
			SendResultset1.close();
			SendResultset2.close();
			
				ResultSet ReceiveResultset1 = tofinduserstmt3.executeQuery("SELECT EXTRACT(MONTH FROM RECEIVEDATE)AS MONTH, COUNT(*) AS RECEIVECOUNT " +
																		  "FROM MAIL " +
																		  "WHERE ((MESSAGEID LIKE 'INBOX%') AND (SENDERNAME LIKE '%"+finduser+"%') AND (EXTRACT(YEAR FROM RECEIVEDATE) LIKE '"+year+"'))" +
																		  "GROUP BY EXTRACT (MONTH FROM RECEIVEDATE)");
				
			   ResultSet ReceiveResultset2 = tofinduserstmt4.executeQuery("SELECT EXTRACT(MONTH FROM RECEIVEDATE)AS MONTH, COUNT(*) AS RECEIVECOUNT " +
																		 "FROM REPLYMAIL " +
																		 "WHERE ((MESSAGEID LIKE 'INBOX%') AND (SENDERNAME LIKE '%"+finduser+"%') AND (EXTRACT(YEAR FROM RECEIVEDATE) LIKE '"+year+"'))" +
					                                                   	"GROUP BY EXTRACT(MONTH FROM RECEIVEDATE)");
			   
			   System.out.println("received:set1");
				while(ReceiveResultset1.next()){
					//System.out.println("year : "+ fromfinduserSet1.getString("YEAR") +" receivecount: "+ fromfinduserSet1.getString("RECEIVECOUNT"));
					collectByMonthReceive.put(ReceiveResultset1.getString("MONTH"),ReceiveResultset1.getString("RECEIVECOUNT"));
					System.out.println("month3 :11 value: "+collectByMonthReceive.get("11"));
				}
				System.out.println("received:set2");
				while(ReceiveResultset2.next())
				{
					if(collectByMonthReceive.containsKey(ReceiveResultset2.getString("MONTH"))){
						String temp=collectByMonthReceive.get(ReceiveResultset2.getString("MONTH"));
					    int temp1= Integer.parseInt(temp);
					    int temp2=Integer.parseInt(ReceiveResultset2.getString("RECEIVECOUNT"));
					    int temp3=temp1+temp2;
					    //System.out.println("year : "+ fromfinduserSet2.getString("YEAR") +" receivecount: "+ Integer.toString(temp3));
					    collectByMonthReceive.remove(ReceiveResultset2.getString("MONTH"));
					    collectByMonthReceive.put(ReceiveResultset2.getString("MONTH"),Integer.toString(temp3));
						System.out.println("month4 :11 value: "+collectByMonthReceive.get("11"));
					}
					else{
						//System.out.println("year : "+ fromfinduserSet2.getString("YEAR") +" receivecount: "+ fromfinduserSet2.getString("SENTCOUNT"));
						collectByMonthReceive.put(ReceiveResultset2.getString("MONTH"), ReceiveResultset2.getString("RECEIVECOUNT"));
						System.out.println("month5 :11 value: "+collectByMonthReceive.get("11"));
					}
				}
				ReceiveResultset1.close();
				ReceiveResultset2.close();
				
			ResultString="{\"UserMonthlyData\":[";		
			for (monthcount=1;monthcount<13;monthcount++){
                 if(collectByMonthSend.containsKey(Integer.toString(monthcount)) && collectByMonthReceive.containsKey(Integer.toString(monthcount))){
					
					ResultString+="{\"Month\":\""+convert(Integer.toString(monthcount)) + "\"" + ",\"Sent\":\""+collectByMonthSend.get(Integer.toString(monthcount))+ "\"," + "\"Received\":\""+collectByMonthReceive.get(Integer.toString(monthcount)) + "\"},";
				}
				else if(collectByMonthSend.containsKey(Integer.toString(monthcount)) && !(collectByMonthReceive.containsKey(Integer.toString(monthcount)))){
					
					ResultString+="{\"Month\":\""+convert(Integer.toString(monthcount))+ "\"" + ",\"Sent\":\""+collectByMonthSend.get(Integer.toString(monthcount))+ "\"," + "\"Received\":\"0\"},";
				}
				else if(!(collectByMonthSend.containsKey(Integer.toString(monthcount))) && collectByMonthReceive.containsKey(Integer.toString(monthcount))){
					
					ResultString+="{\"Month\":\""+convert(Integer.toString(monthcount)) + "\"" + ",\"Sent\":\"0\"" + "," + "\"Received\":\""+collectByMonthReceive.get(Integer.toString(monthcount)) + "\"},";
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
	
	public static String convert(String num){
		int no=Integer.parseInt(num);
		switch(no){
		case 1 : return "JAN";
		case 2 : return "FEB";
		case 3 : return "MAR";
		case 4 : return "APR";
		case 5 : return "MAY";
		case 6 : return "JUN";
		case 7 : return "JUL";
		case 8 : return "AUG";
		case 9 : return "SEP";
		case 10 : return "OCT";
		case 11 : return "NOV";
		case 12 : return "DEC";
		default : return null;
		}
	}
}