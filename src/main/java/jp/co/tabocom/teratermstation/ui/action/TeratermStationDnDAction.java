package jp.co.tabocom.teratermstation.ui.action;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolTip;

import jp.co.tabocom.teratermstation.model.TargetNode;

/**
 * TeratermStationの右クリックメニューに追加するアクションはこのクラスを継承します。
 * 
 * @author turbou
 * 
 */
public abstract class TeratermStationDnDAction {

    protected String text;
    protected Image image;
    protected TargetNode[] nodes;
    protected Object value;

    protected Shell shell;

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
    protected TeratermStationDnDAction(String text, String icon, TargetNode[] nodes, Object value, Shell shell) {
        this.text = text;
        this.nodes = nodes;
        this.value = value;
        this.shell = shell;
        if (icon != null && !icon.isEmpty()) {
            this.image = new Image(shell.getDisplay(), getClass().getClassLoader().getResourceAsStream(icon));
        }
    }

    /*
     * (非 Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    public abstract void run();

    public String getText() {
        return text;
    }

    public abstract ToolTip getToolTip();

    public Image getImage() {
        return image;
    }

}
