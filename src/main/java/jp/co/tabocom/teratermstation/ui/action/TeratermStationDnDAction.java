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
public abstract class TeratermStationDnDAction {

    protected String text;
    protected Shell shell;
    protected TargetNode node;
    protected Image image;

    /**
     * デフォルトコンストラクタ
     * 
     * @param text
     *            サーバグループ、サーバノードにファイルがドロップされた時に表示されるポップアップメニュー内のメニュータイトル
     * @param icon
     *            メニューにアイコンを表示する場合のアイコンファイル名<br>
     *            必要ない場合はnullでよい。
     * @param node
     *            右クリックメニューを表示したノード（サーバグループやサーバ）
     * @param shell
     *            画面作成で必要となるベース
     */
    protected TeratermStationDnDAction(String text, String icon, TargetNode node, Shell shell) {
        this.text = text;
        this.shell = shell;
        this.node = node;
        if (icon != null && !icon.isEmpty()) {
            this.image = new Image(shell.getDisplay(), getClass().getClassLoader().getResourceAsStream(icon));
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

    public String getText() {
        return text;
    }

    public Image getImage() {
        return image;
    }

}
