/*
 * MIT License
 *　Copyright (c) 2015-2019 Tabocom
 *
 *　Permission is hereby granted, free of charge, to any person obtaining a copy
 *　of this software and associated documentation files (the "Software"), to deal
 *　in the Software without restriction, including without limitation the rights
 *　to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *　copies of the Software, and to permit persons to whom the Software is
 *　furnished to do so, subject to the following conditions:
 *
 *　The above copyright notice and this permission notice shall be included in all
 *　copies or substantial portions of the Software.
 *
 *　THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *　IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *　FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *　AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *　LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *　OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *　SOFTWARE.
 */

package jp.co.tabocom.teratermstation.plugin;

import java.util.List;

import jp.co.tabocom.teratermstation.TeratermStationShell;
import jp.co.tabocom.teratermstation.model.TargetNode;
import jp.co.tabocom.teratermstation.ui.action.TeratermStationContextMenu;
import jp.co.tabocom.teratermstation.ui.action.TeratermStationAction;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.preference.PreferenceStore;

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
     * TeratermStationでサーバグループやサーバの右クリックメニューに追加するアクションの配列を返します。<br>
     * 右クリックメニューに追加するアクションがない場合はnullを返します。
     * 
     * @param node
     *            選択されているノード（サーバグループかサーバ）
     * @param shell
     *            画面作成で必要となるベースです。これを使用してウインドウやダイアログを生成できます。
     * @param selectionProvider
     * @return TeratermStationContextMenuの配列<br>
     * @see TeratermStationContextMenu
     */
    public List<TeratermStationContextMenu> getActions(TargetNode[] nodes, TeratermStationShell shell);

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
    public List<TeratermStationAction> getBulkActions(TargetNode[] nodes, TeratermStationShell shell);

    /**
     * TeratermStationでノードにファイルがドラッグアンドドロップされた時のポップアップのアクションを返します。<br>
     * アクションが不要な場合はnullを返します。
     * 
     * @param node
     *            ファイルをドロップされたノード（サーバグループかサーバ）
     * 
     * @param files
     *            ドロップされたファイル(ファイルパス)の配列
     * @param shell
     *            画面作成で必要となるベースです。これを使用してウインドウやダイアログを生成できます。
     * @return サブメニュー名とTeratermStationDnDAction配列のマップオブジェクト<br>
     *         サブメニューが必要ない場合はKeyに""の長さ0の文字列を設定してください。
     * @see TeratermStationAction
     */
    public List<TeratermStationContextMenu> getDnDActions(TargetNode[] nodes, Object value, TeratermStationShell shell);

    /**
     * プラグインで設定ページを使用する場合に設定ページを返します。<br>
     * 設定ページが必要ない場合はnullを返します。
     * 
     * @return 設定ページ
     * @see PreferencePage
     */
    public PreferencePage getPreferencePage();

}
