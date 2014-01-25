package pl.edu.agh.samm.testapp.core.record;

import java.util.ArrayList;
import java.util.List;

public class RecordedSession {
	private List<ExpressionsSet> expressionsSets = new ArrayList<ExpressionsSet>();

	public List<ExpressionsSet> getExpressionsSets() {
		return expressionsSets;
	}

	public void setExpressionsSets(List<ExpressionsSet> expressionsSets) {
		this.expressionsSets = expressionsSets;
	}

	public void addExpressionSet(ExpressionsSet set) {
		this.expressionsSets.add(set);
	}

}
