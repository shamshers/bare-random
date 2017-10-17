/**
 * 
 */
package com.infinityfw.wrappers;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.jongo.Jongo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.gson.Gson;
import com.infinityfw.driver.Controller;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.relevantcodes.extentreports.LogStatus;

/**
 * @author Binny
 *
 */
public class Connectivity extends WebObject {

	public int findOne(String ipAddress, String port, String dbName,String collectionName, String param1) {
		int result = 1;
		int port1 = 0;
		
		String strMethod = Controller.strKeywordName;

		// values empty or not

		if ((port == null) || port.trim().equalsIgnoreCase("")
				|| (ipAddress == null) || ipAddress.trim().equalsIgnoreCase("")
				|| (collectionName == null)
				|| collectionName.trim().equalsIgnoreCase("")
				|| (dbName == null) || dbName.trim().equalsIgnoreCase("")) {
			REPORT.log(
					LogStatus.FAIL,
					"Action: "
							+ strMethod
							+ "    "
							+ "Status : Failed \t\t"
							+ "Message : The values passed in method are either null/empty , please verify.");
			return result;
		}

		port1 = Integer.parseInt(port);
		System.out.println(port1);
		MongoClient mongoClient = null;
		boolean flag = false;

		// connection created or not

		try {
			System.out.println("in try");
			boolean connect = mongoRunningAt(ipAddress, port1);
			System.out.println(connect);

			if (connect) {
				mongoClient = new MongoClient(ipAddress, port1);
			}
		} catch (MongoException e) {
			System.out.println("in catch");
			result = 1;
			REPORT.log(
					LogStatus.FAIL,
					"Action: "
							+ strMethod
							+ "    "
							+ "Status : Failed \t\t"
							+ "Message : The values passed in method are either null/empty , please verify.");
			return result;
		}
		catch (Exception e) {
			System.out.println("in 2nd catch");
			result = 1;
			REPORT.log(
					LogStatus.FAIL,
					"Action: "
							+ strMethod
							+ "    "
							+ "Status : Failed \t\t"
							+ "Message : The values passed in method are either null/empty , please verify.");
			return result;
		}
		List<String> dbNameList = mongoClient.getDatabaseNames();
		for (String db : dbNameList) {
			if (db.matches(dbName))

			{
				System.out.print("db exists");
				flag = true;
			}
		}
		if (flag) {
			DB mongoDB = mongoClient.getDB(dbName);
			Jongo jongo = new Jongo(mongoDB);
			// collection exists or not
			if (jongo.getDatabase().collectionExists(collectionName)) {
				Object iterator3 = jongo.getCollection(collectionName)
						.findOne().as(Object.class);
				String jsonString = new Gson().toJson(iterator3, Map.class);
				try {
					org.json.JSONObject js = new org.json.JSONObject(jsonString);
					System.out.println(js);
					String[] tokens = null;
					int count = 0;
					//	System.out.println("hiiiii");
					//	System.out.println(param1);
					//	System.out.println(param1.contains("."));
					if (param1.contains(".")) {
						//		System.out.println("param has . in it");
						tokens = param1.split("\\.");
						count = tokens.length;
						//			System.out.println(tokens.length);
						if (js.get(tokens[0]) instanceof JSONObject) {
							jsonob(param1, count, js, 0);
						}

						if (js.get(tokens[0]) instanceof JSONArray) {
							//					System.out.println("in array");
							jsonarr(param1, js);
						}
					}
					if (param1 != null) {
						if (js.get(param1) instanceof JSONArray) {
							jsonarr(param1, js);
						}

						if (js.get(param1) instanceof Integer) {
							System.out.println("In integer");
							System.out.println(js.getInt(param1));
						}

						if (js.get(param1) instanceof Long) {
							System.out.println("In Long");
							System.out.println(js.getLong(param1));
						}
						if (js.get(param1) instanceof String) {
							System.out.println(js.getString(param1));
						}
						if (js.get(param1) instanceof Boolean) {
							System.out.println(js.getBoolean(param1));
						}
					}
					result = 0;
				} catch (JSONException e) {
					result = 1;
					REPORT.log(LogStatus.FAIL, "Action: " + strMethod + "    "
							+ "Status : Failed \t\t"
							+ "Message :  does not Exist.");
					return result;
				}
			} else {
				result = 1;
				REPORT.log(LogStatus.FAIL, "Action: " + strMethod + "    "
						+ "Status : Failed \t\t"
						+ "Message : collection does not Exist.");
				return result;
			}
		} else {
			result = 1;
			REPORT.log(LogStatus.FAIL, "Action: " + strMethod + "    "
					+ "Status : Failed \t\t"
					+ "Message : Database does not Exist.");
			return result;
		}
		return result;
	}

