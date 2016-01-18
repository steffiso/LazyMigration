package database;

import java.util.ArrayList;

public class Entity {
	
	private String kind;
	private int schemaversion;
	private ArrayList<String> values;
	
	public Entity(){
		
	}
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
	public int getSchemaversion() {
		return schemaversion;
	}
	public void setSchemaversion(int schemaversion) {
		this.schemaversion = schemaversion;
	}
	public ArrayList<String> getValues() {
		return values;
	}
	public void setValues(ArrayList<String> values) {
		this.values = values;
	}
	
	public String toString(){
		String entity = kind + Integer.toString(schemaversion) + "(";
		for (String s : values){
			try{
				int value = Integer.parseInt(s);
			}catch ( NumberFormatException e){
				s = "'" + s + "'";
			}
			entity = entity + s + ",";
		}
		if (entity.endsWith(",")) entity = entity.substring(0, entity.length()-1);
		entity = entity + ").\n";
		return entity;
	}
}
