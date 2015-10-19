package jp.co.tabocom.teratermstation.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;

import jp.co.tabocom.teratermstation.Main;

public class ConnToolTabFolder extends TabFolder {

    Main main;

    public ConnToolTabFolder(Composite composite, Main main) {
        super(composite, SWT.NONE);
        this.main = main;
    }

    public Main getMain() {
        return main;
    }

    @Override
    protected void checkSubclass() {
        // super.checkSubclass();
    }
}
