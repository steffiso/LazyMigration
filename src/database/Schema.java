package database;

public class Schema {

	private String kind;
	private int schemaversion;
	private String values;
	
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
	public String getValues() {
		return values;
	}
	public void setValues(String values) {
		this.values = values;
	}
}
