package jp.co.tabocom.teratermstation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.co.tabocom.teratermstation.exception.FormatException;
import jp.co.tabocom.teratermstation.model.Tab;
import jp.co.tabocom.teratermstation.model.ToolDefinition;
import jp.co.tabocom.teratermstation.plugin.TeratermStationPlugin;
import jp.co.tabocom.teratermstation.preference.AboutPage;
import jp.co.tabocom.teratermstation.preference.BasePreferencePage;
import jp.co.tabocom.teratermstation.preference.PathPreferencePage;
import jp.co.tabocom.teratermstation.preference.PluginPreferencePage;
import jp.co.tabocom.teratermstation.preference.PreferenceConstants;
import jp.co.tabocom.teratermstation.preference.TtlPreferencePage;
import jp.co.tabocom.teratermstation.ui.EnvTabItem;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import com.sun.jna.WString;
import com.sun.jna.platform.win32.DBT;
import com.sun.jna.platform.win32.DBT.DEV_BROADCAST_DEVICEINTERFACE;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HMODULE;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinUser.HDEVNOTIFY;
import com.sun.jna.platform.win32.WinUser.WNDCLASSEX;
import com.sun.jna.platform.win32.WinUser.WindowProc;
import com.sun.jna.platform.win32.Wtsapi32;

public class Main implements PropertyChangeListener, WindowProc {

    public static final String ROOT_DIR = "sample";
    public static final String WINDOW_TITLE = "TeratermStation - %s - %s";

    private Shell shell;

    // 各種定義(xmlから定義をロードしたオブジェクト)
    private ToolDefinition toolDefine;

    private Map<String, EnvTabItem> tabItemMap;

    // タブフォルダ
    private TabFolder tabFolder;

    // TTL生成のみチェックボックス
    private Button onlyTtlGenChkBox;
    // 一括起動ボタン
    private Button bulkExecuteBtn;

    private PreferenceStore preferenceStore;

    // Diff取得識別子に指定できる文字定義
    public static String ACCEPTABLE_CHAR = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_-";

    private String authUsrCache;
    private String authPwdCache;
    private String authUsrGroup;
    private String authPwdGroup;

    private HDEVNOTIFY hDevNotify;
    private HWND hWnd;
    private WString windowClass;
    private HMODULE hInst;

    private String loadDirErrorMsg;
    private String openingMsg;

    private int loginUserIdx = 1;

    /**
     * @param args
     */
    public static void main(String[] args) {
        Main main = new Main();
        main.initialize();
        main.createPart();
    }

    private void win32EventOn() {
        windowClass = new WString("MyWindowClass");
        hInst = Kernel32.INSTANCE.GetModuleHandle("");

        WNDCLASSEX wClass = new WNDCLASSEX();
        wClass.hInstance = hInst;
        wClass.lpfnWndProc = Main.this;
        wClass.lpszClassName = windowClass;

        User32.INSTANCE.RegisterClassEx(wClass);
        getLastError();

        hWnd = User32.INSTANCE.CreateWindowEx(User32.WS_EX_TOPMOST, windowClass, "TeratermStation", 0, 0, 0, 0, 0, null, null, hInst, null);

        getLastError();
        // System.out.println("window created. window hWnd: " + hWnd.getPointer().toString());

        Wtsapi32.INSTANCE.WTSRegisterSessionNotification(hWnd, Wtsapi32.NOTIFY_FOR_THIS_SESSION);

        DEV_BROADCAST_DEVICEINTERFACE notificationFilter = new DEV_BROADCAST_DEVICEINTERFACE();
        notificationFilter.dbcc_size = notificationFilter.size();
        notificationFilter.dbcc_devicetype = DBT.DBT_DEVTYP_DEVICEINTERFACE;
        notificationFilter.dbcc_classguid = DBT.GUID_DEVINTERFACE_USB_DEVICE;

        hDevNotify = User32.INSTANCE.RegisterDeviceNotification(hWnd, notificationFilter, User32.DEVICE_NOTIFY_WINDOW_HANDLE);

        getLastError();
        if (hDevNotify != null) {
            // System.out.println("RegisterDeviceNotification was sucess.");
        }
    }

