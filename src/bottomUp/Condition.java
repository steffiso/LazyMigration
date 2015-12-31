package bottomUp;

public class Condition {

	private String leftOperand;
	private String rightOperand;
	private String operator;
	public Condition(String leftOperand, String rightOperand, String operator) {
		super();
		this.leftOperand = leftOperand;
		this.rightOperand = rightOperand;
		this.operator = operator;
	}
	public String getLeftOperand() {
		return leftOperand;
	}
	public String getRightOperand() {
		return rightOperand;
	}
	public String getOperator() {
		return operator;
	}
	public void setLeftOperand(String leftOperand) {
		this.leftOperand = leftOperand;
	}
	public void setRightOperand(String rightOperand) {
		this.rightOperand = rightOperand;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
}
