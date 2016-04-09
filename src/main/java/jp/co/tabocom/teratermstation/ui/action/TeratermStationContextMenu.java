package jp.co.tabocom.teratermstation.ui.action;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Image;

public class TeratermStationContextMenu {
    private String text;
    private Image image;

    private List<TeratermStationActionInterface> actionList;

    public TeratermStationContextMenu() {
        this.actionList = new ArrayList<TeratermStationActionInterface>();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public void addAction(TeratermStationActionInterface action) {
        this.actionList.add(action);
    }

    public List<TeratermStationActionInterface> getActionList() {
        return actionList;
    }

    public boolean isSubMenu() {
        if (this.text != null && !this.text.isEmpty()) {
            return true;
        }
        return false;
    }
}
