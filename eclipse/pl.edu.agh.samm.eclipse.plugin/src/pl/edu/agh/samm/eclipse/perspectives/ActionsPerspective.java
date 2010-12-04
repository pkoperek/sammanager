/**
 * 
 */
package pl.edu.agh.samm.eclipse.perspectives;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import pl.edu.agh.samm.eclipse.views.ActionsListView;
import pl.edu.agh.samm.eclipse.views.DescriptionView;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class ActionsPerspective implements IPerspectiveFactory {

	public static String ID = "pl.edu.agh.samm.eclipse.perspectives.ActionsPerspective";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui
	 * .IPageLayout)
	 */
	@Override
	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(false);
		layout.addView(ActionsListView.ID, IPageLayout.LEFT, 0.25f, layout.getEditorArea());
		layout.getViewLayout(ActionsListView.ID).setCloseable(false);
		layout.addView(DescriptionView.ID, IPageLayout.RIGHT, 0.75f, layout.getEditorArea());
		layout.getViewLayout(DescriptionView.ID).setCloseable(false);
	}

}
