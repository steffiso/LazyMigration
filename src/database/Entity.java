package database;

import java.util.LinkedHashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Entity {

	@JsonProperty("kind")
	private String kind;
	@JsonProperty("schemaversion")
	private int schemaversion;
	@JsonProperty("attributes")
	private LinkedHashMap<String, Object> attributes;
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

	public LinkedHashMap<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(LinkedHashMap<String, Object> attributes) {
		this.attributes = attributes;
	}

	public int getTimestamp() {
		return this.timestamp;
	}

	public String toEDBString() {
		String entity = toString(true);
		return entity;
	}

	public String toJsonString() {
		String entity = toString(false);
		return entity;

	}

	public String toString(boolean isEDB) {
		String entity = "";
		if (isEDB)
			entity = kind + Integer.toString(schemaversion) + "(";
		else
			entity = kind + Integer.toString(schemaversion) + "{";
		String temp;
		for (Map.Entry<String, Object> attributeEntry : attributes.entrySet()) {
			if (!isEDB)
				entity = entity + "\"" + attributeEntry.getKey() + "\":";
			if (attributeEntry.getValue() == null)
				temp = "null";
			else if (attributeEntry.getValue().getClass().getSimpleName()
					.equals("String"))
				if (isEDB)
					temp = "'" + attributeEntry.getValue() + "'";
				else
					temp = "\"" + attributeEntry.getValue() + "\"";
			else
				temp = attributeEntry.getValue().toString();
			entity = entity + temp + ",";
		}
		if (isEDB) {
			if (entity.endsWith(","))
				entity = entity + Integer.toString(timestamp) + ").\n";
			else
				entity = entity + "," + Integer.toString(timestamp) + ").\n";
		} else {
			if (entity.endsWith(","))
				entity = entity + "\"ts\":" + Integer.toString(timestamp)
						+ "}.\n";
			else
				entity = entity + ", \"ts\":" + Integer.toString(timestamp)
						+ "}.\n";
		}
		return entity;
	}
}
