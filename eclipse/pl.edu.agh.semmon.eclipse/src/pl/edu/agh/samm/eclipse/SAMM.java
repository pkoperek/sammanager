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
package pl.edu.agh.samm.eclipse;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;

import pl.edu.agh.samm.common.action.Action;
import pl.edu.agh.samm.common.core.IActionExecutionListener;
import pl.edu.agh.samm.common.core.IAlarm;
import pl.edu.agh.samm.common.core.IAlarmListener;
import pl.edu.agh.samm.common.core.ICoreManagement;
import pl.edu.agh.samm.common.core.IRMILearninegStageListener;
import pl.edu.agh.samm.common.core.IResourceListener;
import pl.edu.agh.samm.common.knowledge.IKnowledge;
import pl.edu.agh.samm.common.metrics.IMetricsManagerListener;
import pl.edu.agh.samm.eclipse.commands.GetWeightsPropertiesCommand;
import pl.edu.agh.samm.eclipse.product.ApplicationWorkbenchAdvisor;

/**
 * This class controls all aspects of the application's execution
 * 
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 */
public class SAMM implements IApplication {

	private static ICoreManagement coreManagement;

	private static IKnowledge knowledge;

	private static IResourceListener resourcesListener;

	private static List<IAlarm> alarms = new LinkedList<IAlarm>();

	private static IAlarmListener alarmListener;

	private static List<Action> actions = new LinkedList<Action>();

	private static IActionExecutionListener actionExecutionListener;

	public static final String PLUGIN_ID = "pl.edu.agh.samm.eclipse";

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.
	 * IApplicationContext)
	 */
	@Override
	public Object start(IApplicationContext context) throws Exception {

		Display display = PlatformUI.createDisplay();

		int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
		if (returnCode == PlatformUI.RETURN_OK || returnCode == PlatformUI.RETURN_RESTART) {
			SAMM.disconnectFromCore();
		}
		if (returnCode == PlatformUI.RETURN_RESTART)
			return IApplication.EXIT_RESTART;
		else
			return IApplication.EXIT_OK;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
	@Override
	public void stop() {
		final IWorkbench workbench = PlatformUI.getWorkbench();
		if (workbench == null)
			return;
		final Display display = workbench.getDisplay();
		display.syncExec(new Runnable() {
			@Override
			public void run() {
				if (!display.isDisposed())
					workbench.close();
			}
		});
	}

	public static IKnowledge getKnowledge() {
		return knowledge;
	}

	public static ICoreManagement getCoreManagement() {
		return coreManagement;
	}

	public static IResourceListener getResourceListener() {
		return resourcesListener;
	}

	public static boolean isConnected() {
		return coreManagement != null && knowledge != null && resourcesListener != null;
	}

	public static void connectToCore(ICoreManagement coreManagement, IKnowledge knowledge,
			IResourceListener resourcesListener, IAlarmListener alarmListener,
			IMetricsManagerListener metricsManagerListener, IActionExecutionListener actionExecutionListener) {
		try {
			SAMM.coreManagement = coreManagement;
			SAMM.knowledge = knowledge;
			SAMM.resourcesListener = resourcesListener;
			SAMM.alarmListener = alarmListener;
			SAMM.actionExecutionListener = actionExecutionListener;

			SAMM.getCoreManagement().addResourceListener(resourcesListener);
			SAMM.getCoreManagement().addAlarmListener(alarmListener);
			SAMM.getCoreManagement().addRunningMetricsManagerListener(metricsManagerListener);
			SAMM.getCoreManagement().addActionExecutorListener(actionExecutionListener);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void startInitialLearning(IRMILearninegStageListener listener) {
		if (SAMM.isConnected()) {
			try {
				IRMILearninegStageListener rmiListener = (IRMILearninegStageListener) UnicastRemoteObject
						.exportObject(listener, 0);
				SAMM.getCoreManagement().startLearning(rmiListener);
			} catch (RemoteException e) {
				e.printStackTrace();
				SAMM.handleException(e);
			}
		} else {
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Connect to core",
					"Connect to core first!");
		}
	}

	public static void disconnectFromCore() {
		try {
			if (resourcesListener != null) {
				SAMM.getCoreManagement().removeResourceListener(resourcesListener);
			}
			if (alarmListener != null) {
				SAMM.getCoreManagement().removeAlarmListener(alarmListener);
			}
		} catch (Exception e) {
			SAMM.handleException(e);
		}
	}

	public static void addAction(Action action) {
		actions.add(action);
	}

	public static void addAlarm(IAlarm alarm) {
		alarms.add(alarm);
	}

	public static List<IAlarm> getAlarms() {
		return alarms;
	}

	public static List<Action> getActions() {
		return actions;
	}

	/*
	 * private static IRunningMetricsManager runningMetricsManager;
	 * 
	 * private static ICoreManagement coreManagement;
	 * 
	 * private static IKnowledge knowledge;
	 * 
	 * private static IResourceListener resourcesListener;
	 */

	public static Properties getProperties() {
		IHandlerService handler = (IHandlerService) PlatformUI.getWorkbench().getService(
				IHandlerService.class);
		try {
			return (Properties) handler.executeCommand(GetWeightsPropertiesCommand.ID, null);
		} catch (Exception e) {
			SAMM.handleException(e);
		}
		return null;
	}

	public static void handleException(Exception e) {
		MessageDialog messageDialog = new MessageDialog(Display.getDefault().getActiveShell(), "Error!",
				null, e.toString(), MessageDialog.ERROR, new String[] { "OK" }, 0);
		messageDialog.open();
		e.printStackTrace();
	}

	public static void showMessage(String title, String message, int messageType) {
		MessageDialog messageDialog = new MessageDialog(Display.getDefault().getActiveShell(), title, null,
				message, messageType, new String[] { "OK" }, 0);
		messageDialog.open();
	}
}
