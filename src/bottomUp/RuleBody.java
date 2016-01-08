package bottomUp;

import java.util.ArrayList;

public class RuleBody {
	public ArrayList<Predicate> predicates = null;
	public ArrayList<Condition> conditions = null;
	public RuleBody(ArrayList<Predicate> values, ArrayList<Condition> conditons) {
		super();
		this.predicates = values;
		this.conditions = conditons;
	}
}
