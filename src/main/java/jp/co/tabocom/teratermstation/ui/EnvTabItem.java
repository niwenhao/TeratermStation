package jp.co.tabocom.teratermstation.ui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jp.co.tabocom.teratermstation.Main;
import jp.co.tabocom.teratermstation.model.Auth;
import jp.co.tabocom.teratermstation.model.Category;
import jp.co.tabocom.teratermstation.model.Tab;
import jp.co.tabocom.teratermstation.model.TargetNode;
import jp.co.tabocom.teratermstation.plugin.TeratermStationPlugin;
import jp.co.tabocom.teratermstation.preference.PreferenceConstants;
import jp.co.tabocom.teratermstation.ui.action.TeratermStationBulkAction;
import jp.co.tabocom.teratermstation.ui.action.TreeViewActionGroup;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.actions.ActionContext;

public class EnvTabItem extends TabItem {

    private static final int BULK_INTERVAL = 1700;
    private static final int FILE_INTERVAL = 300;

    private Map<String, CheckboxTreeViewer> treeMap;
    private Map<String, Category> categoryMap;
    private Map<String, Category> defaultCategoryMap;
    private Text usrTxt;
    private Text pwdTxt;
    private Button idPwdMemoryBtn;
    private Button authCheckBtn;
    private Text filterTxt;
    private Button allCheckBtn;
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    private boolean authInputStatus;
    private boolean authFlg;
    private boolean memoryPwdFlg;
    private boolean pwdAutoClearFlg;
    private String pwdGroup;

    private Tab tab;

    private ServerFilter serverFilter = new ServerFilter();

    public static String ACCEPTABLE_CHAR = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_-";

    /**
     * デフォルトコンストラクタ<br>
     * 
     * @param tabName
     *            タブの名前
     * @param parent
     *            TabFolder 要するに親玉
     */
    public EnvTabItem(Tab tab, TabFolder parent) {
        super(parent, SWT.NONE);
        this.tab = tab;
        this.defaultCategoryMap = new LinkedHashMap<String, Category>();
        Main main = (Main) parent.getShell().getData("main");
        List<String> orderList = main.getToolDefine().getOrderList();
        if (orderList != null && !orderList.isEmpty()) {
            List<String> keys = new ArrayList<String>();
            for (Category category : tab.getCategoryList()) {
                keys.add(category.getName());
            }
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
                this.defaultCategoryMap.put(key, tab.getCategory(key));
            }
        } else {
            for (Category category : tab.getCategoryList()) {
                this.defaultCategoryMap.put(category.getName(), category);
            }
        }
        this.categoryMap = targetMapCopy(this.defaultCategoryMap);
        if (this.tab.getAuth() != null) {
            this.authFlg = true;
            this.memoryPwdFlg = this.tab.getAuth().isMemory();
            this.pwdAutoClearFlg = this.tab.getAuth().isAutoclear();
            this.pwdGroup = this.tab.getAuth().getGroup();
        }

