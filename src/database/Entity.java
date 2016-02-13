package database;

import java.util.ArrayList;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Entity {
	
	@JsonProperty("kind")
	private String kind;
	@JsonProperty("schemaversion")
	private int schemaversion;
	@JsonProperty("attributes")
	SortedMap<String,String> attributes;
	@JsonProperty("ts")
	int timestamp;

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
	
	public SortedMap<String, String> getAttributes(){
		return attributes;
	}
	
	public void setAttributes(SortedMap<String, String> attributes){
		this.attributes = attributes;
	}
	
	public int getTimestamp(){
		return this.timestamp;
	}
	
	public String toString(){
		String entity = kind + Integer.toString(schemaversion) + "(";
		String temp;
		for (Map.Entry<String, String> attributeEntry : attributes.entrySet()){
			try{
				int value = Integer.parseInt(attributeEntry.getValue());
				entity = entity + value + ",";
			}catch ( NumberFormatException e){
				temp = "'" + attributeEntry.getValue() + "'";
				entity = entity + temp + ",";
			}
		}
		if (entity.endsWith(",")) entity = entity.substring(0, entity.length()-1);
		entity = entity + ").\n";
		return entity;

	}
}