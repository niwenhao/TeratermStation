package jp.co.tabocom.teratermstation.preference;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;

import jp.co.tabocom.teratermstation.Main;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
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
        super("基本設定");
    }

    @Override
    protected Control createContents(Composite parent) {
        final Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(4, false));
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

        Button restartBtn = new Button(composite, SWT.NULL);
        restartBtn.setText("再起動");
        restartBtn.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) {
            }

            public void widgetSelected(SelectionEvent event) {
                if (!performOk()) {
                    return;
                }
                String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
                File currentExecuteFile;
                try {
                    currentExecuteFile = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
                    if (currentExecuteFile.getName().endsWith(".jar")) {
                        ArrayList<String> command = new ArrayList<String>();
                        command.add(javaBin);
                        command.add("-jar");
                        command.add(currentExecuteFile.getPath());
                        ProcessBuilder builder = new ProcessBuilder(command);
                        builder.start();
                        getShell().getParent().getShell().close();
                    } else if (currentExecuteFile.getName().endsWith(".exe")) {
                        Runtime runtime = Runtime.getRuntime();
                        runtime.exec(new String[] { currentExecuteFile.toString() });
                        getShell().getParent().getShell().close();
                    } else {
                        MessageDialog.openWarning(composite.getShell(), "再起動", "jarまたはexe形式から起動された時だけ、このボタンからの再起動が可能です。");
                    }
                } catch (URISyntaxException | IOException e) {
                    e.printStackTrace();
                    StringWriter stringWriter = new StringWriter();
                    PrintWriter printWriter = new PrintWriter(stringWriter);
                    e.printStackTrace(printWriter);
                    Logger logger = Logger.getLogger("conntool");
                    String trace = stringWriter.toString();
                    logger.error(trace);
                    MessageDialog.openError(composite.getShell(), "再起動", "再起動に失敗しました。手で再起動してください。");
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
