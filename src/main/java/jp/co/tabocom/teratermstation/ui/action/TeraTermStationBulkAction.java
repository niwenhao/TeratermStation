package jp.co.tabocom.teratermstation.ui.action;

import java.util.List;

import org.eclipse.swt.widgets.Shell;

import jp.co.tabocom.teratermstation.model.TargetNode;

public abstract class TeraTermStationBulkAction {

	protected Shell shell;
	protected List<TargetNode> nodeList;

	protected TeraTermStationBulkAction(List<TargetNode> nodeList, Shell shell) {
		this.shell = shell;
		this.nodeList = nodeList;
	}

	public abstract boolean isValid();

	public abstract void run();
}
