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

    /**
     * アクションのタイトル
     */
    protected String text;
    /**
     * アクションのアイコン
     */
    protected Image image;
    /**
     * ノード配列<br>
     * 右クリックやドラッグアンドドロップの場合は基本サイズ1の配列
     */
    protected TargetNode[] nodes;
    /**
     * 渡されるバリュー<br>
     * 今のところドラッグアンドドロップで渡されるファイルパスの配列
     */
    protected Object value;
    /**
     * 画面を作る際のベースクラス
     */
    protected Shell shell;

    /**
     * デフォルトコンストラクタ
     * 
     * @param text
     *            サーバグループ、サーバノードにファイルがドロップされた時に表示されるポップアップメニュー内のメニュータイトル
     * @param icon
     *            メニューにアイコンを表示する場合のアイコンファイル名<br>
     *            必要ない場合はnullでよい。
     * @param nodes
     *            右クリックメニューを表示したノード（サーバグループやサーバ）の配列
     * @param value
     *            引き渡されるバリュー
     * @param shell
     *            画面作成で必要となるベース
     */
    protected TeratermStationDnDAction(String text, String icon, TargetNode[] nodes, Object value, Shell shell) {
        this.text = text;
        if (icon != null && !icon.isEmpty()) {
            this.image = new Image(shell.getDisplay(), getClass().getClassLoader().getResourceAsStream(icon));
        }
        this.nodes = nodes;
        this.value = value;
        this.shell = shell;
    }

    public String getText() {
        return text;
    }

    public abstract ToolTip getToolTip();

    public Image getImage() {
        return image;
    }

    public abstract void run();

}
