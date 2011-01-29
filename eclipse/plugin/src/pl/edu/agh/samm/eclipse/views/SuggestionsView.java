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
package pl.edu.agh.samm.eclipse.views;

import java.util.List;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import pl.edu.agh.samm.common.core.IAlarm;
import pl.edu.agh.samm.common.metrics.IConfiguredMetric;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class SuggestionsView extends ViewPart implements ISelectionListener {

	public static String ID = "pl.edu.agh.samm.eclipse.views.SuggestionsView";
	private Table suggestionsTable;
	private TableColumn suggestionsColumn;
	private TableColumn rankColumn;

	@Override
	public void createPartControl(Composite parent) {
		Composite root = new Composite(parent, SWT.NULL);

		suggestionsTable = new Table(root, SWT.MULTI | SWT.FULL_SELECTION | SWT.VIRTUAL);
		suggestionsTable.setHeaderVisible(true);
		suggestionsTable.setLinesVisible(true);

		suggestionsColumn = new TableColumn(suggestionsTable, SWT.LEFT);
		suggestionsColumn.setText("Suggestion");

		rankColumn = new TableColumn(suggestionsTable, SWT.RIGHT);
		rankColumn.setText("Rank");

		TableColumnLayout tableLayout = new TableColumnLayout();
		root.setLayout(tableLayout);
		tableLayout.setColumnData(suggestionsColumn, new ColumnWeightData(80));
		tableLayout.setColumnData(rankColumn, new ColumnWeightData(20));

		getSite().getPage().addSelectionListener(this);
	}

	@Override
	public void setFocus() {
		suggestionsTable.setFocus();
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (part instanceof AlarmsView) {
			if (!selection.isEmpty()) {
				if (selection instanceof IStructuredSelection) {
					IStructuredSelection structuredSelection = (IStructuredSelection) selection;
					if (structuredSelection.getFirstElement() instanceof IAlarm) {
						IAlarm alarm = (IAlarm) structuredSelection.getFirstElement();
						List<IConfiguredMetric> metricsToStart = alarm.getMetricsToStart();

						suggestionsTable.clearAll();

						for (IConfiguredMetric metric : metricsToStart) {
							TableItem tableItem = new TableItem(suggestionsTable, SWT.NONE);
							Number rank = alarm.getSuggestedMetricRank(metric);
							String rankString = (rank == null) ? "0.0" : rank.toString();
							tableItem.setText(new String[] { metric.getMetricURI(), rankString });
						}

					}
				}
			}
		}
	}

}
