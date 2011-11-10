package website.web;

import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FolderByPeriodServlet extends HttpServlet {

	@Override
	@SuppressWarnings("rawtypes")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		String responseString = "abc";
		try {
			String folderName = request.getParameter("folderName");
			String password = request.getParameter("password");
			String userName = request.getParameter("userName");
			
			PrintWriter pw = response.getWriter();		

			HashMap<String, Integer> groupedMailCount = accessGmail(folderName, userName, password);
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("{\"" + folderName +  "\": [");
			Iterator iterator = groupedMailCount.entrySet().iterator();

			while(iterator.hasNext()) {
				
				Map.Entry element = (Map.Entry) iterator.next();
				stringBuilder.append("{\"Period\":\"" + element.getKey() + "\"," + "\"folderSize\":\"" + element.getValue() + "\"},");
			}
			
			if(!groupedMailCount.isEmpty()) {
				responseString = stringBuilder.substring(0, stringBuilder.length() -1);
			}			
			else {
				responseString = stringBuilder.toString();
			}
			responseString = responseString + "],\"success\": \"true\"}";			
			pw.write(responseString);
		}
		catch(Exception e) {
			try {
				PrintWriter pw = response.getWriter();
				for(StackTraceElement s: e.getStackTrace()) {
					pw.write(s.toString());
					pw.write("\n");
				}				
			} 
			catch (Exception e1) {
				// TODO Auto-generated catch block
				
			}
		}		
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
		doGet(request, response);
	}
	
	public HashMap<String, Integer> accessGmail(String folderName, String userName, String password) throws Exception {
		Properties props = System.getProperties();
		props.setProperty("mail.store.protocol", "imaps");
		Session session = Session.getDefaultInstance(props, null);
		
		Store store = session.getStore("imaps");
		store.connect("imap.gmail.com", userName, password);
		
		Folder folder = store.getFolder(folderName);
		HashMap<String, Integer> hashMap = new HashMap<String, Integer> ();
		
		if(folder.exists()) {
			hashMap = getMap(folder);
		}
		else {
			Folder parentFolder = store.getFolder("[Gmail]");
			Folder[] parentFolderList = parentFolder.list();
			
			for(Folder childFolder : parentFolderList) {
				if(childFolder.getName() == folderName) {
					hashMap = getMap(childFolder);
					break;
				}
			}
		}
		store.close();
		return hashMap;
	}
	
	public HashMap<String, Integer> getMap(Folder folder) throws Exception {
		folder.open(1);
		Message[] messages = folder.getMessages();
		
		HashMap<String, Integer> hashMap = new HashMap<String, Integer> ();

		Calendar calendar = Calendar.getInstance();
		Date today = calendar.getTime();
		for(Message message: messages) {
			Date messageReceivedDate = message.getReceivedDate();
			
			// Today
			if(((today.getDate() - messageReceivedDate.getDate())/ (1000*60*60*24)) <= 1) {
				if(hashMap.containsKey("Today")) {
					hashMap.put("Today", ((Integer)hashMap.get("Today")) + 1);					
				}
				else {
					hashMap.put("Today", 0);
				}	
			}
			
			//Yesterday
			else if(((today.getDate() - messageReceivedDate.getDate())/ (1000*60*60*24)) <= 2) {
				if(hashMap.containsKey("Yesterday")) {
					hashMap.put("Yesterday", ((Integer)hashMap.get("Yesterday")) + 1);
					
				}
				else {
					hashMap.put("Yesterday", 0);
				}	
			}
			//Last week
			else if(((today.getDate() - messageReceivedDate.getDate())/ (1000*60*60*24)) <= 7) {
				if(hashMap.containsKey("Last Week")) {
					hashMap.put("Last Week", ((Integer)hashMap.get("Last Week")) + 1);
					
				}
				else {
					hashMap.put("Last Week", 0);
				}	
			}
			//This month
			else if(today.getMonth() == messageReceivedDate.getMonth()) {
				if(hashMap.containsKey("This Month")) {
					hashMap.put("This Month", ((Integer)hashMap.get("This Month")) + 1);
					
				}
				else {
					hashMap.put("This Month", 0);
				}	
			}
			//Last month
			else if(today.getMonth() - messageReceivedDate.getMonth() == 1) {
				if(hashMap.containsKey("Last Month")) {
					hashMap.put("Last Month", ((Integer)hashMap.get("Last Month")) + 1);
					
				}
				else {
					hashMap.put("Last Month", 0);
				}	
			}
		}
		return hashMap;		
	}
}