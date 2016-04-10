package jp.co.tabocom.teratermstation.ui;

import java.util.List;

import jp.co.tabocom.teratermstation.ui.action.TeratermStationDnDAction;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolTip;

public class PluginSelectDialog extends Dialog {

    private List<TeratermStationDnDAction> actionList;
    private TeratermStationDnDAction selectedAction;
    private Combo pluginCombo;
    private StyledText widget;

    public PluginSelectDialog(Shell parentShell, List<TeratermStationDnDAction> actionList) {
        super(parentShell);
        this.actionList = actionList;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite) super.createDialogArea(parent);
        composite.setLayout(new GridLayout(1, false));
        pluginCombo = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
        pluginCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        for (TeratermStationDnDAction action : actionList) {
            pluginCombo.add(action.getText());
        }
        pluginCombo.select(0);
        pluginCombo.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                int idx = pluginCombo.getSelectionIndex();
                selectedAction = actionList.get(idx);
                ToolTip toolTip = selectedAction.getToolTip();
                if (toolTip != null) {
                    widget.setText(toolTip.getMessage());
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });

        this.widget = new StyledText(composite, SWT.BORDER | SWT.V_SCROLL);
        this.widget.setLayoutData(new GridData(GridData.FILL_BOTH));
        this.widget.setMargins(5, 5, 10, 5);
        this.widget.setEditable(false);
        this.widget.setWordWrap(true);
        ToolTip toolTip = actionList.get(0).getToolTip();
        if (toolTip != null) {
            this.widget.setText(toolTip.getMessage());
        }

        this.selectedAction = actionList.get(0);

        return composite;
    }

    public TeratermStationDnDAction getSelectedAction() {
        return selectedAction;
    }

    @Override
    protected Point getInitialSize() {
        return new Point(360, 200);
    }

    @Override
    protected void setShellStyle(int newShellStyle) {
        super.setShellStyle(getShellStyle() | SWT.RESIZE);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("拡張機能選択");
    }
}
