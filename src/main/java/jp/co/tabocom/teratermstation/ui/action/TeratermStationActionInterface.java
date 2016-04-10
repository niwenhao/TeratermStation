package jp.co.tabocom.teratermstation.ui.action;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.ToolTip;

public interface TeratermStationActionInterface {
    public String getText();

    public ToolTip getToolTip();

    public Image getImage();

    public boolean isValid();

    public void run();
}
