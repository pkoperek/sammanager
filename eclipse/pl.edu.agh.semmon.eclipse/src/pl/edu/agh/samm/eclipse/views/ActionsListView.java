/**
 * 
 */
package pl.edu.agh.samm.eclipse.views;

import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import pl.edu.agh.samm.eclipse.SAMM;
import pl.edu.agh.samm.eclipse.views.providers.ActionsListContentProvider;
import pl.edu.agh.samm.eclipse.views.providers.ActionsListLabelProvider;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class ActionsListView extends ViewPart {

	public static final String ID = "pl.edu.agh.samm.eclipse.views.ActionsListView";
	private ListViewer viewer;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		Composite root = new Composite(parent, SWT.NULL);
		root.setLayout(new FillLayout(SWT.VERTICAL));

		viewer = new ListViewer(root);
		viewer.setContentProvider(new ActionsListContentProvider());
		viewer.setLabelProvider(new ActionsListLabelProvider());

		getSite().setSelectionProvider(viewer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		viewer.getList().setFocus();
	}

	public void refresh() {
		viewer.setInput(SAMM.getActions());
	}
}
