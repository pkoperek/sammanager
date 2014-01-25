package pl.edu.agh.samm.testapp.core;

import java.io.Serializable;
import java.rmi.RemoteException;

import org.apache.commons.math.ConvergenceException;
import org.apache.commons.math.FunctionEvaluationException;

/**
 * This file is part of SAMM.
 *
 * SAMM is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SAMM is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SAMM.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * @author koperek
 * 
 */
public class RemoteSlaveProxy implements IRemoteSlave, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1103077575894052152L;
	private ISlave slave = null;

	public RemoteSlaveProxy(ISlave slave) {
		this.slave = slave;
	}

	public void stopExecution() throws RemoteException {
		slave.stopExecution();
	}

	public double numericIntegration(String formula, double min, double max)
			throws ConvergenceException, FunctionEvaluationException,
			IllegalArgumentException, RemoteException {
		return slave.numericIntegration(formula, min, max);
	}

	public void scheduleIntegration(String expression) throws RemoteException {
		slave.scheduleIntegration(expression);
	}

	public String getId() throws RemoteException {
		return slave.getId();
	}

	public void setId(String string) throws RemoteException {
		slave.setId(string);
	}

	@Override
	public boolean canTakeMore() throws RemoteException {
		return slave.canTakeMore();
	}

}
