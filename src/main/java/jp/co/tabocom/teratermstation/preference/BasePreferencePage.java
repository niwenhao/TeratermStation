package jp.co.tabocom.teratermstation.preference;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class BasePreferencePage extends PreferencePage {

    private Text dirTxt;

    public BasePreferencePage() {
        super("��{�ݒ�");
    }

    @Override
    protected Control createContents(Composite parent) {
        final Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(3, false));
        IPreferenceStore preferenceStore = getPreferenceStore();

        // ========== ��`�f�B���N�g���̏ꏊ ========== //
        new Label(composite, SWT.LEFT).setText("��`��_�f�B���N�g���F");
        dirTxt = new Text(composite, SWT.BORDER);
        dirTxt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        dirTxt.setText(preferenceStore.getString(PreferenceConstants.TARGET_DIR));
        Button dirBtn = new Button(composite, SWT.NULL);
        dirBtn.setText("�Q��");
        dirBtn.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) {
            }

            public void widgetSelected(SelectionEvent e) {
                DirectoryDialog dialog = new DirectoryDialog(getShell());
                dialog.setText("��`��_�f�B���N�g�����w�肵�Ă��������B");
                String currentPath = dirTxt.getText();
                dialog.setFilterPath(currentPath.isEmpty() ? "C:\\" : currentPath);
                String dir = dialog.open();
                if (dir != null) {
                    dirTxt.setText(dir);
                }
            }
        });

        noDefaultAndApplyButton();
        return composite;
    }

    @Override
    public boolean performOk() {
        IPreferenceStore ps = getPreferenceStore();
        if (ps == null) {
            return true;
        }
        if (this.dirTxt != null) {
            ps.setValue(PreferenceConstants.TARGET_DIR, this.dirTxt.getText());
        }
        return true;
    }
}
