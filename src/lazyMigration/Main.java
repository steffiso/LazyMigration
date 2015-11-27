package lazyMigration;

import java.util.ArrayList;
import java.util.List;

public class Main {

	public static void main(String[] args) {		
		
	    List<Player> playerList = new ArrayList<Player>();
	    playerList.add(new Player(1,"Lisa", 20));
	    playerList.add(new Player(2,"Bart", 10));
	    playerList.add(new Player(1,"Lisa", 10));

		EDB testEDB = new EDB();
		testEDB.putPlayer(playerList);
		
		List<Player> playerList2 = new ArrayList<Player>();
		playerList2.add(new Player(3,"Homer", 30));
	
		testEDB.putPlayer(playerList2);
		
		//wieder auslesen aus json-Datei
		List<Player> playerList3 = testEDB.getAllPlayer("data/player.json");
		
		for(int i = 0; i< playerList3.size(); i++)
		{
			System.out.println(playerList3.get(i).ToString());
		}

	}
}
