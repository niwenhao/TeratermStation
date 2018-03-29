package jp.co.tabocom.teratermstation.preference;

import jp.co.tabocom.teratermstation.Main;

import java.awt.Desktop;
import java.net.URI;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;

public class AboutPage extends PreferencePage {

    public AboutPage() {
        super("TeratermStationについて");
    }

    @Override
    protected Control createContents(Composite parent) {

        GridLayout parentGrLt = new GridLayout(1, false);
        parentGrLt.verticalSpacing = 20;
        parent.setLayout(parentGrLt);

        Composite appGrp = new Composite(parent, SWT.NONE);
        appGrp.setLayout(new GridLayout(3, false));
        GridData appGrpGrDt = new GridData(GridData.FILL_HORIZONTAL);
        appGrp.setLayoutData(appGrpGrDt);

        Label icon = new Label(appGrp, SWT.NONE);
        GridData iconGrDt = new GridData();
        iconGrDt.verticalSpan = 3;
        iconGrDt.widthHint = 70;
        icon.setLayoutData(iconGrDt);
        Image iconImg = new Image(parent.getDisplay(), Main.class.getClassLoader().getResourceAsStream("icon48.png"));
        icon.setImage(iconImg);

        Label versionTitleLbl = new Label(appGrp, SWT.NONE);
        GridData versionTitleLblGrDt = new GridData();
        versionTitleLblGrDt.widthHint = 100;
        versionTitleLbl.setLayoutData(versionTitleLblGrDt);
        versionTitleLbl.setText("Version:");
        Label versionValueLbl = new Label(appGrp, SWT.NONE);
        GridData versionValueLblGrDt = new GridData();
        versionValueLbl.setLayoutData(versionValueLblGrDt);
        versionValueLbl.setText("2.2.0");

        Label copyrightLbl = new Label(appGrp, SWT.NONE);
        GridData copyrightLblGrDt = new GridData();
        copyrightLblGrDt.horizontalSpan = 2;
        copyrightLbl.setLayoutData(copyrightLblGrDt);
        copyrightLbl.setText("Copyright (C) 2015 - 2018 Tabocom All Rights Reserved.");

        Link urlLbl = new Link(appGrp, SWT.NONE);
        GridData urlLblGrDt = new GridData();
        urlLblGrDt.horizontalSpan = 2;
        urlLbl.setLayoutData(urlLblGrDt);
        urlLbl.setText("<a>https://github.com/turbou/TeratermStation</a>\r\n- <a>wiki</a>\r\n- <a>releases</a>");
        urlLbl.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                String url = "https://github.com/turbou/TeratermStation";
                if (event.text.equals("wiki")) {
                    url = "https://github.com/turbou/TeratermStation/wiki";
                } else if (event.text.equals("releases")) {
                    url = "https://github.com/turbou/TeratermStation/releases";
                }
                Desktop desktop = Desktop.getDesktop();
                try {
                    URI uri = new URI(url);
                    desktop.browse(uri);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });

        Composite licenseGrp = new Composite(parent, SWT.NONE);
        GridLayout licenseGrpGrLt = new GridLayout(1, false);
        licenseGrp.setLayout(licenseGrpGrLt);
        GridData licenseGroupGrDt = new GridData(GridData.FILL_BOTH);
        licenseGrp.setLayoutData(licenseGroupGrDt);

        Label licenseLbl = new Label(licenseGrp, SWT.NONE);
        licenseLbl.setText("This software includes the work that is distributed in the Apache License 2.0");

        Label snakeYamlLbl = new Label(licenseGrp, SWT.NONE);
        snakeYamlLbl.setText("- SnakeYAML");

        noDefaultAndApplyButton();
        return parent;
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
