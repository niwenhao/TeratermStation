package jp.co.tabocom.teratermstation;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class TeratermStationShell extends Shell {
    private Main main;

    public TeratermStationShell(Display display, Main main) {
        super(display, SWT.TITLE | SWT.MIN | SWT.MAX | SWT.CLOSE | SWT.RESIZE);
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
