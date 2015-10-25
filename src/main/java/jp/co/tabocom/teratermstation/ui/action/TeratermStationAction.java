package jp.co.tabocom.teratermstation.ui.action;

import jp.co.tabocom.teratermstation.model.TargetNode;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.actions.SelectionListenerAction;

/**
 * TeratermStationの右クリックメニューに追加するアクションはこのクラスを継承します。
 * 
 * @author turbou
 *
 */
public abstract class TeratermStationAction extends SelectionListenerAction {

    protected Shell shell;
    protected TargetNode node;

    /**
     * デフォルトコンストラクタ
     * 
     * @param text
     *            右クリックメニューに表示するアクション名
     * @param icon
     *            メニューにアイコンを表示する場合のアイコンファイル名<br>
     *            必要ない場合はnullでよい。
     * @param node
     *            右クリックメニューを表示したノード（サーバグループやサーバ）
     * @param shell
     *            画面作成で必要となるベース
     * @param selectionProvider
     */
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

    /**
     * 状況に応じて、右クリックメニューにアクションを追加するか否かを返します。
     * 
     * @return 右クリックメニューに追加する場合はtrue、追加しない場合はfalse
     */
    public abstract boolean isValid();

    /*
     * (非 Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    public abstract void run();
}
