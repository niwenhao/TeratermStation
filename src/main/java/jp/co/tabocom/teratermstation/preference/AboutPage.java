package jp.co.tabocom.teratermstation.preference;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import jp.co.tabocom.teratermstation.Main;

public class AboutPage extends PreferencePage {

    public AboutPage() {
        super("TeratermStationについて");
    }

    @Override
    protected Control createContents(Composite parent) {
        final Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(3, false));
        Label icon = new Label(composite, SWT.NONE);
        GridData iconGrDt = new GridData();
        iconGrDt.verticalSpan = 3;
        iconGrDt.widthHint = 70;
        icon.setLayoutData(iconGrDt);
        Image iconImg = new Image(parent.getDisplay(), Main.class.getClassLoader().getResourceAsStream("icon48.png"));
        icon.setImage(iconImg);
        
        Label versionTitleLbl = new Label(composite, SWT.NONE);
        GridData versionTitleLblGrDt = new GridData();
        versionTitleLblGrDt.widthHint = 100;
        versionTitleLbl.setLayoutData(versionTitleLblGrDt);
        versionTitleLbl.setText("Version:");
        Label versionValueLbl = new Label(composite, SWT.NONE);
        GridData versionValueLblGrDt = new GridData();
        versionValueLbl.setLayoutData(versionValueLblGrDt);
        versionValueLbl.setText("1.0.1");
        
        noDefaultAndApplyButton();
        return composite;
    }

    @Override
    public boolean performOk() {
        IPreferenceStore ps = getPreferenceStore();
        if (ps == null) {
            return true;
        }
        return true;
    }
}
