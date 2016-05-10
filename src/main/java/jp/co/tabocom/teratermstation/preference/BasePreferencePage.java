package jp.co.tabocom.teratermstation.preference;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import jp.co.tabocom.teratermstation.Main;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
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
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class BasePreferencePage extends PreferencePage {

    private Text dirTxt;
    List<String> valueList = new ArrayList<String>();
    CheckboxTableViewer viewer;

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

        valueList.add("C:\\Users\\turbou\\Desktop\\Total1");
        valueList.add("C:\\Users\\turbou\\Desktop\\Total2");
        valueList.add("C:\\Users\\turbou\\Desktop\\Total3");

        final Table table = new Table(composite, SWT.CHECK | SWT.BORDER);
        GridData tableGrDt = new GridData(GridData.FILL_BOTH);
        tableGrDt.horizontalSpan = 2;
        table.setLayoutData(tableGrDt);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        viewer = new CheckboxTableViewer(table);
        viewer.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                return element.toString();
            }
        });
        viewer.setContentProvider(new ArrayContentProvider());
        viewer.setInput(valueList);
        viewer.setCheckedElements(valueList.toArray());

        TableLayout layout = new TableLayout();
        table.setLayout(layout);
        ColumnLayoutData layoutData = new ColumnWeightData(100);
        TableColumn column = new TableColumn(table, SWT.NONE, 0);
        layout.addColumnData(layoutData);
        column.setResizable(layoutData.resizable);
        column.setText("定義基点ディレクトリ");

        Composite buttonGrp = new Composite(composite, SWT.NONE);
        buttonGrp.setLayoutData(new GridData(GridData.FILL_VERTICAL));
        buttonGrp.setLayout(new GridLayout(1, true));

        final Button addBtn = new Button(buttonGrp, SWT.NULL);
        addBtn.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        addBtn.setText("新規...");
        addBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                PathDialog pathDialog = new PathDialog(getShell(), "");
                int result = pathDialog.open();
                if (IDialogConstants.OK_ID != result) {
                    return;
                }
                valueList.add(pathDialog.getDirPath());
                viewer.refresh();
            }
        });

        final Button chgBtn = new Button(buttonGrp, SWT.NULL);
        chgBtn.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        chgBtn.setText("編集...");
        chgBtn.setEnabled(false);
        chgBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                int index = table.getSelectionIndex();
                TableItem[] items = table.getSelection();
                PathDialog pathDialog = new PathDialog(getShell(), items[0].getText());
                int result = pathDialog.open();
                if (IDialogConstants.OK_ID != result) {
                    return;
                }
                valueList.remove(index);
                valueList.add(index, pathDialog.getDirPath());
                viewer.refresh();
            }
        });

        final Button rmvBtn = new Button(buttonGrp, SWT.NULL);
        rmvBtn.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        rmvBtn.setText("削除");
        rmvBtn.setEnabled(false);
        rmvBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                int index = table.getSelectionIndex();
                valueList.remove(index);
                viewer.refresh();
            }
        });

        final Button upBtn = new Button(buttonGrp, SWT.NULL);
        upBtn.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        upBtn.setText("上へ移動");
        upBtn.setEnabled(false);
        upBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                int index = table.getSelectionIndex();
                if (index > 0) {
                    TableItem[] items = table.getSelection();
                    valueList.remove(index);
                    index--;
                    valueList.add(index, items[0].getText());
                    viewer.refresh();
                }
            }
        });

        final Button downBtn = new Button(buttonGrp, SWT.NULL);
        downBtn.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        downBtn.setText("下へ移動");
        downBtn.setEnabled(false);
        downBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                int index = table.getSelectionIndex();
                if (index < valueList.size()) {
                    TableItem[] items = table.getSelection();
                    valueList.remove(index);
                    index++;
                    valueList.add(index, items[0].getText());
                    viewer.refresh();
                }
            }
        });

        table.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                chgBtn.setEnabled(true);
                rmvBtn.setEnabled(true);
                upBtn.setEnabled(true);
                downBtn.setEnabled(true);
            }
        });

        Button restartBtn = new Button(composite, SWT.NULL);
        GridData restartBtnGrDt = new GridData();
        restartBtnGrDt.horizontalSpan = 3;
        restartBtnGrDt.horizontalAlignment = GridData.BEGINNING;
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
        for (Object obj : viewer.getCheckedElements()) {
            System.out.println(obj);
        }
        return true;
    }
}
