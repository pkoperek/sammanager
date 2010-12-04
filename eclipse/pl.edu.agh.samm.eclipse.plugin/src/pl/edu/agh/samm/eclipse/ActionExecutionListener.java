/**
 * 
 */
package pl.edu.agh.samm.eclipse;

import java.rmi.RemoteException;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import pl.edu.agh.samm.common.action.Action;
import pl.edu.agh.samm.common.core.IRMIActionExecutionListener;
import pl.edu.agh.samm.eclipse.views.ActionsListView;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class ActionExecutionListener implements IRMIActionExecutionListener {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * pl.edu.agh.samm.common.core.IRMIActionExecutionListener#notifyActionExecution
	 * (pl.edu.agh.samm.common.action.Action)
	 */
	@Override
	public void notifyActionExecution(Action executedAction) throws RemoteException {
		SAMM.addAction(executedAction);
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				if (workbenchWindow != null) {
					IWorkbenchPage workbenchPage = workbenchWindow.getActivePage();
					if (workbenchPage != null) {
						ActionsListView actionsListView = (ActionsListView) workbenchPage
								.findView(ActionsListView.ID);
						if (actionsListView != null) {
							actionsListView.refresh();
						}
					}
				}
			}
		});

	}

}
