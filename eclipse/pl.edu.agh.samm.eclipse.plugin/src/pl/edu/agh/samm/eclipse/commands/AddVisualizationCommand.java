/**
 * 
 */
package pl.edu.agh.samm.eclipse.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import pl.edu.agh.samm.eclipse.dialogs.NewVisualizationDialog;
import pl.edu.agh.samm.eclipse.views.VisualizationsView;
import pl.edu.agh.samm.eclipse.views.visualization.IVisualisation;
import pl.edu.agh.samm.eclipse.views.visualization.Visualisation;
import pl.edu.agh.samm.eclipse.views.visualization.charts.ChartType;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class AddVisualizationCommand extends AbstractHandler implements IHandler {

	public static final String ID = "pl.edu.agh.samm.eclipse.commands.AddVisualization";

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		NewVisualizationDialog dialog = new NewVisualizationDialog(HandlerUtil.getActiveWorkbenchWindow(arg0)
				.getShell());
		if (dialog.open() == Window.OK) {
			String visName = dialog.getVisualizationName();
			ChartType visType = dialog.getVisualizationType();
			IVisualisation vis = new Visualisation(visType);
			vis.setName(visName);
			vis.setRunning(false);
			IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(arg0);
			IWorkbenchPage page = window.getActivePage();
			VisualizationsView view = (VisualizationsView) page.findView(VisualizationsView.ID);
			view.addVisualization(vis);
		}
		return null;
	}

}