    /**
     * Gets the last error.
     * 
     * @return the last error
     */
    public int getLastError() {
        int rtn = Kernel32.INSTANCE.GetLastError();
        if (rtn != 0) {
            System.out.println("error: " + rtn);
        }
        return rtn;
    }

    private void win32EventOff() {
        User32.INSTANCE.UnregisterDeviceNotification(hDevNotify);
        Wtsapi32.INSTANCE.WTSUnRegisterSessionNotification(hWnd);
        User32.INSTANCE.UnregisterClass(windowClass, hInst);
        User32.INSTANCE.DestroyWindow(hWnd);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sun.jna.platform.win32.User32.WindowProc#callback(com.sun.jna. platform .win32.WinDef.HWND, int,
     * com.sun.jna.platform.win32.WinDef.WPARAM, com.sun.jna.platform.win32.WinDef.LPARAM)
     */
    public LRESULT callback(HWND hwnd, int uMsg, WPARAM wParam, LPARAM lParam) {
        switch (uMsg) {
            case WinUser.WM_SESSION_CHANGE: {
                this.onSessionChange(wParam, lParam);
                return new LRESULT(0);
            }
            default:
                return User32.INSTANCE.DefWindowProc(hwnd, uMsg, wParam, lParam);
        }
    }

    /**
     * On session change.
     * 
     * @param wParam
     *            the w param
     * @param lParam
     *            the l param
     */
    protected void onSessionChange(WPARAM wParam, LPARAM lParam) {
        switch (wParam.intValue()) {
            case Wtsapi32.WTS_SESSION_LOCK: {
                this.onMachineLocked(lParam.intValue());
                break;
            }
            case Wtsapi32.WTS_SESSION_UNLOCK: {
                this.onMachineUnlocked(lParam.intValue());
                break;
            }
        }
    }

    /**
     * On machine locked.
     * 
     * @param sessionId
     *            the session id
     */
    protected void onMachineLocked(int sessionId) {
        System.out.println("onMachineLocked: " + sessionId);
    }

    /**
     * On machine unlocked.
     * 
     * @param sessionId
     *            the session id
     */
    protected void onMachineUnlocked(int sessionId) {
        System.out.println("onMachineUnlocked: " + sessionId);
        boolean clearFlg = false;
        for (EnvTabItem item : tabItemMap.values()) {
            if (item.isPwdAutoClearFlg()) {
                clearFlg |= item.clearPassword();
            }
        }
        if (clearFlg) {
            // パスワードをクリアした時だけメッセージを出す。
            MessageDialog.openInformation(shell, "セキュリティ対応", "PCロックに伴い、パスワード情報をクリアしました。");
        }
    }

