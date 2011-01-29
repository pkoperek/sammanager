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
package pl.edu.agh.samm.eclipse.commands;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;

import pl.edu.agh.samm.common.core.IRMILearninegStageListener;
import pl.edu.agh.samm.eclipse.SAMM;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class LearningJob implements IRMILearninegStageListener, IRunnableWithProgress {

	private boolean finished = false;
	private IProgressMonitor monitor;;

	// @Override
	// protected IStatus run(IProgressMonitor monitor) {
	// try {
	// SAMM.startInitialLearning(this);
	// synchronized (this) {
	// while (!finished)
	// this.wait();
	// }
	// Display.getDefault().asyncExec(new Runnable() {
	//
	// @Override
	// public void run() {
	// MessageDialog.openInformation(Display.getDefault()
	// .getActiveShell(), "Initial learning finished",
	// "Initial learning has finished");
	// }
	//
	// });
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// SAMM.handleException(e);
	// }
	// return Status.OK_STATUS;
	// }

	@Override
	public void learninegStageFinished() throws RemoteException {
		synchronized (this) {
			finished = true;
			this.notifyAll();
		}
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		try {
			this.monitor = monitor;
			SAMM.startInitialLearning(this);
			synchronized (this) {
				while (!finished)
					this.wait();
			}
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {
					MessageDialog.openInformation(Display.getDefault().getActiveShell(),
							"Initial learning finished", "Initial learning has finished");
				}

			});

		} catch (Exception e) {
			e.printStackTrace();
			SAMM.handleException(e);
		}

	}

	@Override
	public void startedNewAction(String act) throws RemoteException {
		monitor.beginTask("Executing action " + act, IProgressMonitor.UNKNOWN);
	}

	@Override
	public void waiting() throws RemoteException {
		monitor.beginTask("Waiting for measurements to stabilize", IProgressMonitor.UNKNOWN);
	}

	@Override
	public void taskDone() throws RemoteException {
		monitor.done();

	}

	@Override
	public void preActionWaiting() {
		monitor.beginTask("Collecting pre-action measurements", IProgressMonitor.UNKNOWN);
	}

	@Override
	public void postActionWaiting() {
		monitor.beginTask("Collecting post-action measurements", IProgressMonitor.UNKNOWN);

	}

}
