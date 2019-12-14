/*
 * MIT License
 * Copyright (c) 2015-2019 Tabocom
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package jp.co.tabocom.teratermstation.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
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
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ArmEvent;
import org.eclipse.swt.events.ArmListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import jp.co.tabocom.teratermstation.Main;
import jp.co.tabocom.teratermstation.TeratermStationShell;
import jp.co.tabocom.teratermstation.model.Auth;
import jp.co.tabocom.teratermstation.model.Category;
import jp.co.tabocom.teratermstation.model.Tab;
import jp.co.tabocom.teratermstation.model.TargetNode;
import jp.co.tabocom.teratermstation.plugin.TeratermStationPlugin;
import jp.co.tabocom.teratermstation.preference.PreferenceConstants;
import jp.co.tabocom.teratermstation.ui.action.TeratermStationAction;
import jp.co.tabocom.teratermstation.ui.action.TeratermStationContextMenu;

public class EnvTabItem extends TabItem implements PropertyChangeListener {

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
    private PropertyChangeSupport support = new PropertyChangeSupport(this);
    private boolean authInputStatus;
    private boolean authFlg;
    private String authTitle;
    private boolean memoryPwdFlg;
    private boolean pwdAutoClearFlg;
    private String pwdGroup;
    private List<Map<String, Object>> optionInputs;
    private Map<String, Text> optionInputTextMap;
    private String rootDir;

    private Tab tab;
    
    private Main main;

    private ServerFilter serverFilter = new ServerFilter();

    private org.eclipse.swt.widgets.ToolTip currentItemToolTip;

    public static String ACCEPTABLE_CHAR = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_-";

    private Image serverGroupImage;
    private Image serverGroupImageUserNull;
    private Image serverImage;
    private Image serverImageUserNull;
    
    public Main getMain() {
        return main;
    }

    public TeratermStationShell getShell() {
        return (TeratermStationShell) getParent().getShell();
    }

    /**
     * デフォルトコンストラクタ<br>
     * 
     * @param tabName
     *            タブの名前
     * @param parent
     *            TabFolder 要するに親玉
     */
    public EnvTabItem(String rootDir, Tab tab, TabFolder parent, Main main) {
        super(parent, SWT.NONE);
        this.rootDir = rootDir;
        this.tab = tab;
        this.main = main;
        this.defaultCategoryMap = new LinkedHashMap<String, Category>();
        this.serverGroupImage = new Image(getParent().getDisplay(), getClass().getClassLoader().getResourceAsStream("servers.png"));
        this.serverImage = new Image(getParent().getDisplay(), getClass().getClassLoader().getResourceAsStream("server.png"));
        Image ngImage = new Image(getParent().getDisplay(), getClass().getClassLoader().getResourceAsStream("usernull.png"));
        ImageDescriptor ngDeco = ImageDescriptor.createFromImage(ngImage);
        DecorationOverlayIcon serverGroupIcon = new DecorationOverlayIcon(serverGroupImage, ngDeco, IDecoration.TOP_RIGHT);
        DecorationOverlayIcon serverIcon = new DecorationOverlayIcon(serverImage, ngDeco, IDecoration.TOP_RIGHT);
        this.serverGroupImageUserNull = serverGroupIcon.createImage();
        this.serverImageUserNull = serverIcon.createImage();
        List<String> orderList = main.getToolDefine().getOrderList(rootDir, "category");
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
            this.authTitle = this.tab.getAuth().getTitle();
            this.memoryPwdFlg = this.tab.getAuth().isMemory();
            this.pwdAutoClearFlg = this.tab.getAuth().isAutoclear();
            this.pwdGroup = this.tab.getAuth().getGroup();
            this.optionInputs = this.tab.getAuth().getOptionInputs();
        } else {
            this.authTitle = "認証情報"; // デフォルト表示
            this.optionInputs = new ArrayList<Map<String, Object>>(); // サイズ0で作っておく
        }
        this.optionInputTextMap = new HashMap<String, Text>();

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
        final PreferenceStore ps = main.getPreferenceStore();

        // ==================== 認証設定グループ ====================
        Group authGrp = new Group(composite, SWT.NONE);
        GridLayout authGrpLt = new GridLayout(7, false);
        authGrpLt.marginWidth = 10;
        authGrpLt.horizontalSpacing = 10;
        authGrp.setLayout(authGrpLt);
        GridData authGrpGrDt = new GridData(GridData.FILL_HORIZONTAL);
        authGrpGrDt.horizontalSpan = this.categoryMap.size();
        authGrp.setLayoutData(authGrpGrDt);
        authGrp.setText(this.authTitle);
        authGrp.setEnabled(this.authFlg);

        // 保存されているProxy接続情報を取得（開発用でのみ使用）
        // "userid/password"の形式になってます。
        String defineUserPwd = ps.getString(PreferenceConstants.AUTH_USER_PWD + this.pwdGroup);

        // ---------- ユーザーID ----------
        usrTxt = new Text(authGrp, SWT.BORDER);
        GridData usrTxtGrDt = new GridData(GridData.FILL_HORIZONTAL);
        usrTxtGrDt.horizontalSpan = 2;
        usrTxt.setLayoutData(usrTxtGrDt);
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
                getShell().setImeInputMode(SWT.NONE);
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
        GridData pwdTxtGrDt = new GridData(GridData.FILL_HORIZONTAL);
        pwdTxtGrDt.horizontalSpan = 2;
        pwdTxt.setLayoutData(pwdTxtGrDt);
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
                getShell().setImeInputMode(SWT.NONE);
            }

            @Override
            public void focusLost(FocusEvent arg0) {
            }
        });
        
        // ---------- 認証記憶ボタン ----------
        idPwdMemoryBtn = new Button(authGrp, SWT.PUSH);
        GridData idPwdMemoryBtnGrDt = new GridData(GridData.FILL_VERTICAL);
        idPwdMemoryBtnGrDt.verticalSpan = this.optionInputs.size() + 1;
        idPwdMemoryBtn.setLayoutData(idPwdMemoryBtnGrDt);
        idPwdMemoryBtn.setText("記憶");
        idPwdMemoryBtn.setEnabled(false); // 初期状態では使えなくしておく
        idPwdMemoryBtn.setToolTipText("認証情報を記憶します。");
        idPwdMemoryBtn.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                ps.setValue(PreferenceConstants.AUTH_USER_PWD + pwdGroup, String.format("%s/%s", usrTxt.getText(), pwdTxt.getText()));
                for (String name : optionInputTextMap.keySet()) {
                    ps.setValue(PreferenceConstants.AUTH_OPTION + pwdGroup + "_" + name, optionInputTextMap.get(name).getText());
                }
                try {
                    ps.save();
                    MessageDialog.openInformation(getShell(), "認証情報", "認証情報を記憶しました。");
                } catch (IOException ioe) {
                    MessageDialog.openError(getShell(), "認証情報", "認証情報の記憶に失敗しました。");
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {
            }
        });

        // ---------- 認証チェックボタン ----------
        authCheckBtn = new Button(authGrp, SWT.PUSH);
        authCheckBtn.setImage(new Image(getDisplay(), Main.class.getClassLoader().getResourceAsStream("check_icon.png")));
        GridData authCheckBtnGrDt = new GridData(GridData.FILL_VERTICAL);
        authCheckBtnGrDt.verticalSpan = this.optionInputs.size() + 1;
        authCheckBtn.setLayoutData(authCheckBtnGrDt);
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

        // 認証オプション入力エリア
        for (Map<String, Object> optionInputs : this.optionInputs) {
            Label lbl = new Label(authGrp, SWT.NONE);
            lbl.setText(optionInputs.get("label").toString() + ":");
            Text txt = new Text(authGrp, SWT.BORDER);
            GridData txtGrDt = new GridData(GridData.FILL_HORIZONTAL);
            txtGrDt.horizontalSpan = 4;
            txt.setLayoutData(txtGrDt);
            if (optionInputs.containsKey("message")) {
                txt.setMessage(optionInputs.get("message").toString());
            }
            String initData = ps.getString(PreferenceConstants.AUTH_OPTION + this.pwdGroup + "_" + optionInputs.get("name").toString());
            if (initData != null && !initData.isEmpty()) {
                txt.setText(initData);
            }
            // リスナー
            txt.addModifyListener(new ModifyListener() {
                public void modifyText(ModifyEvent e) {
                    authInputChange();
                    support.firePropertyChange("optionInputs", tab.getAuth().getGroup() + "/" + optionInputs.get("name"), txt.getText());
                }
            });
            // Map保持
            this.optionInputTextMap.put(optionInputs.get("name").toString(), txt);
        }

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
        List<String> groupOrderList = main.getToolDefine().getOrderList(this.rootDir, "group");
        List<String> serverOrderList = main.getToolDefine().getOrderList(this.rootDir, "server");
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
            category.sortTargetNode(groupOrderList, serverOrderList);
            chkTree.setInput(category.getTargetNode());
            for (TargetNode group : category.getTargetNode().getChildren()) {
                addPropertyChangeListener(group);
                for (TargetNode server : group.getChildren()) {
                    addPropertyChangeListener(server);
                }
            }
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
                                if (main.isTtlOnly()) {
                                    MessageDialog.openInformation(getShell(), "TTLマクロ生成", "TTLマクロを生成しました。");
                                }
                            } else {
                                MessageDialog.openError(getShell(), "サーバ接続", "認証情報が入力されていません。");
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
                            builder.append(node.getParentName());
                            builder.append("\r\n");
                            builder.append(node.getName());
                            builder.append("\r\n");
                            builder.append(String.format("%s", node.getHostName()));
                            builder.append("\r\n");
                            builder.append(String.format("%s", node.getIpAddr()));
                            builder.append("\r\n");
                            builder.append(String.format("%s", node.getLoginUsr()));
                            builder.append("\r\n");
                        } else {
                            // 要は親（サーバ種別）の場合
                            builder.append(node.getName());
                            builder.append("\r\n");
                            for (TargetNode nd : node.getChildren()) {
                                builder.append(String.format("%s", nd.getLoginUsr()));
                                builder.append("@");
                                builder.append(String.format("%s", nd.getHostName()));
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

            // ドラッグアンドドロップ
            Transfer[] types = new Transfer[] { FileTransfer.getInstance() };
            DropTarget dropTarget = new DropTarget(tree, DND.DROP_DEFAULT | DND.DROP_MOVE | DND.DROP_COPY);
            dropTarget.setTransfer(types);
            dropTarget.addDropListener(new DropTargetAdapter() {

                @Override
                public void dragEnter(DropTargetEvent event) {
                    if (event.detail == DND.DROP_DEFAULT) {
                        event.detail = DND.DROP_MOVE;
                    }
                }

                @Override
                public void dragOperationChanged(DropTargetEvent event) {
                    if (event.detail == DND.DROP_DEFAULT) {
                        event.detail = DND.DROP_COPY;
                    }
                }

                @Override
                public void drop(DropTargetEvent event) {
                    TargetNode node = (TargetNode) event.item.getData();
                    String[] files = (String[]) event.data;
                    Menu parentMenu = new Menu(getShell(), SWT.POP_UP);
                    for (TeratermStationPlugin plugin : main.getToolDefine().getPluginList(rootDir)) {
                        try {
                            plugin.getClass().getDeclaredMethod("getDnDActions", TargetNode[].class, Object.class, TeratermStationShell.class);
                        } catch (NoSuchMethodException | SecurityException e) {
                            continue;
                        }
                        List<TeratermStationContextMenu> contextMenuList = plugin.getDnDActions(new TargetNode[] { node }, files, getShell());
                        if (contextMenuList != null) { // 拡張機能の無いプラグインはnullを返すので.
                            for (TeratermStationContextMenu contextMenu : contextMenuList) {
                                Menu menu = parentMenu;
                                if (contextMenu.isSubMenu()) {
                                    Menu subMenu = new Menu(parentMenu);
                                    MenuItem subMenuItem = new MenuItem(parentMenu, SWT.CASCADE);
                                    subMenuItem.setText(contextMenu.getText());
                                    subMenuItem.setImage(contextMenu.getImage());
                                    final org.eclipse.swt.widgets.ToolTip toolTip = contextMenu.getToolTip();
                                    subMenuItem.addArmListener(new ArmListener() {
                                        @Override
                                        public void widgetArmed(ArmEvent e) {
                                            if (currentItemToolTip != null && currentItemToolTip.isVisible()) {
                                                currentItemToolTip.setVisible(false);
                                            }
                                            if (toolTip != null) {
                                                toolTip.setVisible(true);
                                                currentItemToolTip = toolTip;
                                            }
                                        }
                                    });
                                    subMenuItem.setMenu(subMenu);
                                    menu = subMenu;
                                }
                                for (final TeratermStationAction action : contextMenu.getActionList()) {
                                    MenuItem item = new MenuItem(menu, SWT.PUSH);
                                    item.setText(action.getText());
                                    item.setImage(action.getImage());
                                    item.addListener(SWT.Selection, new Listener() {
                                        @Override
                                        public void handleEvent(Event event) {
                                            action.run();
                                        }
                                    });
                                    final org.eclipse.swt.widgets.ToolTip toolTip = action.getToolTip();
                                    item.addArmListener(new ArmListener() {
                                        @Override
                                        public void widgetArmed(ArmEvent e) {
                                            if (currentItemToolTip != null && currentItemToolTip.isVisible()) {
                                                currentItemToolTip.setVisible(false);
                                            }
                                            if (toolTip != null) {
                                                toolTip.setVisible(true);
                                                currentItemToolTip = toolTip;
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    }
                    parentMenu.setLocation(event.x, event.y);
                    parentMenu.setVisible(true);
                }
            });

            // あとでTreeViewerに対して一括でなんだかんだやるので、Mapに格納しておく。
            this.treeMap.put(category.getName(), chkTree);

            // ここからサーバツリーの右クリックメニューの設定
            final Menu parentMenu = new Menu(chkTree.getTree());
            chkTree.getTree().setMenu(parentMenu);
            parentMenu.addMenuListener(new MenuAdapter() {
                public void menuShown(MenuEvent event) {
                    MenuItem[] items = parentMenu.getItems();
                    for (MenuItem item : items) {
                        item.dispose();
                    }
                    TargetNode node = (TargetNode) chkTree.getTree().getSelection()[0].getData();
                    for (TeratermStationPlugin plugin : main.getToolDefine().getPluginList(rootDir)) {
                        try {
                            plugin.getClass().getDeclaredMethod("getActions", TargetNode[].class, TeratermStationShell.class);
                        } catch (NoSuchMethodException | SecurityException e) {
                            continue;
                        }
                        List<TeratermStationContextMenu> contextMenuList = plugin.getActions(new TargetNode[] { node }, getShell());
                        if (contextMenuList != null) { // 拡張機能の無いプラグインはnullを返すので.
                            for (TeratermStationContextMenu contextMenu : contextMenuList) {
                                Menu menu = parentMenu;
                                if (contextMenu.isSubMenu()) {
                                    Menu subMenu = new Menu(parentMenu);
                                    MenuItem subMenuItem = new MenuItem(parentMenu, SWT.CASCADE);
                                    subMenuItem.setText(contextMenu.getText());
                                    subMenuItem.setImage(contextMenu.getImage());
                                    final org.eclipse.swt.widgets.ToolTip toolTip = contextMenu.getToolTip();
                                    subMenuItem.addArmListener(new ArmListener() {
                                        @Override
                                        public void widgetArmed(ArmEvent e) {
                                            if (currentItemToolTip != null && currentItemToolTip.isVisible()) {
                                                currentItemToolTip.setVisible(false);
                                            }
                                            if (toolTip != null) {
                                                toolTip.setVisible(true);
                                                currentItemToolTip = toolTip;
                                            }
                                        }
                                    });
                                    subMenuItem.setMenu(subMenu);
                                    menu = subMenu;
                                }
                                for (final TeratermStationAction action : contextMenu.getActionList()) {
                                    MenuItem item = new MenuItem(menu, SWT.PUSH);
                                    item.setText(action.getText());
                                    item.setImage(action.getImage());
                                    item.addListener(SWT.Selection, new Listener() {
                                        @Override
                                        public void handleEvent(Event event) {
                                            action.run();
                                        }
                                    });
                                    final org.eclipse.swt.widgets.ToolTip toolTip = action.getToolTip();
                                    item.addArmListener(new ArmListener() {
                                        @Override
                                        public void widgetArmed(ArmEvent e) {
                                            if (currentItemToolTip != null && currentItemToolTip.isVisible()) {
                                                currentItemToolTip.setVisible(false);
                                            }
                                            if (toolTip != null) {
                                                toolTip.setVisible(true);
                                                currentItemToolTip = toolTip;
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    }
                }
            });
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
                support.firePropertyChange("authUsr", null, tab.getAuth().getGroup() + "/" + usrTxt.getText());
            }
        });

        this.pwdTxt.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                authInputChange();
                support.firePropertyChange("authPwd", null, tab.getAuth().getGroup() + "/" + pwdTxt.getText());
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
        List<String> groupOrderList = main.getToolDefine().getOrderList(this.rootDir, "group");
        List<String> serverOrderList = main.getToolDefine().getOrderList(this.rootDir, "server");
        this.categoryMap = targetMapCopy(this.defaultCategoryMap);
        for (Category category : this.categoryMap.values()) {
            CheckboxTreeViewer treeViewer = this.treeMap.get(category.getName());
            category.sortTargetNode(groupOrderList, serverOrderList);
            treeViewer.setInput(category.getTargetNode());
            for (TargetNode group : category.getTargetNode().getChildren()) {
                addPropertyChangeListener(group);
                for (TargetNode server : group.getChildren()) {
                    addPropertyChangeListener(server);
                }
            }
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
                this.support.firePropertyChange("authInput", null, Boolean.FALSE);
            } else {
                this.support.firePropertyChange("authInput", null, Boolean.TRUE);
            }
        } else {
            this.authInputStatus = true;
            this.idPwdMemoryBtn.setEnabled(this.memoryPwdFlg);
            this.authCheckBtn.setEnabled(this.isValidAuthCheck());
            this.support.firePropertyChange("authInput", null, Boolean.TRUE);
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
        IPreferenceStore ps = main.getPreferenceStore();
        // まずはTTLファイルを作成するディレクトリを取得
        String ttlDir = ps.getString(PreferenceConstants.WORK_DIR);
        File ttlDirFile = new File(ttlDir);
        if (!ttlDirFile.isAbsolute()) {
            try {
                ttlDir = ttlDirFile.getCanonicalPath();
            } catch (IOException e) {
                MessageDialog.openError(getShell(), "実行時エラー", "基本設定にある作業領域（ディレクトリ）はちゃんと作成されていますか？" + e.getMessage());
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
                String base64auth = Base64.getEncoder().encodeToString(String.format("%s:%s", authUsr, authPwd).getBytes());
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
                String iniFile = iniDir + "\\" + tab.getIniFile();
                // ---------- もろもろ情報を取得 ここまで ----------
                Map<String, String> valuesMap = new TreeMap<String, String>();
                valuesMap.put("authuser", authUsr);
                valuesMap.put("authpassword", authPwd);
                valuesMap.put("base64auth", base64auth);
                valuesMap.put("inidir", iniDir);
                valuesMap.put("inifile", iniFile);
                valuesMap.put("logdir", logDir);
                valuesMap.put("workdir", workDir);
                // 認証オプションの値
                for (Map<String, Object> optionInputs : this.optionInputs) {
                    String name = optionInputs.get("name").toString();
                    String value = optionInputTextMap.get(name).getText();
                    valuesMap.put(name, value);
                }

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
                MessageDialog.openError(getShell(), "実行時エラー", "コマンドの生成でエラーが発生しました。\n" + e.getMessage());
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
                    MessageDialog.openError(getShell(), "実行時エラー", "基本設定にある作業領域（ディレクトリ）はちゃんと作成されていますか？" + e.getMessage());
                }
            }
            runtime.exec(new String[] { ttpmacroexe, ttlFile.toString(), pwdArg });
        } catch (FileNotFoundException fnfe) {
            MessageDialog.openError(getShell(), "実行時エラー", "基本設定にある作業領域（ディレクトリ）はちゃんと作成されていますか？" + fnfe.getMessage());
        } catch (Exception e) {
            MessageDialog.openError(getShell(), "実行時エラー", "実行環境に問題があります。初期設定はお済みでしょうか？\n" + e.getMessage());
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
            MessageDialog.openError(getShell(), "一括起動", "対象サーバが選択されていません。");
            return;
        }

        // 念のため確認ダイアログを出す。
        String templateCmd = null;
        String dialogMsg = "一括で接続します。よろしいですか？";
        String[] buttonArray;

        TargetNode[] nodes = checkedTreeList.toArray(new TargetNode[0]);
        List<TeratermStationAction> bulkActionList = new ArrayList<TeratermStationAction>();
        for (TeratermStationPlugin plugin : main.getToolDefine().getPluginList(rootDir)) {
            try {
                plugin.getClass().getDeclaredMethod("getBulkActions", TargetNode[].class, TeratermStationShell.class);
            } catch (NoSuchMethodException | SecurityException e) {
                continue;
            }
            List<TeratermStationAction> actionList = plugin.getBulkActions(nodes, getShell());
            if (actionList != null) { // 一括接続での拡張機能の無いプラグインはnullを返すので.
                bulkActionList.addAll(actionList);
            }
        }

        if (bulkActionList.isEmpty()) {
            buttonArray = new String[] { "OK", "Cancel" };
        } else {
            dialogMsg += "\r\n（拡張機能を利用することもできます）";
            buttonArray = new String[] { "OK", "Cancel", "拡張機能選択..." };
        }
        MessageDialog dialog = new MessageDialog(getShell(), "一括起動", null, dialogMsg, MessageDialog.QUESTION, buttonArray, 0);
        int result = dialog.open();
        switch (result) {
            case 0: // OK
                break;
            case 2: // 拡張機能の利用
                PluginSelectDialog pluginDialog = new PluginSelectDialog(getShell(), bulkActionList);
                int pluginResult = pluginDialog.open();
                if (IDialogConstants.OK_ID != pluginResult) {
                    return;
                }
                TeratermStationAction selectedAction = pluginDialog.getSelectedAction();
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
                MessageDialog.openInformation(getShell(), "TTLマクロ生成", "TTLマクロを生成しました。");
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
        IPreferenceStore ps = main.getPreferenceStore();
        // まずはTTLファイルを作成するディレクトリを取得
        String ttlDir = ps.getString(PreferenceConstants.WORK_DIR);
        File ttlDirFile = new File(ttlDir);
        if (!ttlDirFile.isAbsolute()) {
            try {
                ttlDir = ttlDirFile.getCanonicalPath();
            } catch (IOException e) {
                MessageDialog.openError(getShell(), "実行時エラー", "基本設定にある作業領域（ディレクトリ）はちゃんと作成されていますか？" + e.getMessage());
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
        ttlFile.append(target.getParentName());
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
                MessageDialog.openError(getShell(), "実行時エラー", "コマンドの生成でエラーが発生しました。\n" + e.getMessage());
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
                        MessageDialog.openError(getShell(), "実行時エラー", "基本設定にある作業領域（ディレクトリ）はちゃんと作成されていますか？" + e.getMessage());
                    }
                }
                runtime.exec(new String[] { ttpmacroexe, ttlFile.toString(), pwdArg });
            }
        } catch (FileNotFoundException fnfe) {
            MessageDialog.openError(getShell(), "実行時エラー", "基本設定にある作業領域（ディレクトリ）はちゃんと作成されていますか？" + fnfe.getMessage());
        } catch (Exception e) {
            MessageDialog.openError(getShell(), "実行時エラー", "実行環境に問題があります。初期設定はお済みでしょうか？\n" + e.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    private void rewriteIniFile(String iniFile, Map<String, Object> rewriteMap, StrSubstitutor sub) throws Exception {
        if (rewriteMap == null || rewriteMap.isEmpty()) {
            return;
        }
        File file = new File(iniFile);
        if (!file.exists() || file.isDirectory()) {
            return;
        }
        Map<String, Object> insertMap = targetMapCopyStrObj(rewriteMap);
        List<String> lineBuffer = new ArrayList<String>();
        List<String> rwLineBuffer = new ArrayList<String>();
        BufferedReader br = null;
        BufferedWriter bw = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "SJIS"));
            String line;
            while ((line = br.readLine()) != null) {
                lineBuffer.add(line);
            }
        } catch (IOException e) {
            throw e;
        } finally {
            br.close();
        }
        // まずは既存行の書き換え
        String section = null;
        boolean isSectionMode = false;
        String key = null;
        for (String line : lineBuffer) {
            if (line.isEmpty() || line.startsWith(";")) {
                rwLineBuffer.add(line);
                continue;
            }
            if (line.startsWith("[")) {
                section = line.replaceAll("[\\[\\]]", "");
                isSectionMode = true;
                rwLineBuffer.add(line);
                continue;
            }
            String[] array = line.split("=");
            key = array[0].trim();
            boolean isReplace = false;
            for (Map.Entry<String, Object> e : rewriteMap.entrySet()) {
                // System.out.println(e.getKey() + " : " + e.getValue());
                if (e.getValue() instanceof Map) {
                    // セクション配下
                    if (section.equals(e.getKey()) && isSectionMode) {
                        Map<String, Object> sectionValueMap = (Map<String, Object>) e.getValue();
                        for (Map.Entry<String, Object> e2 : sectionValueMap.entrySet()) {
                            if (key.equals(e2.getKey())) {
                                rwLineBuffer.add(String.format("%s=%s", key, sub.replace(String.valueOf(e2.getValue()))));
                                ((Map<String, Object>) insertMap.get(e.getKey())).remove(e2.getKey());
                                isReplace = true;
                                break;
                            }
                        }
                    }
                }
            }
            if (!isReplace) {
                rwLineBuffer.add(line);
            }
        }
        // そして既存行になかった定義の追記(セクションあり)
        Map<String, Object> addMap = targetMapCopyStrObj(insertMap);
        List<String> fixLineBuffer = new ArrayList<String>();
        for (String line : rwLineBuffer) {
            fixLineBuffer.add(line);
            for (Map.Entry<String, Object> e : insertMap.entrySet()) {
                String insertSection = e.getKey();
                if (e.getValue() instanceof Map) {
                    Map<String, Object> insertSectionValueMap = (Map<String, Object>) e.getValue();
                    if (insertSectionValueMap.isEmpty()) {
                        addMap.remove(insertSection);
                        continue;
                    }
                    if (line.startsWith(String.format("[%s]", insertSection))) {
                        for (Map.Entry<String, Object> e2 : insertSectionValueMap.entrySet()) {
                            fixLineBuffer.add(
                                    String.format("%s=%s", e2.getKey(), sub.replace(String.valueOf(e2.getValue()))));
                        }
                        addMap.remove(insertSection);
                    }
                }
            }
        }
        // そして既存行になかった定義の追記(セクションも追加)
        for (Map.Entry<String, Object> e : addMap.entrySet()) {
            String addSection = e.getKey();
            if (e.getValue() instanceof Map) {
                Map<String, Object> addSectionValueMap = (Map<String, Object>) e.getValue();
                if (addSectionValueMap.isEmpty()) {
                    continue;
                }
                fixLineBuffer.add(String.format("[%s]", addSection));
                for (Map.Entry<String, Object> e2 : addSectionValueMap.entrySet()) {
                    fixLineBuffer.add(String.format("%s=%s", e2.getKey(), sub.replace(String.valueOf(e2.getValue()))));
                }
            }
        }
        try {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "SJIS"));
            for (String line : fixLineBuffer) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            throw e;
        } finally {
            bw.close();
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
            IPreferenceStore ps = main.getPreferenceStore();

            // ---------- もろもろ情報を取得 ここから ----------
            String authUsr = this.usrTxt.getText();
            String authPwd = this.pwdTxt.getText();
            String ipAddr = node.getIpAddr();
            String targetSvr = node.getName();
            String svrType = node.getParentName();
            String loginUsr = node.getLoginUsr();
            String loginPwd = node.getLoginPwd();
            String base64auth = Base64.getEncoder().encodeToString(String.format("%s:%s", authUsr, authPwd).getBytes());
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
            valuesMap.put("base64auth", base64auth);
            valuesMap.put("ipaddress", ipAddr);
            valuesMap.put("loginuser", loginUsr);
            valuesMap.put("loginpassword", loginPwd);
            valuesMap.put("inidir", iniDir);
            valuesMap.put("inifile", iniFile);
            valuesMap.put("logdir", logDir);
            valuesMap.put("workdir", workDir);
            // オプション変数
            for (Map.Entry<String, String> entry : node.getVariable().entrySet()) {
                valuesMap.put(entry.getKey(), entry.getValue());
            }
            // 認証オプションの値
            for (Map<String, Object> optionInputs : this.optionInputs) {
                String name = optionInputs.get("name").toString();
                String value = optionInputTextMap.get(name).getText();
                valuesMap.put(name, value);
            }

            StrSubstitutor sub = new StrSubstitutor(valuesMap);
            rewriteIniFile(iniFile, node.getInirewrite(), sub);

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
        String svrType = node.getParentName();
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
        Map<String, String> valueMap = new HashMap<String, String>();
        // HOSTNAMEをセットしておく
        valueMap.put("HOSTNAME", node.getHostName());
        // あとは変数マップから
        if (node.getVariable() != null) {
            valueMap.putAll(node.getVariable());
        }
        StrSubstitutor sub = new StrSubstitutor(valueMap);
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

    @SuppressWarnings("unchecked")
    private Map<String, Object> targetMapCopyStrObj(Map<String, Object> obj) {
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(obj);
            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            ObjectInputStream in = new ObjectInputStream(byteIn);
            return (Map<String, Object>) in.readObject();
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
            MessageDialog.openError(getShell(), "エラー", "ユーザーディレクトリを作成できませんでした。");
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
            if (node.isParent()) {
                boolean flg = false;
                for (TargetNode child : node.getChildren()) {
                    if (child.getLoginUsr().isEmpty()) {
                        flg |= true;
                    }
                }
                if (flg) {
                    return serverGroupImageUserNull;
                } else {
                    return serverGroupImage;
                }
            } else {
                if (node.getLoginUsr().isEmpty()) {
                    return serverImageUserNull;
                } else {
                    return serverImage;
                }
            }
        }

        @Override
        public String getToolTipText(Object element) {
            TargetNode node = (TargetNode) element;
            StringBuilder builder = new StringBuilder();
            if (node.getChildren().isEmpty()) {
                // 要は子供（サーバ号機）の場合
                String user = node.getLoginUsr();
                builder.append(user);
                builder.append("@");
                builder.append(node.getIpAddr());
                if (node.getHostName() != null) {
                    builder.append(String.format("(%s)", node.getHostName()));
                }
                // ID
                if (node.getId() != null && !node.getId().isEmpty() && !node.getHostName().equals(node.getId())) {
                    builder.append(String.format("[%s]", node.getId()));
                }
                // Procedure
                if (node.getProcedure() != null) {
                    builder.append("\r\n--- procedure ---\r\n");
                    builder.append(node.getProcedure());
                }
                // Variable
                if (node.getVariable() != null && !node.getVariable().isEmpty()) {
                    builder.append("\r\n--- variable ---\r\n");
                    for (String key : node.getVariable().keySet()) {
                        builder.append(String.format("%s: %s\r\n", key, node.getVariable().get(key)));
                    }
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
            boolean flg = false;
            if (childrenNodeList == null || childrenNodeList.isEmpty()) {
                flg |= StringUtils.containsIgnoreCase(targetNode.getName(), filterTxt.getText());
                flg |= StringUtils.containsIgnoreCase(targetNode.getIpAddr(), filterTxt.getText());
                flg |= StringUtils.containsIgnoreCase(targetNode.getHostName(), filterTxt.getText());
            } else {
                for (TargetNode childrenNode : childrenNodeList) {
                    flg |= StringUtils.containsIgnoreCase(childrenNode.getName(), filterTxt.getText());
                    flg |= StringUtils.containsIgnoreCase(childrenNode.getIpAddr(), filterTxt.getText());
                    flg |= StringUtils.containsIgnoreCase(childrenNode.getHostName(), filterTxt.getText());
                }
            }
            return flg;
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

    public void setAuthUsrPwdText(String usrGrp, String usr, String pwdGrp, String pwd, Map<String, Map<String, String>> optionInputs) {
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
        // OptionInputs
        if (this.authFlg) {
            if (optionInputs.containsKey(this.tab.getAuth().getGroup())) {
                Map<String, String> inputs = optionInputs.get(this.tab.getAuth().getGroup());
                for (String key : inputs.keySet()) {
                    String value = inputs.get(key);
                    if (optionInputTextMap.containsKey(key)) {
                        Text txt = optionInputTextMap.get(key);
                        txt.setText(value);
                    }
                }
            }
        }
    }

    public String getRootDir() {
        return rootDir;
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
        this.support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.support.removePropertyChangeListener(listener);
    }

    public Tab getTab() {
        return tab;
    }

    private String getToolTipText(TargetNode node) {
        StringBuilder builder = new StringBuilder();
        if (node.getChildren().isEmpty()) {
            // 要は子供（サーバ号機）の場合
            String user = node.getLoginUsr();
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

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if ("userswitch".equals(event.getPropertyName())) {
            int idx = ((Integer) event.getNewValue()).intValue();
            support.firePropertyChange("userswitch", 0, idx);
        }
    }
}
