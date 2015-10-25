package jp.co.tabocom.teratermstation.ui.action;

import java.util.List;

import jp.co.tabocom.teratermstation.model.TargetNode;

import org.eclipse.swt.widgets.Shell;

/**
 * TeratermStationの一括接続時に利用できる拡張機能を提供する場合はこのクラスを継承します。
 * 
 * @author turbou
 *
 */
public abstract class TeratermStationBulkAction {

    protected Shell shell;
    protected List<TargetNode> nodeList;

    /**
     * デフォルトコンストラクタ
     * 
     * @param nodeList
     *            チェックボックスにチェックの入っているノード（サーバグループかサーバ）の配列
     * @param shell
     *            画面作成で必要となるベース
     */
    protected TeratermStationBulkAction(List<TargetNode> nodeList, Shell shell) {
        this.shell = shell;
        this.nodeList = nodeList;
    }

    /**
     * 状況に応じて、拡張機能を提供するか否かを返します。
     * 
     * @return　提供する場合はtrue、提供しない場合はfalse
     */
    public abstract boolean isValid();

    /**
     * メイン処理です。
     */
    public abstract void run();

    /**
     * 拡張機能の名前を返します。
     * 
     * @return 拡張機能の名前
     */
    public abstract String getDisplayName();

    /**
     * 拡張機能の詳細な説明が必要な場合に説明文を返します。<br>
     * 必要に応じてoverrideしてください。
     * 
     * @return 説明文
     */
    public String getDescription() {
        return "";
    }
}
