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

package jp.co.tabocom.teratermstation.ui.action;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.ToolTip;

import jp.co.tabocom.teratermstation.TeratermStationShell;
import jp.co.tabocom.teratermstation.model.TargetNode;

/**
 * TeratermStationの右クリックメニューに追加するアクションはこのクラスを継承します。
 * 
 * @author turbou
 * 
 */
public abstract class TeratermStationAction {

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
    protected TeratermStationShell shell;

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
    protected TeratermStationAction(String text, String icon, TargetNode[] nodes, Object value, TeratermStationShell shell) {
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
