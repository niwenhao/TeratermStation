/*
 * MIT License
 * Copyright (c) 2015-2019 Tabocom
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package jp.co.tabocom.teratermstation.ui.action;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.ToolTip;

/**
 * 右クリックメニューやドラッグアンドドロップメニューのアクションメニューを管理するクラスです。<br>
 * textを設定すればサブメニューとして機能します。textが設定されていない場合は単にアクションリストの保持クラスです。<br>
 * 
 * @author turbou
 *
 */
public class TeratermStationContextMenu {

    /**
     * サブメニューのタイトル<br>
     * サブメニューとして機能させる場合に必要
     */
    private String text;
    /**
     * サブメニューのツールチップ（optional）
     */
    private ToolTip toolTip;
    /**
     * サブメニューのアイコン（optional）
     */
    private Image image;
    /**
     * アクションリスト
     */
    private List<TeratermStationAction> actionList;

    /**
     * デフォルトコンストラクタ
     */
    public TeratermStationContextMenu() {
        this.actionList = new ArrayList<TeratermStationAction>();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ToolTip getToolTip() {
        return toolTip;
    }

    public void setToolTip(ToolTip toolTip) {
        this.toolTip = toolTip;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public void addAction(TeratermStationAction action) {
        this.actionList.add(action);
    }

    public List<TeratermStationAction> getActionList() {
        return actionList;
    }

    /**
     * このクラスがサブメニューとして機能させるかを返します。<br>
     * 
     * @return サブメニューとして機能する場合はtrue、そうでない場合はfalse
     */
    public boolean isSubMenu() {
        // タイトル（text）が有効であればサブメニューとして機能させます。
        if (this.text != null && !this.text.isEmpty()) {
            return true;
        }
        return false;
    }
}
