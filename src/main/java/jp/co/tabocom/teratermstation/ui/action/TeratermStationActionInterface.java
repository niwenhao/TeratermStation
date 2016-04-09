package jp.co.tabocom.teratermstation.ui.action;

import org.eclipse.swt.graphics.Image;

public interface TeratermStationActionInterface {
    public String getText();

    public Image getImage();

    public boolean isValid();

    public void run();
}