        if (this.tab.getIconPath() != null && !this.tab.getIconPath().isEmpty()) {
            try {
                this.setImage(new Image(getDisplay(), new FileInputStream(this.tab.getIconPath())));
            } catch (FileNotFoundException fnfe) {
            }
        }
        setText(tab.getName());
        // UI構築
        createItemArea();
    }

    private void createItemArea() {
        // 親Compositeの準備（これがUI部品の親分）
        final Composite composite = new Composite(getParent(), SWT.NULL);
        composite.setLayout(new GridLayout(this.categoryMap.size(), true));

        // 設定の取得
        final Main main = (Main) getParent().getShell().getData("main");
        final PreferenceStore ps = main.getPreferenceStore();

        // ==================== 認証設定グループ ====================
        Group authGrp = new Group(composite, SWT.NONE);
        GridLayout authGrpLt = new GridLayout(5, false);
        authGrpLt.marginWidth = 10;
        authGrpLt.horizontalSpacing = 10;
        authGrp.setLayout(authGrpLt);
        GridData authGrpGrDt = new GridData(GridData.FILL_HORIZONTAL);
        authGrpGrDt.horizontalSpan = this.categoryMap.size();
        authGrp.setLayoutData(authGrpGrDt);
        authGrp.setText("認証情報");
        authGrp.setEnabled(this.authFlg);

        // 保存されているProxy接続情報を取得（開発用でのみ使用）
        // "userid/password"の形式になってます。
        String defineUserPwd = ps.getString(PreferenceConstants.AUTH_USER_PWD + this.pwdGroup);

        // ---------- ユーザーID ----------
        usrTxt = new Text(authGrp, SWT.BORDER);
        usrTxt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        usrTxt.setMessage("ユーザーID");
        usrTxt.setEnabled(this.authFlg);
        if (this.memoryPwdFlg) {
            if (defineUserPwd != null && !defineUserPwd.isEmpty()) {
                usrTxt.setText(defineUserPwd.split("/")[0]);
            }
        }
        // IMEモード設定
        usrTxt.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent arg0) {
                getParent().getShell().setImeInputMode(SWT.NONE);
            }

            @Override
            public void focusLost(FocusEvent arg0) {
            }
        });
        usrTxt.addVerifyListener(new VerifyListener() {
            // Enterを押したときには、このメソッドは呼び出されない
            public void verifyText(VerifyEvent e) {
                // BackspaceやDeleteが押されたときは有効にする。それとプログラム内部からsetTextされた場合も有効にする。
                if (e.character == SWT.BS || e.character == SWT.DEL || e.keyCode == 0) {
                    return;
                }
                // ACCEPTABLE_CHARに定義されている文字列以外は無視
                if (ACCEPTABLE_CHAR.indexOf(Character.toString(e.character)) == -1) {
                    e.doit = false;
                }
            }
        });

        Label slashLbl = new Label(authGrp, SWT.NONE);
        slashLbl.setText("/");

        // ---------- パスワード ----------
        pwdTxt = new Text(authGrp, SWT.BORDER);
        pwdTxt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        pwdTxt.setEchoChar('*');
        pwdTxt.setMessage("パスワード");
        pwdTxt.setEnabled(this.authFlg);
        if (this.memoryPwdFlg) {
            if (defineUserPwd != null && !defineUserPwd.isEmpty()) {
                pwdTxt.setText(defineUserPwd.split("/")[1]);
            }
        }
        // IMEモード設定
        pwdTxt.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent arg0) {
                getParent().getShell().setImeInputMode(SWT.NONE);
            }

            @Override
            public void focusLost(FocusEvent arg0) {
            }
        });

        // ---------- 認証記憶ボタン ----------
        idPwdMemoryBtn = new Button(authGrp, SWT.PUSH);
        idPwdMemoryBtn.setText("記憶");
        idPwdMemoryBtn.setEnabled(false); // 初期状態では使えなくしておく
        idPwdMemoryBtn.setToolTipText("認証情報を記憶します。");
        idPwdMemoryBtn.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                ps.setValue(PreferenceConstants.AUTH_USER_PWD + pwdGroup, String.format("%s/%s", usrTxt.getText(), pwdTxt.getText()));
                try {
                    ps.save();
                    MessageDialog.openInformation(getParent().getShell(), "認証情報", "認証情報を記憶しました。");
                } catch (IOException ioe) {
                    MessageDialog.openError(getParent().getShell(), "認証情報", "認証情報の記憶に失敗しました。");
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {
            }
        });

        // ---------- 認証チェックボタン ----------
        authCheckBtn = new Button(authGrp, SWT.PUSH);
        authCheckBtn.setImage(new Image(getDisplay(), Main.class.getClassLoader().getResourceAsStream("check_icon.png")));
        authCheckBtn.setText("認証チェック");
        authCheckBtn.setEnabled(false);
        authCheckBtn.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                directCheck();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {
            }
        });

        // ---------- サーバフィルタリング ----------
        filterTxt = new Text(composite, SWT.BORDER);
        GridData filterTxtGrDt = new GridData(GridData.FILL_HORIZONTAL);
        filterTxtGrDt.horizontalSpan = this.categoryMap.size() - 1;
        filterTxt.setLayoutData(filterTxtGrDt);
        filterTxt.setMessage("サーバをフィルタリングすることができます。例：WebAP, EXEなど");
        filterTxt.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent event) {
                if (filterTxt.getText().isEmpty()) {
                    for (CheckboxTreeViewer tree : treeMap.values()) {
                        tree.removeFilter(serverFilter);
                    }
                } else {
                    for (CheckboxTreeViewer tree : treeMap.values()) {
                        tree.addFilter(serverFilter);
                        tree.expandAll();
                    }
                }
                main.setWindowTitle(null);
            }
        });

        // ---------- 展開サーバチェックボタン ----------
        allCheckBtn = new Button(composite, SWT.PUSH);
        allCheckBtn.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        allCheckBtn.setText("展開されているサーバを全てチェック");
        allCheckBtn.setToolTipText("展開されているサーバ全てにチェックを入れます。");
        allCheckBtn.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                for (CheckboxTreeViewer tree : treeMap.values()) {
                    for (Object obj : tree.getVisibleExpandedElements()) {
                        viewerRefreshForTreeViewer(tree, obj, true);
                    }
                }

            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {
            }
        });

        // ==================== サーバ選択グループ ====================
        List<String> orderList = main.getToolDefine().getOrderList();
        this.treeMap = new HashMap<String, CheckboxTreeViewer>();
        for (Category category : this.categoryMap.values()) {
            Group targetSubGrp = new Group(composite, SWT.NONE);
            GridLayout targetSubGrpLt = new GridLayout(1, false);
            targetSubGrp.setLayout(targetSubGrpLt);
            targetSubGrp.setLayoutData(new GridData(GridData.FILL_BOTH));
            targetSubGrp.setText(category.getName());
            final CheckboxTreeViewer chkTree = new CheckboxTreeViewer(targetSubGrp, SWT.BORDER | SWT.VIRTUAL);
            chkTree.setContentProvider(new TreeContentProvider());
            ColumnViewerToolTipSupport.enableFor(chkTree, ToolTip.NO_RECREATE);
            chkTree.setLabelProvider(new TreeLabelProvider());
            if (orderList != null && !orderList.isEmpty()) {
                category.sortTargetNode(orderList);
            }
            chkTree.setInput(category.getTargetNode());
            final Tree tree = chkTree.getTree();
            tree.setLayoutData(new GridData(GridData.FILL_BOTH));
            tree.setToolTipText("対象のサーバにチェックを入れてください。");
            // ---------- 対象サーバ選択CheckBoxTreeViewerチェックリスナー ----------
            chkTree.addCheckStateListener(new ICheckStateListener() {
                public void checkStateChanged(CheckStateChangedEvent event) {
                    // チェックボックスにチェックが入れられたら、それに応じて親とか子のノードにチェックを連動して入れるとかそういうことをしている。
                    viewerRefreshForTreeViewer(chkTree, event.getElement(), event.getChecked());
                }
            });

            chkTree.addSelectionChangedListener(new ISelectionChangedListener() {
                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    IStructuredSelection selection = (IStructuredSelection) event.getSelection();
                    if (!selection.isEmpty()) {
                        Object selectedObject = selection.getFirstElement();
                        TargetNode node = (TargetNode) selectedObject;
                        main.setWindowTitle(getToolTipText(node));
                    }
                }
            });

            chkTree.addDoubleClickListener(new IDoubleClickListener() {
                @Override
                public void doubleClick(DoubleClickEvent event) {
                    // 何をしているかというと、要は親ノードがダブルクリックされたら、ツリーを展開するとか、逆に閉じるとかの処理をしている。
                    IStructuredSelection selection = (IStructuredSelection) event.getSelection();
                    if (!selection.isEmpty()) {
                        TreeViewer treeViewer = (TreeViewer) event.getSource();
                        Object selectedObject = selection.getFirstElement();
                        TargetNode node = (TargetNode) selectedObject;
                        if (node.getIpAddr() != null) {
                            if (!authFlg || authInputStatus) {
                                makeAndExecuteTTL(node, 1, null);
                                Main main = (Main) getParent().getShell().getData("main");
                                if (main.isTtlOnly()) {
                                    MessageDialog.openInformation(getParent().getShell(), "TTLマクロ生成", "TTLマクロを生成しました。");
                                }
                            } else {
                                MessageDialog.openError(getParent().getShell(), "サーバ接続", "認証情報が入力されていません。");
                            }
                            return;
                        }
                        if (treeViewer.getExpandedState(selectedObject)) {
                            treeViewer.collapseToLevel(selectedObject, 1);
                        } else {
                            treeViewer.expandToLevel(selectedObject, TreeViewer.ALL_LEVELS);
                        }
                        treeViewer.reveal(selectedObject);
                    }
                }
            });

            // Ctrl+cに対応
            tree.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.stateMask == SWT.CTRL && e.keyCode == 99) {
                        TreeItem item = tree.getSelection()[0];
                        TargetNode node = (TargetNode) item.getData();
                        StringBuilder builder = new StringBuilder();
                        if (node.getChildren().isEmpty()) {
                            // 要は子供（サーバ号機）の場合
                            builder.append(String.format("%-8s", node.getHostName()));
                            builder.append(",");
                            builder.append(String.format("%-15s", node.getIpAddr()));
                            builder.append(",");
                            builder.append(node.getParent().getName());
                            builder.append(",");
                            builder.append(node.getName());
                        } else {
                            // 要は親（サーバ種別）の場合
                            builder.append(node.getName());
                            builder.append("\r\n");
                            for (TargetNode nd : node.getChildren()) {
                                builder.append(String.format("%-8s", nd.getHostName()));
                                builder.append(",");
                                builder.append(String.format("%-15s", nd.getIpAddr()));
                                builder.append(",");
                                builder.append(nd.getName());
                                builder.append("\r\n");
                            }
                        }
                        // クリップボード
                        Clipboard clipBoard = new Clipboard(composite.getShell().getDisplay());
                        clipBoard.setContents(new Object[] { builder.toString() }, new Transfer[] { TextTransfer.getInstance() });
                    }
                }
            });

            // あとでTreeViewerに対して一括でなんだかんだやるので、Mapに格納しておく。
            this.treeMap.put(category.getName(), chkTree);

            // ここからサーバツリーの右クリックメニューの設定
            MenuManager manager = new MenuManager();
            manager.setRemoveAllWhenShown(true);
            manager.addMenuListener(new IMenuListener() {
                public void menuAboutToShow(IMenuManager mgr) {
                    IStructuredSelection selection = chkTree == null ? null : (StructuredSelection) chkTree.getSelection();
                    ActionContext context = new ActionContext(selection);
                    TreeViewActionGroup actionGroup = new TreeViewActionGroup(composite.getShell(), chkTree);
                    actionGroup.setContext(context);
                    actionGroup.fillContextMenu(mgr);
                }
            });
            Menu menu = manager.createContextMenu(chkTree.getControl());
            chkTree.getControl().setMenu(menu);
        }

        Composite bottomGrp = new Composite(composite, SWT.NONE);
        bottomGrp.setLayout(new GridLayout(2, true));
        GridData bottomGrpGrDt = new GridData(GridData.FILL_HORIZONTAL);
        bottomGrpGrDt.horizontalSpan = this.categoryMap.size();
        bottomGrp.setLayoutData(bottomGrpGrDt);

        // ==================== サーバ選択状態 保存、読込グループ ====================
        Composite defaultGrp = new Composite(bottomGrp, SWT.NONE);
        defaultGrp.setLayout(new GridLayout(1, true));
        GridData defaultGrpGrDt = new GridData(GridData.FILL_HORIZONTAL);
        defaultGrpGrDt.horizontalAlignment = SWT.LEFT;
        // ---------- デフォルトの復元ボタン ----------
        Button defaultBtn = new Button(defaultGrp, SWT.PUSH);
        defaultBtn.setImage(new Image(getDisplay(), Main.class.getClassLoader().getResourceAsStream("refresh-icon.png")));
        defaultBtn.setText("デフォルトの復元");
        defaultBtn.setToolTipText("サーバツリーをデフォルトの状態に戻します。");
        defaultBtn.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                defaultTreeData();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });

        // ==================== ユーザーIDとパスワードのリスナー登録 ====================
        this.usrTxt.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                authInputChange();
                propertyChangeSupport.firePropertyChange("authUsr", null, tab.getAuth().getGroup() + "/" + usrTxt.getText());
            }
        });

        this.pwdTxt.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                authInputChange();
                propertyChangeSupport.firePropertyChange("authPwd", null, tab.getAuth().getGroup() + "/" + pwdTxt.getText());
            }
        });

        setControl(composite);
    }

    public boolean isAnythingChecked() {
        // まずCheckBoxTreeViewerでチェックの入っているものを取得する。
        List<Object> checkedTreeList = new ArrayList<Object>();
        for (CheckboxTreeViewer tree : treeMap.values()) {
            checkedTreeList.addAll(Arrays.asList(tree.getCheckedElements()));
        }
        if (checkedTreeList.isEmpty()) {
            // １つもチェックされていなかったらfalseを返す。
            return false;
        }
        return true;
    }

    public void propertyChangeUpdate() {
        authInputChange();
    }

    private void defaultTreeData() {
        Main main = (Main) getParent().getShell().getData("main");
        List<String> orderList = main.getToolDefine().getOrderList();
        this.categoryMap = targetMapCopy(this.defaultCategoryMap);
        for (Category category : this.categoryMap.values()) {
            CheckboxTreeViewer treeViewer = this.treeMap.get(category.getName());
            if (orderList != null && !orderList.isEmpty()) {
                category.sortTargetNode(orderList);
            }
            treeViewer.setInput(category.getTargetNode());
            treeViewer.refresh();
        }
        this.filterTxt.setText("");
        main.setWindowTitle(null);
    }

    /**
     * authInputChange<br>
     * ユーザーID、パスワードの入力状態をチェックして、認証ボタンや一括起動ボタンの状態を変更してます。
     */
    private void authInputChange() {
        if (this.usrTxt.getText().isEmpty() || this.pwdTxt.getText().isEmpty()) {
            this.authInputStatus = false;
            this.idPwdMemoryBtn.setEnabled(false);
            this.authCheckBtn.setEnabled(false);
            if (this.authFlg) {
                this.propertyChangeSupport.firePropertyChange("authInput", null, Boolean.FALSE);
            } else {
                this.propertyChangeSupport.firePropertyChange("authInput", null, Boolean.TRUE);
            }
        } else {
            this.authInputStatus = true;
            this.idPwdMemoryBtn.setEnabled(this.memoryPwdFlg);
            this.authCheckBtn.setEnabled(this.isValidAuthCheck());
            this.propertyChangeSupport.firePropertyChange("authInput", null, Boolean.TRUE);
        }
    }

    private boolean isValidAuthCheck() {
        Auth auth = this.tab.getAuth();
        if (auth == null) {
            return false;
        }
        if (auth.getCheck() == null || auth.getCheck().isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * refreshTree<br>
     * 全てのTreeViewerの状態を更新して再描画します。
     */
    public void refreshTree() {
        for (CheckboxTreeViewer tree : treeMap.values()) {
            tree.refresh();
            IStructuredSelection selection = (IStructuredSelection) tree.getSelection();
            if (!selection.isEmpty()) {
                Object selectedObject = selection.getFirstElement();
                TargetNode node = (TargetNode) selectedObject;
                Main main = (Main) getParent().getShell().getData("main");
                main.setWindowTitle(getToolTipText(node));
            }
        }
    }

    /**
     * directCheck<br>
     * Teratermマクロを使わずにTelnetClientを使って直接サーバに接続をして認証チェックを行います。
     * 
     * @param target
     */
    private void directCheck() {
        // 設定クラスを取得
        Main main = (Main) getParent().getShell().getData("main");
        IPreferenceStore ps = main.getPreferenceStore();
        // まずはTTLファイルを作成するディレクトリを取得
        String ttlDir = ps.getString(PreferenceConstants.WORK_DIR);
        File ttlDirFile = new File(ttlDir);
        if (!ttlDirFile.isAbsolute()) {
            try {
                ttlDir = ttlDirFile.getCanonicalPath();
            } catch (IOException e) {
                MessageDialog.openError(getParent().getShell(), "実行時エラー", "基本設定にある作業領域（ディレクトリ）はちゃんと作成されていますか？" + e.getMessage());
            }
        }
        if (this.authFlg) {
            ttlDir = ttlDir + "\\" + this.usrTxt.getText();
            if (!makeUserDirectory(ttlDir)) {
                return;
            }
        }
        // ========== ファイルパス生成 ここから ==========
        // 例） C:\library\work\4面-新WebAP_con_t-shiozaki.ttl
        StringBuilder ttlFile = new StringBuilder(ttlDir);
        ttlFile.append("\\authcheck_");
        ttlFile.append(this.usrTxt.getText());
        ttlFile.append(".ttl");
        // ========== ファイルパス生成 ここまで ==========
        File outputFile = new File(ttlFile.toString());
        try {
            String ttlCharCode = ps.getString(PreferenceConstants.TTL_CHARCODE);
            FileOutputStream fos = new FileOutputStream(outputFile);
            OutputStreamWriter osw = null;
            if (ttlCharCode != null && !ttlCharCode.isEmpty()) {
                try {
                    osw = new OutputStreamWriter(fos, ttlCharCode);
                } catch (UnsupportedEncodingException uee) {
                    osw = new OutputStreamWriter(fos, "Shift_JIS");
                }
            } else {
                osw = new OutputStreamWriter(fos, "Shift_JIS");
            }
            PrintWriter pw = new PrintWriter(osw);
            try {
                // ++++++++++++++++++++ 接続、認証文字列の取得 ++++++++++++++++++++ //
                // ---------- もろもろ情報を取得 ここから ----------
                String authUsr = this.usrTxt.getText();
                String authPwd = this.pwdTxt.getText();
                // INIファイル
                String iniDir = ps.getString(PreferenceConstants.INIFILE_DIR);
                File iniDirFile = new File(iniDir);
                if (!iniDirFile.isAbsolute()) {
                    iniDir = iniDirFile.getCanonicalPath();
                }
                String logDir = ps.getString(PreferenceConstants.LOG_DIR);
                File logDirFile = new File(logDir);
                if (!logDirFile.isAbsolute()) {
                    logDir = logDirFile.getCanonicalPath();
                }
                String workDir = ps.getString(PreferenceConstants.WORK_DIR);
                File workDirFile = new File(workDir);
                if (!workDirFile.isAbsolute()) {
                    workDir = workDirFile.getCanonicalPath();
                }
                // ---------- もろもろ情報を取得 ここまで ----------
                Map<String, String> valuesMap = new TreeMap<String, String>();
                valuesMap.put("authuser", authUsr);
                valuesMap.put("authpassword", authPwd);
                valuesMap.put("inidir", iniDir);
                valuesMap.put("logdir", logDir);
                valuesMap.put("workdir", workDir);

                StrSubstitutor sub = new StrSubstitutor(valuesMap);
                String NEW_LINE = System.getProperty("line.separator");
                StringBuilder word = new StringBuilder();

                String magicPwd = ps.getString(PreferenceConstants.TTL_AUTH_PWD_HIDE);
                if (magicPwd == null || magicPwd.isEmpty()) {
                    magicPwd = "PASSWORD";
                }
                if (this.tab.getAuth().getCheck().contains(magicPwd)) {
                    word.append(magicPwd + "=param2" + NEW_LINE); // 認証パスワードはセキュリティのためマクロ実行引数で渡します。
                    word.append("strlen " + magicPwd + NEW_LINE);
                    word.append("if result = 0 then" + NEW_LINE);
                    word.append("    passwordbox 'パスワードを入力してください。[" + authUsr + "]' '認証'" + NEW_LINE);
                    word.append("    strlen inputstr" + NEW_LINE);
                    word.append("    if result = 0 then" + NEW_LINE);
                    word.append("        exit" + NEW_LINE);
                    word.append("    else" + NEW_LINE);
                    word.append("        " + magicPwd + "=inputstr" + NEW_LINE);
                    word.append("    endif" + NEW_LINE);
                    word.append("endif" + NEW_LINE);
                }

                for (String authCheckLine : sub.replace(this.tab.getAuth().getCheck()).split("\r\n")) {
                    word.append(authCheckLine.trim() + NEW_LINE);
                }
                pw.println(word.toString());
            } catch (Exception e) {
                MessageDialog.openError(getParent().getShell(), "実行時エラー", "コマンドの生成でエラーが発生しました。\n" + e.getMessage());
                return;
            } finally {
                pw.close();
            }
            Runtime runtime = Runtime.getRuntime();
            String pwdArg = this.pwdTxt.getText();
            String ttpmacroexe = ps.getString(PreferenceConstants.TTPMACRO_EXE);
            File ttpmacroexeFile = new File(ttpmacroexe);
            if (!ttpmacroexeFile.isAbsolute()) {
                try {
                    ttpmacroexe = ttpmacroexeFile.getCanonicalPath();
                } catch (IOException e) {
                    MessageDialog.openError(getParent().getShell(), "実行時エラー", "基本設定にある作業領域（ディレクトリ）はちゃんと作成されていますか？" + e.getMessage());
                }
            }
            runtime.exec(new String[] { ttpmacroexe, ttlFile.toString(), pwdArg });
        } catch (FileNotFoundException fnfe) {
            MessageDialog.openError(getParent().getShell(), "実行時エラー", "基本設定にある作業領域（ディレクトリ）はちゃんと作成されていますか？" + fnfe.getMessage());
        } catch (Exception e) {
            MessageDialog.openError(getParent().getShell(), "実行時エラー", "実行環境に問題があります。初期設定はお済みでしょうか？\n" + e.getMessage());
        }
    }

    /**
     * bulkConnection<br>
     * 一括起動ボタンから実行される処理です。
     */
    public void bulkConnection() {
        // まずCheckBoxTreeViewerでチェックの入っているものを取得する。
        List<TargetNode> checkedTreeList = new ArrayList<TargetNode>();
        for (CheckboxTreeViewer tree : treeMap.values()) {
            for (Object obj : tree.getCheckedElements()) {
                if (((TargetNode) obj).getIpAddr() == null) {
                    continue;
                }
                checkedTreeList.add((TargetNode) obj);
            }
        }
        if (checkedTreeList.isEmpty()) {
            // １つもチェックされていなかったらエラーダイアログを出して終了
            MessageDialog.openError(getParent().getShell(), "一括起動", "対象サーバが選択されていません。");
            return;
        }
        Main main = (Main) getParent().getShell().getData("main");

        // 念のため確認ダイアログを出す。
        String templateCmd = null;
        String dialogMsg = "一括で接続します。よろしいですか？";
        String[] buttonArray;

        List<TeratermStationBulkAction> bulkActionList = new ArrayList<TeratermStationBulkAction>();
        for (TeratermStationPlugin plugin : main.getToolDefine().getPluginList()) {
            List<TeratermStationBulkAction> actionList = plugin.getBulkActions(checkedTreeList, getParent().getShell());
            if (actionList != null) { // 一括接続での拡張機能の無いプラグインはnullを返すので.
                for (TeratermStationBulkAction action : actionList) {
                    if (action.isValid()) {
                        bulkActionList.add(action);
                    }
                }
            }
        }

        if (bulkActionList.isEmpty()) {
            buttonArray = new String[] { "OK", "Cancel" };
        } else {
            dialogMsg += "\r\n（拡張機能を利用することもできます）";
            buttonArray = new String[] { "OK", "Cancel", "拡張機能選択..." };
        }
        MessageDialog dialog = new MessageDialog(getParent().getShell(), "一括起動", null, dialogMsg, MessageDialog.QUESTION, buttonArray, 0);
        int result = dialog.open();
        switch (result) {
            case 0: // OK
                break;
            case 2: // 拡張機能の利用
                PluginSelectDialog pluginDialog = new PluginSelectDialog(getParent().getShell(), bulkActionList);
                int pluginResult = pluginDialog.open();
                if (IDialogConstants.OK_ID != pluginResult) {
                    return;
                }
                TeratermStationBulkAction selectedAction = pluginDialog.getSelectedAction();
                selectedAction.run();
                return;
            default: // Cancel or Other
                return;
        }
        try {
            // チェックされているノードすべてで実行します。もちろん親ノード（サーバ種別を表すノード）は対象外です。
            int idx = 1;
            for (TargetNode target : checkedTreeList) {
                makeAndExecuteTTL(target, idx, templateCmd);
                idx++;
                if (!main.isTtlOnly()) {
                    Thread.sleep(BULK_INTERVAL); // スリープしなくても問題はないけど、あまりにも連続でターミナルが開くのもあれなので。
                }
            }
            if (main.isTtlOnly()) {
                // TTLファイルの作成のみだったら、ファイル作成後、ダイアログを出す。
                MessageDialog.openInformation(getParent().getShell(), "TTLマクロ生成", "TTLマクロを生成しました。");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * makeAndExecuteTTL<br>
     * 与えられた情報からTTLを生成して実行します。
     * 
     * @param target
     *            対象サーバのノード
     * @param befaft
     *            処理区分
     */
    public void makeAndExecuteTTL(TargetNode target, int idx, String templateCmd) {
        // 設定クラスを取得
        Main main = (Main) getParent().getShell().getData("main");
        IPreferenceStore ps = main.getPreferenceStore();
        // まずはTTLファイルを作成するディレクトリを取得
        String ttlDir = ps.getString(PreferenceConstants.WORK_DIR);
        File ttlDirFile = new File(ttlDir);
        if (!ttlDirFile.isAbsolute()) {
            try {
                ttlDir = ttlDirFile.getCanonicalPath();
            } catch (IOException e) {
                MessageDialog.openError(getParent().getShell(), "実行時エラー", "基本設定にある作業領域（ディレクトリ）はちゃんと作成されていますか？" + e.getMessage());
            }
        }
        if (this.authFlg) {
            ttlDir = ttlDir + "\\" + this.usrTxt.getText();
            if (!makeUserDirectory(ttlDir)) {
                return;
            }
        }
        // ========== ファイルパス生成 ここから ==========
        // 例） C:\library\work\4面-新WebAP_con_t-shiozaki.ttl
        StringBuilder ttlFile = new StringBuilder(ttlDir);
        ttlFile.append("\\");
        if (target.getParent().getName() != null) {
            ttlFile.append(target.getParent().getName());
        } else {
            ttlFile.append(target.getCategory().getName());
        }
        ttlFile.append("-");
        ttlFile.append(target.getName());
        ttlFile.append("_");
        ttlFile.append(this.usrTxt.getText());
        ttlFile.append(".ttl");
        // ========== ファイルパス生成 ここまで ==========

        File outputFile = new File(ttlFile.toString());
        try {
            String ttlCharCode = ps.getString(PreferenceConstants.TTL_CHARCODE);
            FileOutputStream fos = new FileOutputStream(outputFile);
            OutputStreamWriter osw = null;
            if (ttlCharCode != null && !ttlCharCode.isEmpty()) {
                try {
                    osw = new OutputStreamWriter(fos, ttlCharCode);
                } catch (UnsupportedEncodingException uee) {
                    osw = new OutputStreamWriter(fos, "Shift_JIS");
                }
            } else {
                osw = new OutputStreamWriter(fos, "Shift_JIS");
            }
            PrintWriter pw = new PrintWriter(osw);
            try {
                // ++++++++++++++++++++ 接続、認証文字列の取得 ++++++++++++++++++++ //
                pw.println(genConnText(target, idx, templateCmd));
            } catch (Exception e) {
                MessageDialog.openError(getParent().getShell(), "実行時エラー", "コマンドの生成でエラーが発生しました。\n" + e.getMessage());
                return;
            } finally {
                pw.close();
            }
            // 「TTLファイルの作成のみ」にチェックが入っているか取得
            if (!main.isTtlOnly()) { // TTL作成のみでなかったら本当に実行する
                Thread.sleep(FILE_INTERVAL);
                Runtime runtime = Runtime.getRuntime();
                String pwdArg = this.pwdTxt.getText();
                String ttpmacroexe = ps.getString(PreferenceConstants.TTPMACRO_EXE);
                File ttpmacroexeFile = new File(ttpmacroexe);
                if (!ttpmacroexeFile.isAbsolute()) {
                    try {
                        ttpmacroexe = ttpmacroexeFile.getCanonicalPath();
                    } catch (IOException e) {
                        MessageDialog.openError(getParent().getShell(), "実行時エラー", "基本設定にある作業領域（ディレクトリ）はちゃんと作成されていますか？" + e.getMessage());
                    }
                }
                runtime.exec(new String[] { ttpmacroexe, ttlFile.toString(), pwdArg });
            }
        } catch (FileNotFoundException fnfe) {
            MessageDialog.openError(getParent().getShell(), "実行時エラー", "基本設定にある作業領域（ディレクトリ）はちゃんと作成されていますか？" + fnfe.getMessage());
        } catch (Exception e) {
            MessageDialog.openError(getParent().getShell(), "実行時エラー", "実行環境に問題があります。初期設定はお済みでしょうか？\n" + e.getMessage());
        }
    }

    /**
     * genConnText<br>
     * 与えられた情報から接続用のネゴシエーション文字列を生成して返します。
     * 
     * @param node
     *            対象サーバ情報
     * @return 接続用のネゴシエーション文字列
     */
    private String genConnText(TargetNode node, int idx, String templateCmd) throws Exception {
        StringBuilder word = new StringBuilder();
        try {
            // 設定クラスを取得
            Main main = (Main) getParent().getShell().getData("main");
            IPreferenceStore ps = main.getPreferenceStore();

            // ---------- もろもろ情報を取得 ここから ----------
            String authUsr = this.usrTxt.getText();
            String authPwd = this.pwdTxt.getText();
            String ipAddr = node.getIpAddr();
            String targetSvr = node.getName();
            String svrType = node.getParent().getName();
            if (svrType == null) {
                svrType = node.getCategory().getName();
            }
            int usrIdx = main.getLoginUserIdx();
            String loginUsr = node.getLoginUsr(usrIdx);
            String loginPwd = node.getLoginPwd(usrIdx);
            // INIファイル
            String iniDir = ps.getString(PreferenceConstants.INIFILE_DIR);
            File iniDirFile = new File(iniDir);
            if (!iniDirFile.isAbsolute()) {
                iniDir = iniDirFile.getCanonicalPath();
            }
            String logDir = ps.getString(PreferenceConstants.LOG_DIR);
            File logDirFile = new File(logDir);
            if (!logDirFile.isAbsolute()) {
                logDir = logDirFile.getCanonicalPath();
            }
            String workDir = ps.getString(PreferenceConstants.WORK_DIR);
            File workDirFile = new File(workDir);
            if (!workDirFile.isAbsolute()) {
                workDir = workDirFile.getCanonicalPath();
            }
            String iniFile = iniDir + "\\" + node.getIniFile();
            String seqNo = String.format("%03d. ", idx);
            // ---------- もろもろ情報を取得 ここまで ----------
            Map<String, String> valuesMap = new TreeMap<String, String>();
            valuesMap.put("authuser", authUsr);
            valuesMap.put("authpassword", authPwd);
            valuesMap.put("ipaddress", ipAddr);
            valuesMap.put("loginuser", loginUsr);
            valuesMap.put("loginpassword", loginPwd);
            valuesMap.put("inidir", iniDir);
            valuesMap.put("inifile", iniFile);
            valuesMap.put("logdir", logDir);
            valuesMap.put("workdir", workDir);

            StrSubstitutor sub = new StrSubstitutor(valuesMap);

            String connect = sub.replace(this.tab.getConnect());

            String NEW_LINE = System.getProperty("line.separator");
            if (this.authFlg) {
                String magicPwd = ps.getString(PreferenceConstants.TTL_AUTH_PWD_HIDE);
                if (magicPwd == null || magicPwd.isEmpty()) {
                    magicPwd = "PASSWORD";
                }
                if (connect.contains(magicPwd)) {
                    word.append(magicPwd + "=param2" + NEW_LINE); // 認証パスワードはセキュリティのためマクロ実行引数で渡します。
                    word.append("strlen " + magicPwd + NEW_LINE);
                    word.append("if result = 0 then" + NEW_LINE);
                    word.append("    passwordbox 'パスワードを入力してください。[" + authUsr + "]' '認証'" + NEW_LINE);
                    word.append("    strlen inputstr" + NEW_LINE);
                    word.append("    if result = 0 then" + NEW_LINE);
                    word.append("        exit" + NEW_LINE);
                    word.append("    else" + NEW_LINE);
                    word.append("        " + magicPwd + "=inputstr" + NEW_LINE);
                    word.append("    endif" + NEW_LINE);
                    word.append("endif" + NEW_LINE);
                }
            }
            for (String line : sub.replace(connect).split("\r\n")) {
                word.append(line.trim() + NEW_LINE);
            }
            word.append("settitle '" + seqNo + svrType + " - " + targetSvr + "'" + NEW_LINE); // タイトルはサーバ種別とサーバ名
            word.append(genLogOpen(node, logDir));
            // ここまで
            if (node.getProcedure() != null) {
                String procedure = sub.replace(node.getProcedure());
                word.append(procedure);
            }

            // テンプレート対応
            word.append(genTemplateCmd(node, templateCmd));
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return word.toString();
    }

    private String genLogOpen(TargetNode node, String logDir) {
        // 設定クラスを取得
        Main main = (Main) getParent().getShell().getData("main");
        IPreferenceStore ps = main.getPreferenceStore();

        // 端末名を取得
        String pcName = System.getenv("COMPUTERNAME");

        // タイムスタンプを取得
        Calendar objCal = Calendar.getInstance();
        SimpleDateFormat monthFmt = new SimpleDateFormat("yyyyMM");
        SimpleDateFormat dateFmt = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat timeFmt = new SimpleDateFormat("yyyyMMdd-HHmmss");
        String month = monthFmt.format(objCal.getTime());
        String date = dateFmt.format(objCal.getTime());
        String timestamp = timeFmt.format(objCal.getTime());

        // ログファイルのパスは結局は下のような構成です。
        // C:\library\log\201207\20120710\20120710-121212_WebAP_WebAP(A)#1_PTP95049.log
        String monthDir = logDir + "\\" + month;
        String dateDir = logDir + "\\" + month + "\\" + date;
        String[] dirArray = new String[] { logDir, monthDir, dateDir };

        // ログファイル
        String svrType = node.getParent().getName();
        if (svrType == null) {
            svrType = node.getCategory().getName();
        }
        String targetSvr = node.getName();
        String logFile = dateDir + "\\" + timestamp + "_" + svrType + "_" + targetSvr + "_" + pcName + ".log";

        StringBuilder word = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        // dirArrayには 'YYYYMM' と 'YYYYMM\YYYYMMDD' が入っているので
        // このループでは最初にYYYYMMについてチェックなければ作成を行い
        // 次にYYYYMMDDのチェックなければ作成をやっている。
        // まあディレクトリが違うだけでまったく同じ処理なのでループにしただけです。
        for (String dir : dirArray) {
            word.append("logdir = '" + dir + "'" + NEW_LINE);
            word.append("filesearch logdir" + NEW_LINE);
            word.append("if result = 0 then" + NEW_LINE);
            word.append("    foldercreate logdir" + NEW_LINE);
            word.append("    if result != 0 then" + NEW_LINE);
            word.append("        desc = '\\nのディレクトリ作成に失敗しました。\\n管理者に問い合わせてください。'" + NEW_LINE);
            word.append("        strspecial desc" + NEW_LINE);
            word.append("        sprintf2 msg '%s%s' logdir desc" + NEW_LINE);
            word.append("        messagebox msg ''" + NEW_LINE);
            word.append("        closett" + NEW_LINE);
            word.append("        end" + NEW_LINE);
            word.append("    endif" + NEW_LINE);
            word.append("endif" + NEW_LINE);
        }
        // ログをOPEN
        String logopenOption = ps.getString(PreferenceConstants.LOGOPEN_OPTION);
        if (logopenOption == null || logopenOption.isEmpty()) {
            logopenOption = "0 0 0 0 1";
        }
        word.append("logopen '" + logFile + "' " + logopenOption + NEW_LINE);
        return word.toString();
    }

    private String genTemplateCmd(TargetNode node, String templateCmd) throws Exception {
        if (templateCmd == null) {
            return "";
        }
        Map<String, String> valuesMap = new TreeMap<String, String>();
        String keyValueStr = node.getKeyValue();
        if (keyValueStr != null) {
            for (String keyValue : keyValueStr.split(",")) {
                valuesMap.put(keyValue.split(":")[0].trim(), keyValue.split(":")[1].trim());
            }
        }
        StrSubstitutor sub = new StrSubstitutor(valuesMap);
        return sub.replace(templateCmd);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Category> targetMapCopy(Map<String, Category> obj) {
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(obj);
            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            ObjectInputStream in = new ObjectInputStream(byteIn);
            return (Map<String, Category>) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean makeUserDirectory(String path) {
        File dir = new File(path);
        if (dir.exists()) {
            return true;
        }
        if (!dir.mkdirs()) {
            MessageDialog.openError(getParent().getShell(), "エラー", "ユーザーディレクトリを作成できませんでした。");
            return false;
        }
        return true;
    }

    /**
     * TreeContentProvider<br>
     * <p>
     * このクラスはTreeViewerで必要です。 形式ばったものなので、あまり中身を知らなくて良いです。 どこかでTreeViewerを使いたい場合はこの辺も必要なのでコピーして使ってください。
     * </p>
     * 
     * @author turbou
     * 
     */
    class TreeContentProvider implements ITreeContentProvider {

        public Object[] getElements(Object inputElement) {
            return getChildren(inputElement);
        }

        public Object[] getChildren(Object parentElement) {
            return ((TargetNode) parentElement).getChildren().toArray();
        }

        public Object getParent(Object element) {
            return ((TargetNode) element).getParent();
        }

        public boolean hasChildren(Object element) {
            if (((TargetNode) element).getChildren().size() > 0) {
                return true;
            } else {
                return false;
            }
        }

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }

    }

    /**
     * TreeLabelProvider<br>
     * <p>
     * これもTreeViewerに必要なクラスです。 でも上のやつよりは重要で<br>
     * 要はTreeViewerのノードのタイトルとかをどのように表示するか を定義するクラスです。 getTextメソッドは表示されるタイトルを返します。<br>
     * getToolTipTextはマウスをノードに重ねた時にでるヒント文字列です。
     * </p>
     * 
     * @author turbou
     * 
     */
    class TreeLabelProvider extends ColumnLabelProvider {
        public String getText(Object element) {
            return ((TargetNode) element).getName();
        }

        @Override
        public Image getImage(Object element) {
            TargetNode node = (TargetNode) element;
            Image baseImage;
            if (node.isParent()) {
                baseImage = new Image(getParent().getDisplay(), getClass().getClassLoader().getResourceAsStream("servers.png"));
            } else {
                baseImage = new Image(getParent().getDisplay(), getClass().getClassLoader().getResourceAsStream("server.png"));
            }
            DecorationOverlayIcon icon = null;
            ImageDescriptor statusDeco = null;
            // if (node.isCustomDiff()) {
            // Image statusImage = new Image(getParent().getDisplay(),
            // getClass().getClassLoader().getResourceAsStream("custom_ov.gif"));
            // statusDeco = ImageDescriptor.createFromImage(statusImage);
            // }
            icon = new DecorationOverlayIcon(baseImage, statusDeco, IDecoration.TOP_RIGHT);
            return icon.createImage();
        }

        @Override
        public String getToolTipText(Object element) {
            TargetNode node = (TargetNode) element;
            StringBuilder builder = new StringBuilder();
            if (node.getChildren().isEmpty()) {
                // 要は子供（サーバ号機）の場合
                Main main = (Main) getParent().getShell().getData("main");
                int usrIdx = main.getLoginUserIdx();
                String user = node.getLoginUsr(usrIdx);
                builder.append(user);
                builder.append("@");
                builder.append(node.getIpAddr());
                if (node.getHostName() != null) {
                    builder.append(String.format("(%s)", node.getHostName()));
                }
                if (node.getId() != null && !node.getId().isEmpty() && !node.getHostName().equals(node.getId())) {
                    builder.append(String.format("[%s]", node.getId()));
                }
            } else {
                // 要は親（サーバ種別）の場合
                builder.append(node.getName());
                builder.append(String.format("(%s台)", node.getChildren().size()));
                if (node.getId() != null && !node.getId().isEmpty()) {
                    builder.append(String.format("[%s]", node.getId()));
                }
            }
            return builder.toString();
        }

        @Override
        public Font getToolTipFont(Object object) {
            FontRegistry fontRegistry = new FontRegistry(getDisplay());
            fontRegistry.put("MSGothic", new FontData[] { new FontData("ＭＳ ゴシック", 9, SWT.NORMAL) });
            return fontRegistry.get("MSGothic");
        }

        @Override
        public Point getToolTipShift(Object object) {
            return new Point(15, 5);
        }

        @Override
        public int getToolTipDisplayDelayTime(Object object) {
            return 0;
        }

        @Override
        public int getToolTipTimeDisplayed(Object object) {
            return 15000;
        }

    }

    private class ServerFilter extends ViewerFilter {
        @Override
        public boolean select(Viewer viewer, Object parentElement, Object element) {
            TargetNode targetNode = (TargetNode) element;
            List<TargetNode> childrenNodeList = targetNode.getChildren();
            if (childrenNodeList == null || childrenNodeList.isEmpty()) {
                return StringUtils.containsIgnoreCase(targetNode.getName(), filterTxt.getText());
            } else {
                boolean flg = false;
                for (TargetNode childrenNode : childrenNodeList) {
                    flg |= StringUtils.containsIgnoreCase(childrenNode.getName(), filterTxt.getText());
                }
                return flg;
            }
        }
    }

    /**
     * viewerRefreshForTreeViewer<br>
     * このメソッドの中身は複雑で難しいので中身を見なくても良いです。<br>
     * 一応、何をしてるかというと、ある子ノードをチェックしたら親もチェックが入るようにするとか<br>
     * 親ノードをチェックしたら、その配下の子ノード全部にチェックが入るとかしてます。
     * 
     * @param viewer
     * @param element
     * @param checked
     */
    private void viewerRefreshForTreeViewer(CheckboxTreeViewer viewer, Object element, boolean checked) {
        // If the item is changed, check all its children
        viewer.setGrayed(element, false);
        viewer.setSubtreeChecked(element, checked);
        ((TargetNode) element).setIsEnabled(checked);
        if (((TargetNode) element).getChildren().size() > 0) {
            for (TargetNode node : ((TargetNode) element).getChildren()) {
                node.setIsEnabled(checked);
            }
        }

        // If the item is changed, check its parent
        TargetNode parentNode = ((TargetNode) element).getParent();
        while (parentNode != null) {
            List<Boolean> isEnabledList = new ArrayList<Boolean>();
            List<Boolean> isGrayedList = new ArrayList<Boolean>();

            for (TargetNode node : parentNode.getChildren()) {
                isEnabledList.add(node.isEnabled());
                isGrayedList.add(node.isGrayed());
            }

            if (isEnabledList.contains(true) && !isEnabledList.contains(false)) {
                viewer.setGrayed(parentNode, false);
                parentNode.setIsGrayed(false);
                viewer.setChecked(parentNode, true);
                parentNode.setIsEnabled(true);
            } else if (isEnabledList.contains(false) && !isEnabledList.contains(true)) {
                viewer.setGrayed(parentNode, false);
                parentNode.setIsGrayed(false);
                viewer.setChecked(parentNode, false);
                parentNode.setIsEnabled(false);
            } else {
                viewer.setGrayed(parentNode, true);
                parentNode.setIsGrayed(true);
                viewer.setChecked(parentNode, true);
                parentNode.setIsEnabled(true);
            }
            parentNode = parentNode.getParent();
        }

    }

    public void setAuthUsrPwdText(String usrGrp, String usr, String pwdGrp, String pwd) {
        if (usrGrp != null && usr != null) {
            if (this.tab.getAuth() != null) {
                if (usrGrp.equals(this.tab.getAuth().getGroup())) {
                    this.usrTxt.setText(usr);
                }
            }
        }
        if (pwdGrp != null && pwd != null) {
            if (this.tab.getAuth() != null) {
                if (pwdGrp.equals(this.tab.getAuth().getGroup())) {
                    this.pwdTxt.setText(pwd);
                }
            }
        }
    }

    public String getAuthId() {
        return this.usrTxt.getText();
    }

    public String getAuthPwd() {
        return this.pwdTxt.getText();
    }

    public boolean clearPassword() {
        if (!this.pwdTxt.getText().isEmpty()) {
            this.pwdTxt.setText("");
            return true;
        }
        return false;
    }

    public boolean isPwdAutoClearFlg() {
        return pwdAutoClearFlg;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public Tab getTab() {
        return tab;
    }

    private String getToolTipText(TargetNode node) {
        StringBuilder builder = new StringBuilder();
        if (node.getChildren().isEmpty()) {
            // 要は子供（サーバ号機）の場合
            Main main = (Main) getParent().getShell().getData("main");
            int usrIdx = main.getLoginUserIdx();
            String user = node.getLoginUsr(usrIdx);
            builder.append(user);
            builder.append("@");
            builder.append(node.getIpAddr());
            if (node.getHostName() != null) {
                builder.append(" [");
                builder.append(node.getHostName());
                builder.append("]");
            }
        } else {
            // 要は親（サーバ種別）の場合
            builder.append(node.getName());
            builder.append("(");
            builder.append(node.getChildren().size());
            builder.append("台)");
        }
        return builder.toString();
    }

    @Override
    protected void checkSubclass() {
        // super.checkSubclass();
    }
}
