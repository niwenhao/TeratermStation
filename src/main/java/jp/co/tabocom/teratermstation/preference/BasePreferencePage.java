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
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

public class BasePreferencePage extends PreferencePage {

    private Text dirTxt;

    public BasePreferencePage() {
        super("基本設定");
    }

    @Override
    protected Control createContents(Composite parent) {
        final Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(3, false));
        IPreferenceStore preferenceStore = getPreferenceStore();
        Transfer[] types = new Transfer[] { FileTransfer.getInstance() };

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
        DropTarget dropTarget = new DropTarget(dirTxt, DND.DROP_DEFAULT | DND.DROP_MOVE | DND.DROP_COPY);
        dropTarget.setTransfer(types);
        dropTarget.addDropListener(new DropTargetAdapter() {
            @Override
            public void dragEnter(DropTargetEvent event) {
                if (event.detail == DND.DROP_DEFAULT) {
                    event.detail = DND.DROP_MOVE;
                }
            }

            @Override
            public void drop(DropTargetEvent event) {
                String[] files = (String[]) event.data;
                File dir = new File(files[0]);
                if (dir.isDirectory()) {
                    dirTxt.setText(files[0]);
                } else {
                    MessageDialog.openError(composite.getShell(), "基本設定", "ディレクトリを指定してください。");
                }
            }
        });

        LabelProvider labelProvider = new LabelProvider();
        ITreeContentProvider contentProvider = new ITreeContentProvider() {
            public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            }

            public void dispose() {
            }

            public Object[] getElements(Object inputElement) {
                return null;
            }

            public boolean hasChildren(Object element) {
                return false;
            }

            public Object getParent(Object element) {
                return null;
            }

            public Object[] getChildren(Object parentElement) {
                return null;
            }
        };

        Table table = new Table(composite, SWT.CHECK | SWT.BORDER);
        GridData tableGrDt = new GridData(GridData.FILL_BOTH);
        tableGrDt.horizontalSpan = 2;
        table.setLayoutData(tableGrDt);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        CheckboxTableViewer viewer = new CheckboxTableViewer(table);
        viewer.setLabelProvider(labelProvider);
        viewer.setContentProvider(contentProvider);

        Composite buttonGrp = new Composite(composite, SWT.NONE);
        buttonGrp.setLayoutData(new GridData(GridData.FILL_VERTICAL));
        
        Button restartBtn = new Button(composite, SWT.NULL);
        GridData restartBtnGrDt = new GridData();
        restartBtnGrDt.horizontalSpan = 3;
        restartBtnGrDt.horizontalAlignment = GridData.END;
        restartBtn.setLayoutData(restartBtnGrDt);
        restartBtn.setText("再起動");
        restartBtn.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) {
            }

            public void widgetSelected(SelectionEvent event) {
                if (!performOk()) {
                    return;
                }
                Logger logger = Logger.getLogger("conntool");
                String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
                File currentExecuteFile;
                try {
                    currentExecuteFile = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
                    logger.info(String.format("Restart: %s", currentExecuteFile));
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
