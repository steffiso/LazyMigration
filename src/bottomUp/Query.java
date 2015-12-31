package bottomUp;

import java.util.ArrayList;

public class Query {

	private Relation idbRelation;
	private ArrayList<Relation> relations;
	private ArrayList<Condition> conditions;

	public Query(Relation idbRelation, ArrayList<Relation> relations) {
		super();
		this.idbRelation = idbRelation;
		this.relations = relations;
	}

	public Relation getIdbRelation() {
		return idbRelation;
	}

	public void setIdbRelation(Relation idbRelation) {
		this.idbRelation = idbRelation;
	}

	public ArrayList<Relation> getRelations() {
		return relations;
	}

	public void setRelations(ArrayList<Relation> relations) {
		this.relations = relations;
	}

	public ArrayList<Condition> getConditions() {
		return conditions;
	}

	public void setConditions(ArrayList<Condition> conditions) {
		this.conditions = conditions;
	}

}
