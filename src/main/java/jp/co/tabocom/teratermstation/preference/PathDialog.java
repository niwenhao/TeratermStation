package jp.co.tabocom.teratermstation.preference;

import java.io.File;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class PathDialog extends Dialog {

    private Text dirTxt;
    private String dirPath;

    public PathDialog(Shell parentShell, String dirPath) {
        super(parentShell);
        this.dirPath = dirPath;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite) super.createDialogArea(parent);
        composite.setLayout(new GridLayout(3, false));
        new Label(composite, SWT.LEFT).setText("定義基点ディレクトリ：");
        dirTxt = new Text(composite, SWT.BORDER);
        dirTxt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        dirTxt.setText(this.dirPath);
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
        dropTarget.setTransfer(new Transfer[] { FileTransfer.getInstance() });
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
                    MessageDialog.openError(getShell(), "定義基点ディレクトリ", "ディレクトリを指定してください。");
                }
            }
        });

        return composite;
    }

    public String getDirPath() {
        return this.dirPath;
    }

    @Override
    protected void okPressed() {
        this.dirPath = this.dirTxt.getText();
        super.okPressed();
    }

    @Override
    protected Point getInitialSize() {
        return new Point(480, 120);
    }

    @Override
    protected void setShellStyle(int newShellStyle) {
        super.setShellStyle(getShellStyle() | SWT.RESIZE);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("定義基点ディレクトリ");
    }
}
