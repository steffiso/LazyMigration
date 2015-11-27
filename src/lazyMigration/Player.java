package lazyMigration;

public class Player {

		private String name;
		private int id;
		private int score;
		private int timestamp;
		
		public Player(int id, String name, int score) {
			this.name = name;
			this.id = id;
			this.score = score;	
			this.timestamp = 1;
		}
		
		//dummy constructor für den json mapper
		public Player(){
			
		}
		
		public int getScore() {
			return score;
		}
		public void setScore(int score) {
			this.score = score;
		}
		public int getTimestamp() {
			return timestamp;
		}
		public void setTimestamp(int timestamp) {
			this.timestamp = timestamp;
		}
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
		public String ToString(){
			return "Id: " + this.id + " Name: " + this.name + " Score: " + this.score + " Timestamp: " + this.timestamp + "\n";
			
		}

	

}
