package jp.co.tabocom.teratermstation.ui.action;

import java.util.List;

import jp.co.tabocom.teratermstation.Main;
import jp.co.tabocom.teratermstation.model.TargetNode;
import jp.co.tabocom.teratermstation.plugin.TeratermStationPlugin;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.actions.SelectionListenerAction;

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

        for (TeratermStationPlugin plugin : main.getToolDefine().getPluginList()) {
            List<TeratermStationAction> actionList = plugin.getActions(node, shell, selectionProvider);
            if (actionList != null) { // 拡張機能の無いプラグインはnullを返すので.
                menu.add(new Separator());
                for (TeratermStationAction action : actionList) {
                    if (action.isValid()) {
                        addToMenu(menu, action);
                    }
                }
            }
        }
    }

    protected IStructuredSelection getStructuredSelection() {
        return (IStructuredSelection) getContext().getSelection();
    }
}