    private void initialize() {
        try {
            String homeDir = System.getProperty("user.home");
            this.preferenceStore = new PreferenceStore(homeDir + "\\teratermstation.properties");
            try {
                this.preferenceStore.load();
            } catch (FileNotFoundException fnfe) {
                this.preferenceStore = new PreferenceStore("teratermstation.properties");
                this.preferenceStore.load();
            }

            // ROOT_DIRの絶対パスを取得しておく
            String rootDirStr = ROOT_DIR;
            File rootDirFile = new File(rootDirStr);
            if (!rootDirFile.isAbsolute()) {
                rootDirStr = rootDirFile.getCanonicalPath();
            }

            String rootDirs = this.preferenceStore.getString(PreferenceConstants.TARGET_DIRS);
            List<String> validDirList = new ArrayList<String>();
            Pattern ptn = Pattern.compile("^<([^<>]+)>$", Pattern.DOTALL);
            if (rootDirs.trim().length() > 0) {
                for (String dirStr : rootDirs.split(",")) {
                    Matcher matcher = ptn.matcher(dirStr);
                    if (matcher.find()) {
                        dirStr = matcher.group(1);
                        File dirFile = new File(dirStr);
                        if (!dirFile.isAbsolute()) {
                            dirStr = dirFile.getCanonicalPath();
                        }
                        validDirList.add(dirStr);
                    }
                }
            } else {
                validDirList.add(rootDirStr);
            }

            // String rootDir = this.preferenceStore.getString(PreferenceConstants.TARGET_DIR);
            // if (rootDir == null || rootDir.isEmpty()) {
            // rootDir = ROOT_DIR;
            // }
            toolDefine = new ToolDefinition(validDirList);
            try {
                toolDefine.initialize();
                for (String rootDir : this.toolDefine.getRootDirList()) {
                    if (!toolDefine.getLoadExceptionList(rootDir).isEmpty()) {
                        StringBuilder builder = new StringBuilder();
                        for (Exception e : toolDefine.getLoadExceptionList(rootDir)) {
                            builder.append(e.getMessage() + "\r\n");
                        }
                        loadDirErrorMsg = builder.toString();
                    }
                }
            } catch (FormatException fe) {
                loadDirErrorMsg = fe.getMessage();
            } catch (Exception e) {
                loadDirErrorMsg = "サーバ定義の読み込みに失敗したため、サンプル定義(sample)でツールを起動します。\r\nご指定のサーバ定義に問題がないか、ご確認ください。";
                toolDefine = new ToolDefinition(Arrays.asList(rootDirStr));
                try {
                    toolDefine.initialize();
                } catch (Exception e1) {
                    e1.printStackTrace();
                    loadDirErrorMsg = "サーバ定義の読み込み、さらにサンプル定義(sample)の読み込みにも失敗しました。。\r\nご指定のサーバ定義に問題がないか、ご確認ください。";
                }
            }
        } catch (FileNotFoundException fnfe) {
            // teratermstation.propertiesがない場合(初回など)は初期値用のプロパティファイルを読み込む.
            try {
                String rootDirStr = ROOT_DIR;
                File rootDirFile = new File(rootDirStr);
                if (!rootDirFile.isAbsolute()) {
                    rootDirStr = rootDirFile.getCanonicalPath();
                }
                toolDefine = new ToolDefinition(Arrays.asList(rootDirStr));
                try {
                    toolDefine.initialize();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            openingMsg = "まず最初に基本設定を行なってください。\r\nデフォルト値と異なる箇所については適宜変更してください。\r\nとくにワーク領域やttpmacro.exeのパスが重要です。";
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        this.preferenceStore.setDefault(PreferenceConstants.WORK_DIR, "work");
        this.preferenceStore.setDefault(PreferenceConstants.LOG_DIR, "log");
        this.preferenceStore.setDefault(PreferenceConstants.INIFILE_DIR, "ini");
    }

    private void createPart() {
        Display display = new Display();
        shell = new Shell(display, SWT.TITLE | SWT.MIN | SWT.MAX | SWT.CLOSE | SWT.RESIZE);
        shell.setData("main", this);
        shell.setText(String.format(WINDOW_TITLE, toolDefine.getSystem(), "Unselected"));
        // アイコンセットアップ
        Image[] imageArray = new Image[5];
        imageArray[0] = new Image(display, Main.class.getClassLoader().getResourceAsStream("icon16.png"));
        imageArray[1] = new Image(display, Main.class.getClassLoader().getResourceAsStream("icon24.png"));
        imageArray[2] = new Image(display, Main.class.getClassLoader().getResourceAsStream("icon32.png"));
        imageArray[3] = new Image(display, Main.class.getClassLoader().getResourceAsStream("icon48.png"));
        imageArray[4] = new Image(display, Main.class.getClassLoader().getResourceAsStream("icon128.png"));
        shell.setImages(imageArray);
        shell.addShellListener(new ShellListener() {
            @Override
            public void shellIconified(ShellEvent event) {
            }

            @Override
            public void shellDeiconified(ShellEvent event) {
            }

            @Override
            public void shellDeactivated(ShellEvent event) {
            }

            @Override
            public void shellClosed(ShellEvent event) {
                for (String rootDir : toolDefine.getRootDirList()) {
                    if (toolDefine.getPluginList(rootDir) != null) {
                        for (TeratermStationPlugin plugin : toolDefine.getPluginList(rootDir)) {
                            try {
                                plugin.getClass().getDeclaredMethod("teminate", PreferenceStore.class);
                                plugin.teminate(preferenceStore);
                            } catch (NoSuchMethodException | SecurityException e) {
                                continue;
                            } catch (Exception e) {
                                MessageDialog.openError(shell, "終了時処理", e.getMessage());
                            }
                        }
                    }
                }
                int idx = tabFolder.getSelectionIndex();
                // 開いていたタブを記憶しておく。
                preferenceStore.setValue(PreferenceConstants.OPENED_TAB_IDX, idx);
                preferenceStore.setValue(PreferenceConstants.MEM_WIDTH, shell.getSize().x);
                preferenceStore.setValue(PreferenceConstants.MEM_HEIGHT, shell.getSize().y);
                try {
                    preferenceStore.save();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }

            @Override
            public void shellActivated(ShellEvent event) {
            }
        });

        Listener listener = new Listener() {
            @Override
            public void handleEvent(Event event) {
                if (event.stateMask == SWT.CTRL) {
                    int num = Character.getNumericValue(event.character);
                    if (num > -1) {
                        loginUserIdx = num;
                        tabItemRefresh();
                    }
                }
            }
        };
        display.addFilter(SWT.KeyUp, listener);

        GridLayout baseLayout = new GridLayout(1, false);
        baseLayout.marginWidth = 10;
        shell.setLayout(baseLayout);

        if (loadDirErrorMsg != null && !loadDirErrorMsg.isEmpty()) {
            MessageDialog.openError(shell, "サーバ定義ロード", loadDirErrorMsg);
        }
        if (openingMsg != null && !openingMsg.isEmpty()) {
            MessageDialog.openInformation(shell, "ご利用ありがとうございます。", openingMsg);
        }

        // Target Group
        tabFolder = new TabFolder(shell, SWT.NONE);
        tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));

        // ==================== ここで各環境のタブを生成しています ==================== //
        this.tabItemMap = new LinkedHashMap<String, EnvTabItem>();
        if (!this.toolDefine.isTabMapEmpty()) {
            for (String rootDir : this.toolDefine.getRootDirList()) {
                List<String> orderList = this.toolDefine.getOrderList(rootDir);
                if (orderList != null && !orderList.isEmpty()) {
                    List<String> keys = new ArrayList<String>(this.toolDefine.getTabMap(rootDir).keySet());
                    Map<String, String> sortMap = new HashMap<String, String>();
                    for (String key : keys) {
                        int idx = orderList.indexOf(key);
                        if (idx > -1) {
                            sortMap.put(String.format("%04d", idx), key);
                        } else {
                            sortMap.put(key, key);
                        }
                    }
                    List<String> idxList = new ArrayList<String>(sortMap.keySet());
                    Collections.sort(idxList);
                    for (String idx : idxList) {
                        String key = sortMap.get(idx);
                        Tab tab = this.toolDefine.getTabMap(rootDir).get(key);
                        tabItemMap.put(key, new EnvTabItem(rootDir, tab, tabFolder));
                    }
                } else {
                    // 挿入順（正確には辞書順）となるように制御
                    List<String> keys = new ArrayList<String>(this.toolDefine.getTabMap(rootDir).keySet());
                    Collections.sort(keys);
                    for (int i = 0; i < keys.size(); i++) {
                        Tab tab = this.toolDefine.getTabMap(rootDir).get(keys.get(i));
                        tabItemMap.put(keys.get(i), new EnvTabItem(rootDir, tab, tabFolder));
                    }
                    // 下は逆順とする場合
                    // for (int i = keys.size() - 1; i >= 0; i--) {
                    // Tab tab = this.toolDefine.getTabMap().get(keys.get(i));
                    // tabItemMap.put(keys.get(i), new EnvTabItem(tab, tabFolder));
                    // }

                    // これは普通のやりかた
                    // for (String key : toolDefine.getTabMap().keySet()) {
                    // Tab tab = toolDefine.getTabMap().get(key);
                    // tabItemMap.put(key, new EnvTabItem(tab, tabFolder));
                    // }
                }
                for (EnvTabItem item : tabItemMap.values()) {
                    item.addPropertyChangeListener(this);
                }
            }
        }
        // ============================================================================
        // //

        int idx = this.preferenceStore.getInt(PreferenceConstants.OPENED_TAB_IDX);
        tabFolder.setSelection(idx);

        tabFolder.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetDefaultSelected(SelectionEvent arg0) {

            }

            @Override
            public void widgetSelected(SelectionEvent event) {
                TabFolder folder = (TabFolder) event.getSource();
                EnvTabItem tabItem = (EnvTabItem) folder.getItem(folder.getSelectionIndex());
                tabItem.propertyChangeUpdate();
                tabItem.setAuthUsrPwdText(authUsrGroup, authUsrCache, authPwdGroup, authPwdCache);
            }
        });

        // Execute Group
        Composite executeGrp = new Composite(shell, SWT.NULL);
        GridLayout executeGrpLt = new GridLayout(5, true);
        executeGrpLt.horizontalSpacing = 10;
        executeGrpLt.verticalSpacing = 5;
        executeGrp.setLayout(executeGrpLt);
        GridData executeGrpGrDt = new GridData(GridData.FILL_HORIZONTAL);
        executeGrp.setLayoutData(executeGrpGrDt);
        // executeGrp.setBackground(display.getSystemColor(SWT.COLOR_BLUE));

        // ========== 管理用グループ ==========
        Group adminGrp = new Group(executeGrp, SWT.NONE);
        GridLayout adminGrpLt = new GridLayout(1, false);
        // adminGrpLt.marginLeft = 5;
        // adminGrpLt.marginRight = 5;
        adminGrpLt.verticalSpacing = 5;
        adminGrp.setLayout(adminGrpLt);
        GridData adminGrpGrDt = new GridData(GridData.FILL_BOTH);
        adminGrpGrDt.horizontalSpan = 1;
        adminGrp.setLayoutData(adminGrpGrDt);
        adminGrp.setText("管理");

        // ========== 設定ボタン ==========
        Button settingsBtn = new Button(adminGrp, SWT.PUSH);
        settingsBtn.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        settingsBtn.setText("基本設定");
        settingsBtn.setToolTipText("動作に必要な設定を行います。");
        settingsBtn.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                PreferenceManager mgr = new PreferenceManager();
                PreferenceNode baseNode = new PreferenceNode("base", new BasePreferencePage());
                PreferenceNode pathNode = new PreferenceNode("path", new PathPreferencePage());
                PreferenceNode ttlNode = new PreferenceNode("ttl", new TtlPreferencePage());
                mgr.addToRoot(baseNode);
                mgr.addTo(baseNode.getId(), pathNode);
                mgr.addTo(baseNode.getId(), ttlNode);

                PreferenceNode pluginsNode = new PreferenceNode("plugins", new PluginPreferencePage());
                mgr.addToRoot(pluginsNode);
                for (String rootDir : toolDefine.getRootDirList()) {
                    if (toolDefine.getPluginList(rootDir) != null) {
                        for (TeratermStationPlugin plugin : toolDefine.getPluginList(rootDir)) {
                            try {
                                plugin.getClass().getDeclaredMethod("getPreferencePage");
                            } catch (NoSuchMethodException | SecurityException e) {
                                continue;
                            }
                            if (plugin.getPreferencePage() != null) {
                                PreferenceNode pluginNode = new PreferenceNode(plugin.getClass().getName(), plugin.getPreferencePage());
                                mgr.addTo(pluginsNode.getId(), pluginNode);
                            }
                        }
                    }
                }
                PreferenceNode aboutNode = new PreferenceNode("about", new AboutPage());
                mgr.addToRoot(aboutNode);
                PreferenceDialog dialog = new PreferenceDialog(shell, mgr);
                dialog.setPreferenceStore(preferenceStore);
                dialog.open();
                try {
                    preferenceStore.save();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {
            }
        });

