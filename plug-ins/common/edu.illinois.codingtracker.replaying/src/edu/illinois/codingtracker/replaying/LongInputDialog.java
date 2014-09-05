/**
 * This file is licensed under the University of Illinois/NCSA Open Source License. See LICENSE.TXT for details.
 */
package edu.illinois.codingtracker.replaying;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * @author Stas Negara
 * 
 */
public class LongInputDialog extends Dialog {

	private static long input= 0;

	private final String message;

	private Text text;

	private String prompt;

	protected LongInputDialog(Shell parentShell, String message, String prompt) {
		super(parentShell);
		this.message= message;
		this.prompt = prompt;
	}

	public long getInput() {
		return input;
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(message);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite= new Composite(parent, SWT.NONE);
		GridLayout layout= new GridLayout(2, false);
		layout.horizontalSpacing= 5;
		composite.setLayout(layout);
		Label label= new Label(composite, SWT.NONE);
		label.setLayoutData(new GridData(140, 15));
		label.setText(prompt);
		text= new Text(composite, SWT.BORDER);
		text.setLayoutData(new GridData(85, 15));
		text.setText(String.valueOf(input));
		return composite;

	}

	@Override
	protected void okPressed() {
		try {
			input= Long.valueOf(text.getText().trim());
		} catch (NumberFormatException ex) {
			input= 0;
		}
		super.okPressed();
	}

}
