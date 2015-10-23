package jp.co.tabocom.teratermstation.ui.action;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.actions.SelectionListenerAction;

import jp.co.tabocom.teratermstation.Main;
import jp.co.tabocom.teratermstation.model.TargetNode;
import jp.co.tabocom.teratermstation.plugin.TeratermStationPlugin;

public class TreeViewActionGroup extends ActionGroup {

    private Shell shell;
    private ISelectionProvider selectionProvider;

    public TreeViewActionGroup(Shell shell, ISelectionProvider selectionProvider) {
        this.shell = shell;
        this.selectionProvider = selectionProvider;
    }

    protected void addToMenu(IMenuManager menu, SelectionListenerAction action) {
        action.selectionChanged(getStructuredSelection());
        menu.add(action);
    }

    @Override
    public void fillContextMenu(IMenuManager menu) {
        IStructuredSelection selection = getStructuredSelection();
        TargetNode node = (TargetNode) selection.getFirstElement();
        Main main = (Main) this.shell.getData("main");

        for (TeratermStationPlugin plugin : main.getToolDefine().getNodePluginList()) {
            menu.add(new Separator());
            for (TeratermStationAction action : plugin.getActions(node, shell, selectionProvider)) {
                if (action.isValid()) {
                    addToMenu(menu, action);
                }
            }
        }
    }

    protected IStructuredSelection getStructuredSelection() {
        return (IStructuredSelection) getContext().getSelection();
    }
}
