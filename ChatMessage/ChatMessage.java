package ChatMessage;

import java.io.Serializable;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ChatMessage implements Serializable{
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JSONObject obj = new JSONObject();
		
	@SuppressWarnings("unchecked")
	public ChatMessage(String command, String parameters, String sender){
		obj.put("command", command);
		obj.put("parameters", parameters);
		obj.put("sender", sender);
		obj.put("timestamp", System.currentTimeMillis());
	}
	
	public ChatMessage(JSONObject m_obj){
		obj = m_obj;
	}

	public String getCommand(){
		return (String)obj.get("command");	
	}
	
	public String getParameters(){
		return (String)obj.get("parameters");	
	}
	
	public String getSender(){
		return (String)obj.get("sender");
	}
	
	public String getTimeStamp(){
		return obj.get("timestamp").toString();
	}
	
	public String getJsonString(){
		return obj.toJSONString();
	}
	
	static public JSONObject getJsonObject(String JsonString){
		JSONParser parser = new JSONParser();
		JSONObject obj = new JSONObject();
		try {
			obj = (JSONObject)parser.parse(JsonString);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return obj;
	}
	
}
