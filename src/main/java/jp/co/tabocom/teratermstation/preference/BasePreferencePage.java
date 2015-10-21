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
    private Text ttlCharCodeTxt;

    public BasePreferencePage() {
        super("基本設定");
    }

    @Override
    protected Control createContents(Composite parent) {
        final Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(3, false));
        IPreferenceStore preferenceStore = getPreferenceStore();

        // ========== 定義ディレクトリの場所 ========== //
        new Label(composite, SWT.LEFT).setText("定義基点ディレクトリ：");
        dirTxt = new Text(composite, SWT.BORDER);
        dirTxt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        dirTxt.setText(preferenceStore.getString(PreferenceConstants.TARGET_DIR));
        Button dirBtn = new Button(composite, SWT.NULL);
        dirBtn.setText("参照");
        dirBtn.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) {
            }

            public void widgetSelected(SelectionEvent e) {
                DirectoryDialog dialog = new DirectoryDialog(getShell());
                dialog.setText("定義基点ディレクトリを指定してください。");
                String currentPath = dirTxt.getText();
                dialog.setFilterPath(currentPath.isEmpty() ? "C:\\" : currentPath);
                String dir = dialog.open();
                if (dir != null) {
                    dirTxt.setText(dir);
                }
            }
        });

        // ========== 定義ディレクトリの場所 ========== //
        new Label(composite, SWT.LEFT).setText("TTLファイルの文字コード：");
        ttlCharCodeTxt = new Text(composite, SWT.BORDER);
        GridData ttlCharCodeTxtGrDt = new GridData(GridData.FILL_HORIZONTAL);
        ttlCharCodeTxtGrDt.horizontalSpan = 2;
        ttlCharCodeTxt.setLayoutData(ttlCharCodeTxtGrDt);
        ttlCharCodeTxt.setText(preferenceStore.getString(PreferenceConstants.TTL_CHARCODE));
        new Label(composite, SWT.LEFT).setText("");
        Label ttlCharCodeDescLbl = new Label(composite, SWT.LEFT);
        GridData ttlCharCodeDescLblGrDt = new GridData(GridData.FILL_HORIZONTAL);
        ttlCharCodeDescLblGrDt.horizontalSpan = 2;
        ttlCharCodeDescLbl.setText("例) Shift-JIS, UTF-8など。省略や不正な場合はShift-JISになります。");

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
        if (this.ttlCharCodeTxt != null) {
            ps.setValue(PreferenceConstants.TTL_CHARCODE, this.ttlCharCodeTxt.getText());
        }
        return true;
    }
}
