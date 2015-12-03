package lazyMigration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EDB {

	String filename = "data/player.json";
	private List <Player> playerList = new ArrayList<Player>();
	
		public void putPlayer(Player player){
			File file = new File(filename);
			if(file.exists()) {
				playerList = getAllPlayer(filename);
			}
			
			playerList.add(player);
			JsonConvert javaToJson = new JsonConvert();
			javaToJson.objectsToJson(playerList, filename);		
		}
		
		public void putPlayer(List<Player> players){
			File file = new File(filename);
			if(file.exists()) {
				playerList = getAllPlayer(filename);
			}
			for (int i = 0; i<players.size();i++){
				playerList.add(players.get(i));
			}
			JsonConvert javaToJson = new JsonConvert();
			javaToJson.objectsToJson(playerList, filename);
		}
				
		public Player getPlayer(int id){
			//dummy entry

			Player player = null;
			return player;
		}
		
		public List<Player> getAllPlayer(String filename){
			ObjectMapper mapper = new ObjectMapper();
			
			try {
				// Convert JSON string from file to Object
				playerList = mapper.readValue(new File(filename), new TypeReference<List<Player>>(){});
				
			} catch (JsonGenerationException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			//JsonToObject jsonToJava = new JsonToObject(filename);
			//List<Player> players = jsonToJava.convert();
			return playerList;
		}
		
		public String getSchema(int timestamp)
		  {	  
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootArray;
				String schema = "";
				
				try {
					rootArray = mapper.readTree(new File("data/Players.json"));
				
					for(JsonNode root : rootArray){
						
						int ts;
						String attribute = null;
						// Get timestamp
						ts = root.path("ts").asInt();
						if (timestamp == ts){
							for ( Iterator<String> names = root.fieldNames(); names.hasNext(); ){
								attribute = names.next();
								if (!attribute.equals("ts")) schema = schema + "?" + attribute + "\n";
							}
							return schema;
						}
						
					}
					return schema;
				
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return schema;		
		  }
		
}
