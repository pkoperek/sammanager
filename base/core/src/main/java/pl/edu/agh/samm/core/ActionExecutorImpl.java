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
 * 
 */
package pl.edu.agh.samm.core;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.agh.samm.common.action.Action;
import pl.edu.agh.samm.common.action.ActionExecution;
import pl.edu.agh.samm.common.core.IActionExecutionListener;
import pl.edu.agh.samm.common.tadapter.ActionNotSupportedException;
import pl.edu.agh.samm.common.tadapter.ITransportAdapter;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 */
public class ActionExecutorImpl implements IActionExecutor {

	private static final Logger logger = LoggerFactory
			.getLogger(ActionExecutorImpl.class);

	private ExecutorService executor = Executors.newCachedThreadPool();
	private List<IActionExecutionListener> listeners = new CopyOnWriteArrayList<IActionExecutionListener>();

	/**
	 * This field should be thread safe! SpringDM's osgi:set is thread-safe
	 */
	private Set<ITransportAdapter> transportAdapters;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * pl.edu.agh.samm.core.IActionExecutor#executeRequest(pl.edu.agh.samm.common
	 * .action.Action)
	 */
	@Override
	public void executeRequest(Action actionToExecute) {
		ActionExecutionRunnable command = new ActionExecutionRunnable(
				actionToExecute);
		executor.execute(command);
	}

	private class ActionExecutionRunnable implements Runnable {

		private Action action;

		public ActionExecutionRunnable(Action action) {
			this.action = action;
		}

		@Override
		public void run() {
			executeAction(action);
		}

	}

	public void setTransportAdapters(Set<ITransportAdapter> transportAdapters) {
		this.transportAdapters = transportAdapters;
	}

	private void executeAction(Action action) {
		Date start = null;
		Date end = null;
		boolean executed = false;
		try {

			for (ITransportAdapter adapter : transportAdapters) {
				if (adapter.isActionSupported(action.getActionURI())) {
					logger.info("Executing action " + action);
					start = new Date();
					adapter.executeAction(action);
					end = new Date();
					logger.info("Done executing action " + action);
					executed = true;
					break;
				}
			}

		} catch (RuntimeException e) {
			logger.error("Error running action", e);
		} catch (ActionNotSupportedException e) {
			logger.error("Bad TransportAdapter action implementation", e);
		}

		if (executed) {
			fireActionExecuted(new ActionExecution(action, start, end));
		}
	}

	@Override
	public void executeRequest(Action actionToExecute, boolean sync) {
		if (sync == false) {
			executeRequest(actionToExecute);
		} else {
			executeAction(actionToExecute);
		}

	}

	@Override
	public void addActionExecutorListener(IActionExecutionListener listener) {
		this.listeners.add(listener);
	}

	@Override
	public void removeActionExecutorListener(IActionExecutionListener listener) {
		this.listeners.remove(listener);
	}

	protected void fireActionExecuted(ActionExecution actionExecution) {
		for (IActionExecutionListener listener : listeners) {
			try {
				listener.notifyActionExecution(actionExecution);
			} catch (Exception e) {
				logger.error(
						"Listener failed on notification about action execution! ("
								+ actionExecution.toString() + ")", e);
			}
		}
	}

}
