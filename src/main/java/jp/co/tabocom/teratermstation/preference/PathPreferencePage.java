package jp.co.tabocom.teratermstation.preference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class PathPreferencePage extends PreferencePage {

    private Text ttmacroTxt;
    private Text workDirTxt;
    private Text logDirTxt;
    private Text iniFileDirTxt;
    private List<Text> textList;

    public PathPreferencePage() {
        super("パス設定");
    }

    @Override
    protected Control createContents(Composite parent) {
        final Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(3, false));
        IPreferenceStore preferenceStore = getPreferenceStore();

        // ========== TeraTermマクロの場所 ========== //
        new Label(composite, SWT.LEFT).setText("TeraTermマクロ：");
        ttmacroTxt = new Text(composite, SWT.BORDER);
        ttmacroTxt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        ttmacroTxt.setText(preferenceStore.getString(PreferenceConstants.TTPMACRO_EXE));
        Button ttmacroBtn = new Button(composite, SWT.NULL);
        ttmacroBtn.setText("参照");
        ttmacroBtn.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) {
            }

            public void widgetSelected(SelectionEvent e) {
                FileDialog dialog = new FileDialog(getShell());
                dialog.setText("TeraTermマクロ(ttpmacro.exe)を指定してください。");
                dialog.setFilterPath("C:\\Program Files (x86)");
                dialog.setFilterExtensions(new String[] { "*.exe" });
                String file = dialog.open();
                if (file != null) {
                    ttmacroTxt.setText(file);
                }
            }
        });

        Group dirGrp = new Group(composite, SWT.NONE);
        dirGrp.setLayout(new GridLayout(3, false));
        GridData dirGrpGrDt = new GridData(GridData.FILL_HORIZONTAL);
        dirGrpGrDt.horizontalSpan = 3;
        dirGrp.setLayoutData(dirGrpGrDt);
        dirGrp.setText("作業領域");

        this.textList = new ArrayList<Text>();

        // ========== ワークディレクトリの場所 ========== //
        new Label(dirGrp, SWT.LEFT).setText("ワークディレクトリ：");
        workDirTxt = new Text(dirGrp, SWT.BORDER);
        workDirTxt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        workDirTxt.setText(preferenceStore.getString(PreferenceConstants.WORK_DIR));
        this.textList.add(workDirTxt);
        Button workDirBtn = new Button(dirGrp, SWT.NULL);
        workDirBtn.setText("参照");
        workDirBtn.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) {
            }

            public void widgetSelected(SelectionEvent e) {
                String dir = dirDialogOpen("ワークディレクトリを指定してください。", workDirTxt.getText());
                if (dir != null) {
                    workDirTxt.setText(dir);
                }
            }
        });

        // ========== ログディレクトリの場所 ========== //
        new Label(dirGrp, SWT.LEFT).setText("ログディレクトリ：");
        logDirTxt = new Text(dirGrp, SWT.BORDER);
        logDirTxt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        logDirTxt.setText(preferenceStore.getString(PreferenceConstants.LOG_DIR));
        this.textList.add(logDirTxt);
        Button logDirBtn = new Button(dirGrp, SWT.NULL);
        logDirBtn.setText("参照");
        logDirBtn.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) {
            }

            public void widgetSelected(SelectionEvent e) {
                String dir = dirDialogOpen("ログディレクトリ(Local)を指定してください。", logDirTxt.getText());
                if (dir != null) {
                    logDirTxt.setText(dir);
                }
            }
        });

        // ========== INIファイルディレクトリの場所 ========== //
        new Label(dirGrp, SWT.LEFT).setText("INIファイルディレクトリ：");
        iniFileDirTxt = new Text(dirGrp, SWT.BORDER);
        iniFileDirTxt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        iniFileDirTxt.setText(preferenceStore.getString(PreferenceConstants.INIFILE_DIR));
        this.textList.add(iniFileDirTxt);
        Button iniDirBtn = new Button(dirGrp, SWT.NULL);
        iniDirBtn.setText("参照");
        iniDirBtn.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) {
            }

            public void widgetSelected(SelectionEvent e) {
                String dir = dirDialogOpen("INIファイルが置かれているディレクトリを指定してください。", iniFileDirTxt.getText());
                if (dir != null) {
                    iniFileDirTxt.setText(dir);
                }
            }
        });

        Button mkDirBtn = new Button(dirGrp, SWT.NULL);
        GridData mkDirBtnGrDt = new GridData();
        mkDirBtnGrDt.horizontalSpan = 3;
        mkDirBtnGrDt.horizontalAlignment = SWT.RIGHT;
        mkDirBtn.setLayoutData(mkDirBtnGrDt);
        mkDirBtn.setText("ディレクトリを作成する");
        mkDirBtn.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) {
            }

            public void widgetSelected(SelectionEvent e) {
                // TODO 既にディレクトリが存在する場合は警告を出すなどの細かい対応も追加したい。
                // 今はとりあえず作るだけです。
                int mkNum = 0;
                for (Text text : textList) {
                    if (!text.getText().isEmpty()) {
                        File dir = new File(text.getText());
                        dir.mkdirs();
                        mkNum++;
                    }
                }
                if (mkNum > 0) {
                    MessageDialog.openInformation(composite.getShell(), "ディレクトリ作成", "ディレクトリを作成しました。");
                } else {
                    MessageDialog.openWarning(composite.getShell(), "ディレクトリ作成", "ディレクトリ作成対象が設定されていません。");
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
        if (this.ttmacroTxt != null) {
            ps.setValue(PreferenceConstants.TTPMACRO_EXE, this.ttmacroTxt.getText());
        }
        if (this.workDirTxt != null) {
            ps.setValue(PreferenceConstants.WORK_DIR, this.workDirTxt.getText());
        }
        if (this.logDirTxt != null) {
            ps.setValue(PreferenceConstants.LOG_DIR, this.logDirTxt.getText());
        }
        if (this.iniFileDirTxt != null) {
            ps.setValue(PreferenceConstants.INIFILE_DIR, this.iniFileDirTxt.getText());
        }
        return true;
    }

    private String dirDialogOpen(String msg, String currentPath) {
        DirectoryDialog dialog = new DirectoryDialog(getShell());
        dialog.setText(msg);
        dialog.setFilterPath(currentPath.isEmpty() ? "C:\\" : currentPath);
        String dir = dialog.open();
        return dir;
    }
}
