package lazyMigration;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonConvert {

	List<Player> playerList;
	
		public List<Player> jsonToObjects(String filename){
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
			
			return playerList;
		}
		
		public void objectsToJson(List<Player> players, String filename){
			ObjectMapper mapper = new ObjectMapper();			
			
			File f = new File(filename);
			f.getParentFile().mkdirs(); 
			//Create new file if it doesn't exist
			if(!f.exists()) {
					try {
						f.createNewFile();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}		
			} 		
			
			try {
				mapper.writeValue(new File(filename), players);
			} catch (JsonGenerationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}

}
