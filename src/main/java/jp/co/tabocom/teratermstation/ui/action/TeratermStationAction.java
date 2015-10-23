package jp.co.tabocom.teratermstation.ui.action;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.actions.SelectionListenerAction;

import jp.co.tabocom.teratermstation.model.TargetNode;

public abstract class TeratermStationAction extends SelectionListenerAction {

    protected Shell shell;
    protected TargetNode node;

    protected TeratermStationAction(String text, String icon, TargetNode node, Shell shell, ISelectionProvider selectionProvider) {
        super(text);
        this.shell = shell;
        this.node = node;
        selectionProvider.addSelectionChangedListener(this);
        if (icon != null && !icon.isEmpty()) {
            Image image = new Image(shell.getDisplay(), getClass().getClassLoader().getResourceAsStream(icon));
            setImageDescriptor(ImageDescriptor.createFromImage(image));
        }
    }

    public abstract boolean isValid();

    public abstract void run();
}
