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
        super("�p�X�ݒ�");
    }

    @Override
    protected Control createContents(Composite parent) {
        final Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(3, false));
        IPreferenceStore preferenceStore = getPreferenceStore();

        // ========== TeraTerm�}�N���̏ꏊ ========== //
        new Label(composite, SWT.LEFT).setText("TeraTerm�}�N���F");
        ttmacroTxt = new Text(composite, SWT.BORDER);
        ttmacroTxt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        ttmacroTxt.setText(preferenceStore.getString(PreferenceConstants.TTPMACRO_EXE));
        Button ttmacroBtn = new Button(composite, SWT.NULL);
        ttmacroBtn.setText("�Q��");
        ttmacroBtn.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) {
            }

            public void widgetSelected(SelectionEvent e) {
                FileDialog dialog = new FileDialog(getShell());
                dialog.setText("TeraTerm�}�N��(ttpmacro.exe)���w�肵�Ă��������B");
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
        dirGrp.setText("��Ɨ̈�");

        this.textList = new ArrayList<Text>();

        // ========== ���[�N�f�B���N�g���̏ꏊ ========== //
        new Label(dirGrp, SWT.LEFT).setText("���[�N�f�B���N�g���F");
        workDirTxt = new Text(dirGrp, SWT.BORDER);
        workDirTxt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        workDirTxt.setText(preferenceStore.getString(PreferenceConstants.WORK_DIR));
        this.textList.add(workDirTxt);
        Button workDirBtn = new Button(dirGrp, SWT.NULL);
        workDirBtn.setText("�Q��");
        workDirBtn.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) {
            }

            public void widgetSelected(SelectionEvent e) {
                String dir = dirDialogOpen("���[�N�f�B���N�g�����w�肵�Ă��������B", workDirTxt.getText());
                if (dir != null) {
                    workDirTxt.setText(dir);
                }
            }
        });

        // ========== ���O�f�B���N�g���̏ꏊ ========== //
        new Label(dirGrp, SWT.LEFT).setText("���O�f�B���N�g���F");
        logDirTxt = new Text(dirGrp, SWT.BORDER);
        logDirTxt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        logDirTxt.setText(preferenceStore.getString(PreferenceConstants.LOG_DIR));
        this.textList.add(logDirTxt);
        Button logDirBtn = new Button(dirGrp, SWT.NULL);
        logDirBtn.setText("�Q��");
        logDirBtn.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) {
            }

            public void widgetSelected(SelectionEvent e) {
                String dir = dirDialogOpen("���O�f�B���N�g��(Local)���w�肵�Ă��������B", logDirTxt.getText());
                if (dir != null) {
                    logDirTxt.setText(dir);
                }
            }
        });

        // ========== INI�t�@�C���f�B���N�g���̏ꏊ ========== //
        new Label(dirGrp, SWT.LEFT).setText("INI�t�@�C���f�B���N�g���F");
        iniFileDirTxt = new Text(dirGrp, SWT.BORDER);
        iniFileDirTxt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        iniFileDirTxt.setText(preferenceStore.getString(PreferenceConstants.INIFILE_DIR));
        this.textList.add(iniFileDirTxt);
        Button iniDirBtn = new Button(dirGrp, SWT.NULL);
        iniDirBtn.setText("�Q��");
        iniDirBtn.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) {
            }

            public void widgetSelected(SelectionEvent e) {
                String dir = dirDialogOpen("INI�t�@�C�����u����Ă���f�B���N�g�����w�肵�Ă��������B", iniFileDirTxt.getText());
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
        mkDirBtn.setText("�f�B���N�g�����쐬����");
        mkDirBtn.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) {
            }

            public void widgetSelected(SelectionEvent e) {
                // TODO ���Ƀf�B���N�g�������݂���ꍇ�͌x�����o���Ȃǂׂ̍����Ή����ǉ��������B
                // ���͂Ƃ肠������邾���ł��B
                int mkNum = 0;
                for (Text text : textList) {
                    if (!text.getText().isEmpty()) {
                        File dir = new File(text.getText());
                        dir.mkdirs();
                        mkNum++;
                    }
                }
                if (mkNum > 0) {
                    MessageDialog.openInformation(composite.getShell(), "�f�B���N�g���쐬", "�f�B���N�g�����쐬���܂����B");
                } else {
                    MessageDialog.openWarning(composite.getShell(), "�f�B���N�g���쐬", "�f�B���N�g���쐬�Ώۂ��ݒ肳��Ă��܂���B");
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
