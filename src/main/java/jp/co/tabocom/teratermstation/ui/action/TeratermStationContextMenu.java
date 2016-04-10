package jp.co.tabocom.teratermstation.ui.action;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.ToolTip;

public class TeratermStationContextMenu {
    private String text;
    private ToolTip toolTip;
    private Image image;

    private List<TeratermStationDnDAction> actionList;

    public TeratermStationContextMenu() {
        this.actionList = new ArrayList<TeratermStationDnDAction>();
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

    public void addAction(TeratermStationDnDAction action) {
        this.actionList.add(action);
    }

    public List<TeratermStationDnDAction> getActionList() {
        return actionList;
    }

    public boolean isSubMenu() {
        if (this.text != null && !this.text.isEmpty()) {
            return true;
        }
        return false;
    }
}
