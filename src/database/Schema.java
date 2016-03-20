package database;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Schema {

	@JsonProperty("kind")
	private String kind;
	@JsonProperty("schemaversion")
	private int schemaversion;
	@JsonProperty("attributes")
	private ArrayList<String> attributes;
	@JsonProperty("ts")
	private int timestamp;
	
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
	
	public ArrayList<String> getAttributes() {
		return attributes;
	}
	
	public void setValues(ArrayList<String> attributes){
		this.attributes = attributes;
	}
	
	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}
	
	public int getTimestamp() {
		return timestamp;
	}
	
	public String toString(){
		String schema = "";
		for (int i = 0; i < attributes.size();i++){
			schema = schema + attributes.get(i) + ",";
		}
		schema = schema.substring(0, schema.length() -1);
		return schema;
	}
}