        // ========== TTLのみ作成チェックボックス ==========
        onlyTtlGenChkBox = new Button(adminGrp, SWT.CHECK);
        onlyTtlGenChkBox.setText("TTLのみ生成");
        onlyTtlGenChkBox.setToolTipText("TTLマクロの実行までは行わず、TTLファイルの生成のみ行います。\n事前に処理内容を確認したい場合にチェックを入れて実行してください。");

        // ========== 一括グループ ==========
        Composite bulkGrp = new Composite(executeGrp, SWT.NULL);
        bulkGrp.setLayout(new GridLayout(1, false));
        GridData bulkGrpGrDt = new GridData(GridData.FILL_BOTH);
        bulkGrpGrDt.horizontalSpan = 3;
        // bulkGrpGrDt.widthHint = 100;
        bulkGrp.setLayoutData(bulkGrpGrDt);
        // bulkGrp.setBackground(display.getSystemColor(SWT.COLOR_RED));

        // ========== 一括起動ボタン ==========
        bulkExecuteBtn = new Button(bulkGrp, SWT.PUSH);
        bulkExecuteBtn.setLayoutData(new GridData(GridData.FILL_BOTH));
        bulkExecuteBtn.setText("一括接続");
        bulkExecuteBtn.setToolTipText("対象サーバすべてに一括接続をします。");
        bulkExecuteBtn.setEnabled(false);
        bulkExecuteBtn.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                ((EnvTabItem) tabFolder.getItem(tabFolder.getSelectionIndex())).bulkConnection();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {
            }
        });

        Logger logger = Logger.getLogger("conntool");

        uiUpdate();
        int width = this.preferenceStore.getInt(PreferenceConstants.MEM_WIDTH);
        int height = this.preferenceStore.getInt(PreferenceConstants.MEM_HEIGHT);
        if (width > 0 && height > 0) {
            shell.setSize(width, height);
        } else {
            shell.pack();
        }
        shell.open();
        win32EventOn();
        try {
            while (!shell.isDisposed()) {
                if (!display.readAndDispatch()) {
                    display.sleep();
                }
            }
        } catch (Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            String trace = stringWriter.toString();
            logger.error(trace);
        }
        win32EventOff();
        display.dispose();
    }

    private void uiUpdate() {
        if (tabItemMap.size() > 0) {
            ((EnvTabItem) tabFolder.getItem(tabFolder.getSelectionIndex())).propertyChangeUpdate();
        }
    }

    public PreferenceStore getPreferenceStore() {
        return preferenceStore;
    }

    public boolean isTtlOnly() {
        return this.onlyTtlGenChkBox.getSelection();
    }

    public void tabItemRefresh() {
        for (TabItem item : this.tabFolder.getItems()) {
            ((EnvTabItem) item).refreshTree();
        }
    }

    /**
     * 現在選択されているタブを返します。<br>
     * 
     * @return EnvTabItem 現在選択されているタブ
     */
    public EnvTabItem getCurrentTabItem() {
        return (EnvTabItem) tabFolder.getItem(tabFolder.getSelectionIndex());
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if ("authInput".equals(event.getPropertyName())) {
            Boolean enableFlg = (Boolean) event.getNewValue();
            this.bulkExecuteBtn.setEnabled(enableFlg.booleanValue());
        } else if ("nodeChange".equals(event.getPropertyName())) {
            tabItemRefresh();
        } else if ("authUsr".equals(event.getPropertyName())) {
            String value = (String) event.getNewValue();
            this.authUsrGroup = value.split("/")[0];
            if (value.split("/").length > 1) {
                this.authUsrCache = value.split("/")[1];
            } else {
                this.authUsrCache = "";
            }
        } else if ("authPwd".equals(event.getPropertyName())) {
            String value = (String) event.getNewValue();
            this.authPwdGroup = value.split("/")[0];
            if (value.split("/").length > 1) {
                this.authPwdCache = value.split("/")[1];
            } else {
                this.authPwdCache = "";
            }
        }
    }

    public ToolDefinition getToolDefine() {
        return toolDefine;
    }

    public int getLoginUserIdx() {
        return loginUserIdx;
    }

    public void setWindowTitle(String text) {
        if (text == null || text.isEmpty()) {
            this.shell.setText(String.format(WINDOW_TITLE, toolDefine.getSystem(), "Unselected"));
        } else {
            this.shell.setText(String.format(WINDOW_TITLE, toolDefine.getSystem(), text));
        }
    }
}
