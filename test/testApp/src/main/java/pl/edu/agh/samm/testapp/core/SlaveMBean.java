package pl.edu.agh.samm.testapp.core;

public interface SlaveMBean extends ISlave {
	int getQueueLength();
	
	long getProcessedCount();
	
	long getSumProcessingTime();
	
	long getAvgProcessTime();
}
