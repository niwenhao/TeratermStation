package jp.co.tabocom.teratermstation.plugin;

import java.util.List;
import java.util.Map;

import jp.co.tabocom.teratermstation.model.TargetNode;
import jp.co.tabocom.teratermstation.ui.action.TeratermStationAction;
import jp.co.tabocom.teratermstation.ui.action.TeratermStationBulkAction;
import jp.co.tabocom.teratermstation.ui.action.TeratermStationDnDAction;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;

/**
 * TeratermStationを機能拡張するプラグインを作成する際には、このインターフェイスを継承します。<br>
 * プラグインはjarの形式としてTeratermStationに読み込ませますが、MANIFEST.MFにはこのインターフェイスを継承したクラスを<br>
 * 下記のように記述します。<br>
 * Plugin-Class: jp.co.xxx.plugin.oyoyo.OyoyoPlugin
 * 
 * @author turbou
 * 
 */
public interface TeratermStationPlugin {

    /**
     * Pluginロード後の初期処理です。
     */
    public void initialize() throws Exception;

    /**
     * TeratermStation終了時の処理です。
     * 
     * @param preferenceStore
     */
    public void teminate(PreferenceStore preferenceStore) throws Exception;

    /**
     * TeratermStationでサーバグループやサーバの右クリックメニューに追加するサブメニューの配列を返します。<br>
     * 右クリックメニューに追加するサブメニューがない場合はnullを返します。
     * 
     * @param node
     *            選択されているノード（サーバグループかサーバ）
     * @param shell
     *            画面作成で必要となるベースです。これを使用してウインドウやダイアログを生成できます。
     * @param selectionProvider
     * @return MenuManagerの配列<br>
     * @see TeratermStationAction
     */
    public List<MenuManager> getSubmenus(TargetNode node, Shell shell, ISelectionProvider selectionProvider);

    /**
     * TeratermStationでサーバグループやサーバの右クリックメニューに追加するアクションの配列を返します。<br>
     * 右クリックメニューに追加するアクションがない場合はnullを返します。
     * 
     * @param node
     *            選択されているノード（サーバグループかサーバ）
     * @param shell
     *            画面作成で必要となるベースです。これを使用してウインドウやダイアログを生成できます。
     * @param selectionProvider
     * @return TeratermStationActionの配列<br>
     * @see TeratermStationAction
     */
    public List<TeratermStationAction> getActions(TargetNode node, Shell shell, ISelectionProvider selectionProvider);

    /**
     * TeratermStationで一括接続時に利用できる拡張機能の配列を返します。<br>
     * 一括接続時に利用できる拡張機能がない場合はnullを返します。
     * 
     * @param nodeList
     *            チェックボックスにチェックの入っているノード（サーバグループかサーバ）の配列
     * @param shell
     *            画面作成で必要となるベースです。これを使用してウインドウやダイアログを生成できます。
     * @return TeratermStationBulkActionの配列
     * @see TeratermStationBulkAction
     */
    public List<TeratermStationBulkAction> getBulkActions(List<TargetNode> nodeList, Shell shell);

    public Map<MenuDisplay, Menu> getDnDSubmenus(TargetNode node, String[] files, Shell shell);

    public List<TeratermStationDnDAction> getDnDActions(TargetNode node, String[] files, Shell shell);

    /**
     * プラグインで設定ページを使用する場合に設定ページを返します。<br>
     * 設定ページが必要ない場合はnullを返します。
     * 
     * @return　設定ページ
     * @see PreferencePage
     */
    public PreferencePage getPreferencePage();
    
    public class MenuDisplay {
        public String text;
        public Image image;
    }
}
