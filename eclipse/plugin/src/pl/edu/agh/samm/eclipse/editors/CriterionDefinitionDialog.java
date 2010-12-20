/**
 * 
 */
package pl.edu.agh.samm.eclipse.editors;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import pl.edu.agh.samm.common.knowledge.ICriterion;
import pl.edu.agh.samm.eclipse.SAMM;
import pl.edu.agh.samm.eclipse.model.Criteria;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class CriterionDefinitionDialog extends Dialog {

	private Text upperThreshold;
	private Text lowerThreshold;
	private Combo criteriaSelection;
	private ICriterion criterion;
	private String selectedItem = null;

	protected CriterionDefinitionDialog(Shell parentShell) {
		super(parentShell);
		setBlockOnOpen(true);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Criteria definition");
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);

		composite.setLayout(new GridLayout(2, false));

		Label criteriaSelectionLabel = new Label(composite, SWT.LEFT);
		criteriaSelectionLabel.setLayoutData(new GridData(SWT.LEFT, SWT.NONE, false, false));
		criteriaSelectionLabel.setText("Criteria category:");
		criteriaSelection = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
		criteriaSelection.setItems(convertCriteria(Criteria.values()));
		criteriaSelection.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
		criteriaSelection.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = criteriaSelection.getSelectionIndex();
				if (index != -1) {
					String item = criteriaSelection.getItem(index);
					Criteria criteria = Criteria.valueOf(item);
					lowerThreshold.setEnabled(criteria.isLowerThresholdEnabled());
					upperThreshold.setEnabled(criteria.isUpperThresholdEnabled());
					selectedItem = criteriaSelection.getItem(index);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}
		});

		Label upperRangeLabel = new Label(composite, SWT.LEFT);
		upperRangeLabel.setText("Upper threshold");
		upperRangeLabel.setLayoutData(new GridData(SWT.LEFT, SWT.NONE, false, false));
		upperThreshold = new Text(composite, SWT.SINGLE | SWT.BORDER);
		upperThreshold.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));

		Label lowerRangeLabel = new Label(composite, SWT.LEFT);
		lowerRangeLabel.setText("Lower threshold");
		lowerRangeLabel.setLayoutData(new GridData(SWT.LEFT, SWT.NONE, false, false));
		lowerThreshold = new Text(composite, SWT.SINGLE | SWT.BORDER);
		lowerThreshold.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));

		return composite;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);

		Button button = getButton(IDialogConstants.OK_ID);
		// there is only one listener here - and we want to replace it with own
		// one :)
		Listener[] listeners = button.getListeners(SWT.Selection);
		for (Listener listener : listeners) {
			button.removeListener(SWT.Selection, listener);
		}

		button.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (isInputValid()) {
					okPressed();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	private String[] convertCriteria(Criteria[] criteria) {
		String[] converted = new String[criteria.length];
		for (int i = 0; i < converted.length; i++) {
			converted[i] = criteria[i].toString();
		}
		return converted;
	}

	@Override
	protected void okPressed() {
		Double lowerThreshold = null;
		Double upperThreshold = null;

		Criteria criteria = Criteria.valueOf(selectedItem);

		if (criteria.isLowerThresholdEnabled()) {
			lowerThreshold = Double.parseDouble(this.lowerThreshold.getText());
		}
		if (criteria.isUpperThresholdEnabled()) {
			upperThreshold = Double.parseDouble(this.upperThreshold.getText());
		}

		this.criterion = criteria.createCriterion(lowerThreshold, upperThreshold);

		// cleanup goes here
		super.okPressed();
	}

	private boolean isInputValid() {
		boolean retVal = true;

		if (criteriaSelection.getSelectionIndex() == -1) {
			retVal = false;
			SAMM.showMessage("Error", "Please choose criteria category!", MessageDialog.ERROR);
		}

		if (retVal) {
			try {
				if (lowerThreshold.isEnabled()) {
					Double.parseDouble(lowerThreshold.getText());
				}
			} catch (NumberFormatException e) {
				SAMM.showMessage("Error", "Please fill lower threshold text field with a number!",
						MessageDialog.ERROR);
				retVal = false;
			}
		}

		// this "if" is here to show the message box only one time
		if (retVal) {
			try {
				if (upperThreshold.isEnabled()) {
					Double.parseDouble(upperThreshold.getText());
				}
			} catch (NumberFormatException e) {
				SAMM.showMessage("Error", "Please fill upper threshold text field with a number!",
						MessageDialog.ERROR);
				retVal = false;
			}
		}

		return retVal;
	}

	public ICriterion getCriterion() {
		return criterion;
	}

}
