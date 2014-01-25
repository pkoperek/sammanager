package pl.edu.agh.samm.testapp.core;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.apache.commons.math.ConvergenceException;
import org.apache.commons.math.FunctionEvaluationException;

public interface IRemoteSlave extends Remote {
	double numericIntegration(final String formula, double min, double max)
			throws ConvergenceException, FunctionEvaluationException,
			IllegalArgumentException, RemoteException;

	void scheduleIntegration(String expression) throws RemoteException;

	String getId() throws RemoteException;

	void setId(String string) throws RemoteException;

	boolean canTakeMore() throws RemoteException;
}
