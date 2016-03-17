package datalog;

public class MagicCondition {
	private String kindLeft;
	private String kindRight;
	private int positionLeft;
	private int positionRight;
	private boolean alreadyFoundResults;
	private String nameOfMagicView;

	public MagicCondition(String kindLeft, String kindRight, int positionLeft,
			int positionRight) {
		super();
		this.kindLeft = kindLeft;
		this.kindRight = kindRight;
		this.positionLeft = positionLeft;
		this.positionRight = positionRight;
	}

	public String getKindLeft() {
		return kindLeft;
	}

	public String getKindRight() {
		return kindRight;
	}

	public int getPositionLeft() {
		return positionLeft;
	}

	public int getPositionRight() {
		return positionRight;
	}

	public boolean hasAlreadyResults() {
		return alreadyFoundResults;
	}

	public void setKindLeft(String kindLeft) {
		this.kindLeft = kindLeft;
	}

	public void setKindRight(String kindRight) {
		this.kindRight = kindRight;
	}

	public void setPositionLeft(int positionLeft) {
		this.positionLeft = positionLeft;
	}

	public void setPositionRight(int positionRight) {
		this.positionRight = positionRight;
	}

	public void setAlreadyFoundResults(boolean results) {
		this.alreadyFoundResults = results;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (alreadyFoundResults ? 1231 : 1237);
		result = prime * result
				+ ((kindLeft == null) ? 0 : kindLeft.hashCode());
		result = prime * result
				+ ((kindRight == null) ? 0 : kindRight.hashCode());
		result = prime * result + positionLeft;
		result = prime * result + positionRight;
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MagicCondition other = (MagicCondition) obj;
		if (kindLeft == null) {
			if (other.kindLeft != null)
				return false;
		} else if (!kindLeft.equals(other.kindLeft))
			return false;
		if (kindRight == null) {
			if (other.kindRight != null)
				return false;
		} else if (!kindRight.equals(other.kindRight))
			return false;
		if (positionLeft != other.positionLeft)
			return false;
		if (positionRight != other.positionRight)
			return false;
		return true;
	}


	@Override
	public String toString() {
		return "MagicCondition [kindLeft=" + kindLeft + ", kindRight="
				+ kindRight + ", positionLeft=" + positionLeft
				+ ", positionRight=" + positionRight + "]";
	}

	public String getNameOfMagicView() {
		return nameOfMagicView;
	}

	public void setNameOfMagicView(String nameOfMagicView) {
		this.nameOfMagicView = nameOfMagicView;
	}

}