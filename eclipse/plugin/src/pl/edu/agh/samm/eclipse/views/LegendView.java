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
package pl.edu.agh.samm.eclipse.views;

import java.io.InputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class LegendView extends ViewPart {

	public static String ID = "pl.edu.agh.samm.eclipse.views.LegendView";
	private Table legendTable;
	private TableColumn itemColumn;
	private TableColumn descriptionColumn;

	public LegendView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		Composite root = new Composite(parent, SWT.NULL);
		root.setLayout(new FillLayout(SWT.VERTICAL));

		legendTable = new Table(root, SWT.MULTI | SWT.FULL_SELECTION | SWT.VIRTUAL);
		legendTable.setHeaderVisible(true);

		itemColumn = new TableColumn(legendTable, SWT.LEFT);
		itemColumn.setText("Item");

		descriptionColumn = new TableColumn(legendTable, SWT.LEFT);
		descriptionColumn.setText("Description");

		TableItem item1 = new TableItem(legendTable, SWT.NONE);
		item1.setText(1, "Element 1");
		InputStream inputStream1 = this.getClass().getClassLoader()
				.getResourceAsStream("/icons/alt_launcher.ico");
		item1.setImage(0, new Image(parent.getDisplay(), inputStream1));

		TableItem item2 = new TableItem(legendTable, SWT.NONE);
		InputStream inputStream2 = this.getClass().getClassLoader()
				.getResourceAsStream("/icons/alt_window_16.gif");
		item2.setImage(0, new Image(parent.getDisplay(), inputStream2));
		item2.setText(1, "Element 2");

		itemColumn.pack();
		descriptionColumn.pack();
	}

	@Override
	public void setFocus() {
		legendTable.setFocus();
	}

}
