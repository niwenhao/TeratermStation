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
