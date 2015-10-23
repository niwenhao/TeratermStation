package jp.co.tabocom.teratermstation.plugin;

import java.util.List;

import jp.co.tabocom.teratermstation.model.TargetNode;
import jp.co.tabocom.teratermstation.ui.action.TeratermStationAction;
import jp.co.tabocom.teratermstation.ui.action.TeratermStationBulkAction;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;

public interface TeratermStationPlugin {
    public List<TeratermStationAction> getActions(TargetNode node, Shell shell, ISelectionProvider selectionProvider);

    public List<TeratermStationBulkAction> getBulkActions(List<TargetNode> nodeList, Shell shell);

    public PreferencePage getPreferencePage();
}
