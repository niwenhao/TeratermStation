package jp.co.tabocom.teratermstation.plugin;

import java.util.List;

import jp.co.tabocom.teratermstation.model.TargetNode;
import jp.co.tabocom.teratermstation.ui.action.TeraTermStationAction;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;

public interface TeraTermStationPlugin {
    public List<TeraTermStationAction> getActions(TargetNode node, Shell shell, ISelectionProvider selectionProvider);

    public PreferencePage getPreferencePage();
}
