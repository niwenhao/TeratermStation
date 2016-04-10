package jp.co.tabocom.teratermstation.ui.action;

import jp.co.tabocom.teratermstation.model.TargetNode;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

/**
 * TeratermStationの右クリックメニューに追加するアクションはこのクラスを継承します。
 * 
 * @author turbou
 * 
 */
public abstract class TeratermStationDnDAction implements TeratermStationActionInterface {

    protected String text;
    protected Image image;
    protected TargetNode[] nodes;
    protected String[] files;
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
