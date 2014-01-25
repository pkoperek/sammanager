package pl.edu.agh.samm.testapp.core;

public interface IExpressionGenerator extends Stoppable {
	int getQueueLength();
	
	String getNewExpression();

	void startGeneration();

	void stopGeneration();

	void giveBack(String expression);
}