	public void jsonob(String param1, int count, org.json.JSONObject js, int n)throws JSONException {

		//	System.out.println("in jsonobject function");
		// System.out.println(param1 + js);
		String[] tokens = null;
		String token = null;

		if (param1.contains(".")) {
			tokens = param1.split("\\.", 2);
			System.out.println(tokens[0] + tokens[1]);
			count = tokens.length;
		} else {
			token = param1;
		}
		if (count > 1) {
			//			System.out.println("in iff of jsonob");
			if (js.get(tokens[0]) instanceof JSONObject) {
				//				System.out.println("in jsonobject");
				js = js.getJSONObject(tokens[0]);
				param1 = tokens[1];
				jsonob(param1, 0, js, 0);
			}
			if (js.get(tokens[0]) instanceof JSONArray) {
				//			System.out.println("jsonarray of jsonaob");

				js = js.getJSONArray(tokens[0]).getJSONObject(0);
				// count = count - 1;
				// n = n + 1;
				param1 = tokens[1];
				jsonarr(param1, js);
			}
		}
		if (js.get(token) instanceof Integer) {
			System.out.println(js.getInt(token));
		}
		if (js.get(token) instanceof Long) {
			System.out.println(js.getLong(token));
		}
		if (js.get(token) instanceof String) {
			System.out.println(js.getString(token));
		}
		if (js.get(token) instanceof Boolean) {
			System.out.println(js.getBoolean(token));
		}
	}

	public void jsonarr(String param1, org.json.JSONObject js)
			throws JSONException {

		//		System.out.println(js + param1);
		String[] tokens = null;
		String[] token2 = null;
		String token = null;
		int count1 = 0;

		System.out.println(param1.contains("."));

		if (param1.contains(".")) {
			//	System.out.println("in if");
			tokens = param1.split("\\.", 2);
			// System.out.println(tokens[0] + tokens[1]);

		} else {
			token = param1;
			System.out.println(token);
			if (js.get(token) instanceof JSONArray) {
				int len = js.getJSONArray(token).length();
				for (int i = 0; i < len; i++) {
					System.out.println(js.getJSONArray(token).get(i));
				}
			}
			if (js.get(token) instanceof Integer) {
				System.out.println(js.getInt(token));
			}
			if (js.get(token) instanceof Long) {
				System.out.println(js.getLong(token));
			}
			if (js.get(token) instanceof String) {
				System.out.println(js.getString(token));
			}
			if (js.get(token) instanceof Boolean) {
				System.out.println(js.getBoolean(token));
			}
		}
		boolean hasKey = false;
		boolean hasKey1 = false;
		// System.out.println("hiiii" + js.get(tokens[0]).getClass());
		if (js.get(tokens[0]) instanceof JSONObject) {
			//	System.out.println("in if jsonobject");
			jsonob(tokens[1], count1, js.getJSONObject(tokens[0]), 0);
		}
		if (js.get(tokens[0]) instanceof JSONArray) {
			//		System.out.println("in iffffff");
			int m = 0, k = 0;
			// int len = js.getJSONArray(tokens[n]).getJSONObject(0).length();
			@SuppressWarnings("unchecked")
			Iterator<String> ls = js.getJSONArray(tokens[0]).getJSONObject(0)
			.keys();
			String keymatch = null;
			while (ls.hasNext()) {
				String key = ls.next();
				System.out.println(key);
				// System.out.println(tokens[1]);
				if (tokens[1].contains(".")) {
					//		System.out.println("in if");
					token2 = tokens[1].split("\\.", 2);
					//			System.out.println(token2[0] + token2[1]);
					if (key.matches(token2[0])) {
						hasKey = true;
						m = k;
					}
				}
				else {
					if (key.matches(tokens[1])) {
						hasKey1 = true;
						m = k;
						keymatch = key;
					}
				}
				k++;
			}

			//		System.out.println("hasKey1" + hasKey1);
			//	System.out.println("hasKey" + hasKey);
			// System.out.println(js.getJSONArray(tokens[0]).length());

			//	System.out.println(js.getJSONArray(tokens[0]).getJSONObject(0)
			//		.get("url"));
			if (hasKey) {
				if (js.getJSONArray(tokens[0]).get(m) instanceof JSONObject) {
					param1 = tokens[1];
					//	System.out.println(param1);
					//System.out.println(js.getJSONArray(tokens[0])
					//	.getJSONObject(m));
					JSONObject jsonob = js.getJSONArray(tokens[0])
							.getJSONObject(m);
					jsonob(param1, 0, jsonob, 0);
				}
			}
			if (hasKey1) {
				if (js.getJSONArray(tokens[0]).getJSONObject(0).get(keymatch) instanceof String) {

					System.out.println(js.getJSONArray(tokens[0])
							.getJSONObject(m).getString(keymatch));
				}
			}
		}
	}

	public boolean mongoRunningAt(String ipAddress, int port1) {
		
		String strMethod = Controller.strKeywordName;

		try {
			MongoClient mongo = new MongoClient(ipAddress, port1);
			try {
				Socket socket = mongo.getMongoOptions().socketFactory
						.createSocket();
				socket.connect(mongo.getAddress().getSocketAddress());
				socket.close();
			} catch (IOException ex) {

				REPORT.log(
						LogStatus.FAIL,
						"Action: "
								+ strMethod
								+ "    "
								+ "Status : Failed \t\t"
								+ "Message : Connection cannot be established, either port number or ip Address is wrong , please verify.");
				return false;
			}
			mongo.close();
			return true;
		} catch (UnknownHostException e) {
			REPORT.log(
					LogStatus.FAIL,
					"Action: "
							+ strMethod
							+ "    "
							+ "Status : Failed \t\t"
							+ "Message : Connection cannot be established, either port number or ip Address is wrong , please verify.");
			return false;
		} catch (Exception e) {
			REPORT.log(
					LogStatus.FAIL,
					"Action: "
							+ strMethod
							+ "    "
							+ "Status : Failed \t\t"
							+ "Message : Connection cannot be established, either port number or ip Address is wrong , please verify.");
			return false;
		}

	}
}