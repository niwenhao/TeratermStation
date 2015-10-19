package jp.co.tabocom.teratermstation.ui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
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
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
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
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.actions.ActionContext;

import jp.co.tabocom.teratermstation.CommandGenException;
import jp.co.tabocom.teratermstation.Main;
import jp.co.tabocom.teratermstation.model.Category;
import jp.co.tabocom.teratermstation.model.Tab;
import jp.co.tabocom.teratermstation.model.TargetNode;
import jp.co.tabocom.teratermstation.model.UseMacroType;
import jp.co.tabocom.teratermstation.preference.PreferenceConstants;
import jp.co.tabocom.teratermstation.ui.action.TreeViewActionGroup;

public class EnvTabItem extends TabItem {

    private static final int BULK_INTERVAL = 1700;
    private static final int FILE_INTERVAL = 300;

    private Map<String, CheckboxTreeViewer> treeMap;
    private Map<String, Category> targetMap;
    private Map<String, Category> defaultTargetMap;
    private Text usrTxt;
    private Text pwdTxt;
    private Button idPwdMemoryBtn;
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
     * �f�t�H���g�R���X�g���N�^<br>
     * 
     * @param tabName
     *            �^�u�̖��O
     * @param parent
     *            TabFolder �v����ɐe��
     * @param targetList
     *            �\������T�[�o�c���[�̃I�u�W�F�N�g���X�g
     */
    public EnvTabItem(Tab tab, TabFolder parent) {
        super(parent, SWT.NONE);
        this.tab = tab;
        this.defaultTargetMap = new LinkedHashMap<String, Category>();
        for (Category target : tab.getTargetList()) {
            this.defaultTargetMap.put(target.getName(), target);
        }
        this.targetMap = targetMapCopy(this.defaultTargetMap);
        if (this.tab.getGateway() != null) {
            this.authFlg = this.tab.getGateway().isAuth();
            this.memoryPwdFlg = this.tab.getGateway().isMemoryPwd();
            this.pwdAutoClearFlg = this.tab.getGateway().isPwdAutoClear();
            this.pwdGroup = this.tab.getGateway().getPwdGroup();
        }

        if (this.tab.getIconPath() != null && !this.tab.getIconPath().isEmpty()) {
            try {
                this.setImage(new Image(getDisplay(), new FileInputStream(this.tab.getIconPath())));
            } catch (FileNotFoundException fnfe) {
            }
        }
        setText(tab.getName());
        // UI�\�z
        createItemArea();
    }

    private void createItemArea() {
        // �eComposite�̏����i���ꂪUI���i�̐e���j
        final Composite composite = new Composite(getParent(), SWT.NULL);
        composite.setLayout(new GridLayout(this.targetMap.size(), true));

        // �ݒ�̎擾
        final PreferenceStore ps = ((ConnToolTabFolder) getParent()).getMain().getPreferenceStore();

        // ==================== �F�ؐݒ�O���[�v ====================
        Group authGrp = new Group(composite, SWT.NONE);
        GridLayout authGrpLt = new GridLayout(4, false);
        authGrpLt.marginWidth = 10;
        authGrpLt.horizontalSpacing = 10;
        authGrp.setLayout(authGrpLt);
        GridData authGrpGrDt = new GridData(GridData.FILL_HORIZONTAL);
        authGrpGrDt.horizontalSpan = this.targetMap.size();
        authGrp.setLayoutData(authGrpGrDt);
        authGrp.setText("�F�؏��");
        authGrp.setEnabled(this.authFlg);

        // �ۑ�����Ă���Proxy�ڑ������擾�i�J���p�ł̂ݎg�p�j
        // "userid/password"�̌`���ɂȂ��Ă܂��B
        String defineUserPwd = ps.getString(PreferenceConstants.AUTH_USER_PWD + this.pwdGroup);

        // ---------- ���[�U�[ID ----------
        usrTxt = new Text(authGrp, SWT.BORDER);
        usrTxt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        usrTxt.setMessage("���[�U�[ID");
        usrTxt.setEnabled(this.authFlg);
        if (this.memoryPwdFlg) {
            if (defineUserPwd != null && !defineUserPwd.isEmpty()) {
                usrTxt.setText(defineUserPwd.split("/")[0]);
            }
        }
        // IME���[�h�ݒ�
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
            // Enter���������Ƃ��ɂ́A���̃��\�b�h�͌Ăяo����Ȃ�
            public void verifyText(VerifyEvent e) {
                // Backspace��Delete�������ꂽ�Ƃ��͗L���ɂ���B����ƃv���O������������setText���ꂽ�ꍇ���L���ɂ���B
                if (e.character == SWT.BS || e.character == SWT.DEL || e.keyCode == 0) {
                    return;
                }
                // ACCEPTABLE_CHAR�ɒ�`����Ă��镶����ȊO�͖���
                if (ACCEPTABLE_CHAR.indexOf(Character.toString(e.character)) == -1) {
                    e.doit = false;
                }
            }
        });

        Label slashLbl = new Label(authGrp, SWT.NONE);
        slashLbl.setText("/");

        // ---------- �p�X���[�h ----------
        pwdTxt = new Text(authGrp, SWT.BORDER);
        pwdTxt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        pwdTxt.setEchoChar('*');
        pwdTxt.setMessage("�p�X���[�h");
        pwdTxt.setEnabled(this.authFlg);
        if (this.memoryPwdFlg) {
            if (defineUserPwd != null && !defineUserPwd.isEmpty()) {
                pwdTxt.setText(defineUserPwd.split("/")[1]);
            }
        }
        // IME���[�h�ݒ�
        pwdTxt.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent arg0) {
                getParent().getShell().setImeInputMode(SWT.NONE);
            }

            @Override
            public void focusLost(FocusEvent arg0) {
            }
        });

        // ---------- �F�؋L���{�^�� ----------
        idPwdMemoryBtn = new Button(authGrp, SWT.PUSH);
        idPwdMemoryBtn.setText("�L��");
        idPwdMemoryBtn.setEnabled(false); // ������Ԃł͎g���Ȃ����Ă���
        idPwdMemoryBtn.setToolTipText("�F�؏����L�����܂��B");
        idPwdMemoryBtn.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                ps.setValue(PreferenceConstants.AUTH_USER_PWD + pwdGroup, String.format("%s/%s", usrTxt.getText(), pwdTxt.getText()));
                try {
                    ps.save();
                    MessageDialog.openInformation(getParent().getShell(), "�F�؏��", "�F�؏����L�����܂����B");
                } catch (IOException ioe) {
                    MessageDialog.openError(getParent().getShell(), "�F�؏��", "�F�؏��̋L���Ɏ��s���܂����B");
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {
            }
        });

        // ---------- �T�[�o�t�B���^�����O ----------
        filterTxt = new Text(composite, SWT.BORDER);
        GridData filterTxtGrDt = new GridData(GridData.FILL_HORIZONTAL);
        filterTxtGrDt.horizontalSpan = this.targetMap.size() - 1;
        filterTxt.setLayoutData(filterTxtGrDt);
        filterTxt.setMessage("�T�[�o���t�B���^�����O���邱�Ƃ��ł��܂��B��FWebAP, EXE�Ȃ�");
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
            }
        });

        // ---------- �W�J�T�[�o�`�F�b�N�{�^�� ----------
        allCheckBtn = new Button(composite, SWT.PUSH);
        allCheckBtn.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        allCheckBtn.setText("�W�J����Ă���T�[�o��S�ă`�F�b�N");
        allCheckBtn.setToolTipText("�W�J����Ă���T�[�o�S�ĂɃ`�F�b�N�����܂��B");
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

        // ==================== �T�[�o�I���O���[�v ====================
        this.treeMap = new HashMap<String, CheckboxTreeViewer>();
        for (Category target : this.targetMap.values()) {
            Group targetSubGrp = new Group(composite, SWT.NONE);
            GridLayout targetSubGrpLt = new GridLayout(1, false);
            targetSubGrp.setLayout(targetSubGrpLt);
            targetSubGrp.setLayoutData(new GridData(GridData.FILL_BOTH));
            targetSubGrp.setText(target.getName());
            final CheckboxTreeViewer chkTree = new CheckboxTreeViewer(targetSubGrp, SWT.BORDER | SWT.VIRTUAL);
            chkTree.setContentProvider(new TreeContentProvider());
            ColumnViewerToolTipSupport.enableFor(chkTree, ToolTip.NO_RECREATE);
            chkTree.setLabelProvider(new TreeLabelProvider());
            chkTree.setInput(target.getTargetNode());
            final Tree tree = chkTree.getTree();
            tree.setLayoutData(new GridData(GridData.FILL_BOTH));
            tree.setToolTipText("�Ώۂ̃T�[�o�Ƀ`�F�b�N�����Ă��������B");
            // ---------- �ΏۃT�[�o�I��CheckBoxTreeViewer�`�F�b�N���X�i�[ ----------
            chkTree.addCheckStateListener(new ICheckStateListener() {
                public void checkStateChanged(CheckStateChangedEvent event) {
                    // �`�F�b�N�{�b�N�X�Ƀ`�F�b�N�������ꂽ��A����ɉ����Đe�Ƃ��q�̃m�[�h�Ƀ`�F�b�N��A�����ē����Ƃ������������Ƃ����Ă���B
                    viewerRefreshForTreeViewer(chkTree, event.getElement(), event.getChecked());
                }
            });

            chkTree.addDoubleClickListener(new IDoubleClickListener() {
                @Override
                public void doubleClick(DoubleClickEvent event) {
                    // �������Ă��邩�Ƃ����ƁA�v�͐e�m�[�h���_�u���N���b�N���ꂽ��A�c���[��W�J����Ƃ��A�t�ɕ���Ƃ��̏��������Ă���B
                    IStructuredSelection selection = (IStructuredSelection) event.getSelection();
                    if (!selection.isEmpty()) {
                        TreeViewer treeViewer = (TreeViewer) event.getSource();
                        Object selectedObject = selection.getFirstElement();
                        TargetNode node = (TargetNode) selectedObject;
                        if (node.getIpAddr() != null) {
                            if (!authFlg || authInputStatus) {
                                makeAndExecuteTTL(node, 1, null);
                            } else {
                                MessageDialog.openError(getParent().getShell(), "�T�[�o�ڑ�", "�F�؏�񂪓��͂���Ă��܂���B");
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

            // Ctrl+c�ɑΉ�
            tree.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.stateMask == SWT.CTRL && e.keyCode == 99) {
                        TreeItem item = tree.getSelection()[0];
                        TargetNode node = (TargetNode) item.getData();
                        StringBuilder builder = new StringBuilder();
                        if (node.getChildren().isEmpty()) {
                            // �v�͎q���i�T�[�o���@�j�̏ꍇ
                            builder.append(String.format("%-8s", node.getHostName()));
                            builder.append(",");
                            builder.append(String.format("%-15s", node.getIpAddr()));
                            builder.append(",");
                            builder.append(node.getParent().getName());
                            builder.append(",");
                            builder.append(node.getName());
                        } else {
                            // �v�͐e�i�T�[�o��ʁj�̏ꍇ
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
                        // �N���b�v�{�[�h
                        Clipboard clipBoard = new Clipboard(composite.getShell().getDisplay());
                        clipBoard.setContents(new Object[] { builder.toString() }, new Transfer[] { TextTransfer.getInstance() });
                    }
                }
            });

            // ���Ƃ�TreeViewer�ɑ΂��Ĉꊇ�łȂ񂾂��񂾂��̂ŁAMap�Ɋi�[���Ă����B
            this.treeMap.put(target.getName(), chkTree);

            // ��������T�[�o�c���[�̉E�N���b�N���j���[�̐ݒ�
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
        bottomGrpGrDt.horizontalSpan = this.targetMap.size();
        bottomGrp.setLayoutData(bottomGrpGrDt);

        // ==================== �T�[�o�I����� �ۑ��A�Ǎ��O���[�v ====================
        Composite defaultGrp = new Composite(bottomGrp, SWT.NONE);
        defaultGrp.setLayout(new GridLayout(1, true));
        GridData defaultGrpGrDt = new GridData(GridData.FILL_HORIZONTAL);
        defaultGrpGrDt.horizontalAlignment = SWT.LEFT;
        // ---------- �f�t�H���g�̕����{�^�� ----------
        Button defaultBtn = new Button(defaultGrp, SWT.PUSH);
        defaultBtn.setImage(new Image(getDisplay(), Main.class.getClassLoader().getResourceAsStream("refresh-icon.png")));
        defaultBtn.setText("�f�t�H���g�̕���");
        defaultBtn.setToolTipText("�T�[�o�c���[���f�t�H���g�̏�Ԃɖ߂��܂��B");
        defaultBtn.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                defaultTreeData();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });

        // ==================== ���[�U�[ID�ƃp�X���[�h�̃��X�i�[�o�^ ====================
        this.usrTxt.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                authInputChange();
                propertyChangeSupport.firePropertyChange("authUsr", null, tab.getGateway().getPwdGroup() + "/" + usrTxt.getText());
            }
        });

        this.pwdTxt.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                authInputChange();
                propertyChangeSupport.firePropertyChange("authPwd", null, tab.getGateway().getPwdGroup() + "/" + pwdTxt.getText());
            }
        });

        setControl(composite);
    }

    public boolean isAnythingChecked() {
        // �܂�CheckBoxTreeViewer�Ń`�F�b�N�̓����Ă�����̂��擾����B
        List<Object> checkedTreeList = new ArrayList<Object>();
        for (CheckboxTreeViewer tree : treeMap.values()) {
            checkedTreeList.addAll(Arrays.asList(tree.getCheckedElements()));
        }
        if (checkedTreeList.isEmpty()) {
            // �P���`�F�b�N����Ă��Ȃ�������false��Ԃ��B
            return false;
        }
        return true;
    }

    public Map<String, Category> getTargetMap() {
        return targetMap;
    }

    public void propertyChangeUpdate() {
        authInputChange();
    }

    private void defaultTreeData() {
        this.targetMap = targetMapCopy(this.defaultTargetMap);
        for (Category target : this.targetMap.values()) {
            CheckboxTreeViewer treeViewer = this.treeMap.get(target.getName());
            treeViewer.setInput(target.getTargetNode());
            treeViewer.refresh();
        }
        this.filterTxt.setText("");
    }

    /**
     * authInputChange<br>
     * ���[�U�[ID�A�p�X���[�h�̓��͏�Ԃ��`�F�b�N���āA�F�؃{�^����ꊇ�N���{�^���̏�Ԃ�ύX���Ă܂��B
     */
    private void authInputChange() {
        if (this.usrTxt.getText().isEmpty() || this.pwdTxt.getText().isEmpty()) {
            this.authInputStatus = false;
            this.idPwdMemoryBtn.setEnabled(false);
            if (this.authFlg) {
                this.propertyChangeSupport.firePropertyChange("authInput", null, Boolean.FALSE);
            } else {
                this.propertyChangeSupport.firePropertyChange("authInput", null, Boolean.TRUE);
            }
        } else {
            this.authInputStatus = true;
            this.idPwdMemoryBtn.setEnabled(this.memoryPwdFlg);
            this.propertyChangeSupport.firePropertyChange("authInput", null, Boolean.TRUE);
        }
    }

    /**
     * refreshTree<br>
     * �S�Ă�TreeViewer�̏�Ԃ��X�V���čĕ`�悵�܂��B
     */
    public void refreshTree() {
        for (CheckboxTreeViewer tree : treeMap.values()) {
            tree.refresh();
        }
    }

    /**
     * bulkConnection<br>
     * �ꊇ�N���{�^��������s����鏈���ł��B
     */
    public void bulkConnection() {
        // �܂�CheckBoxTreeViewer�Ń`�F�b�N�̓����Ă�����̂��擾����B
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
            // �P���`�F�b�N����Ă��Ȃ�������G���[�_�C�A���O���o���ďI��
            MessageDialog.openError(getParent().getShell(), "�ꊇ�N��", "�ΏۃT�[�o���I������Ă��܂���B");
            return;
        }
        ConnToolTabFolder tabFolder = (ConnToolTabFolder) getParent();
        Main main = (Main) tabFolder.getMain();

        // �O�̂��ߊm�F�_�C�A���O���o���B
        String templateCmd = null;
        String dialogMsg = "�ꊇ�Őڑ����܂��B��낵���ł����H";
        String[] buttonArray;
        // �{�Ԓ[�����ł̓e���v���[�g�@�\���g���Ȃ����Ă܂��B
        if (this.tab.getUseMacroType() != UseMacroType.UNUSED) {
            dialogMsg += "\r\n�i�e���v���[�g��I�����邱�Ƃ��ł��܂��j";
            buttonArray = new String[] { "OK", "Cancel", "�e���v���[�g�I��..." };
        } else {
            buttonArray = new String[] { "OK", "Cancel" };
        }
        MessageDialog dialog = new MessageDialog(getParent().getShell(), "�ꊇ�N��", null, dialogMsg, MessageDialog.QUESTION, buttonArray, 0);
        int result = dialog.open();
        switch (result) {
            case 0: // OK
                break;
            case 2: // Template
                FileDialog fileDialog = new FileDialog(getParent().getShell());
                fileDialog.setText("�e���v���[�g�t�@�C����I�����Ă��������B");
                fileDialog.setFilterPath(tab.getDirPath());
                fileDialog.setFilterExtensions(new String[] { "*.macro" });
                String file = fileDialog.open();
                if (file == null) {
                    return;
                }
                File templateFile = new File(file);
                try {
                    templateCmd = genTemplateCmd(templateFile);
                } catch (Exception e) {
                    MessageDialog.openError(getParent().getShell(), "���s���G���[", "�R�}���h�̐����ŃG���[���������܂����B\n" + e.getMessage());
                    return;
                }
                break;
            default: // Cancel or Other
                return;
        }
        try {
            // �`�F�b�N����Ă���m�[�h���ׂĂŎ��s���܂��B�������e�m�[�h�i�T�[�o��ʂ�\���m�[�h�j�͑ΏۊO�ł��B
            int idx = 1;
            for (TargetNode target : checkedTreeList) {
                makeAndExecuteTTL(target, idx, templateCmd);
                idx++;
                Thread.sleep(BULK_INTERVAL); // �X���[�v���Ȃ��Ă����͂Ȃ����ǁA���܂�ɂ��A���Ń^�[�~�i�����J���̂�����Ȃ̂ŁB
            }
            if (main.isTtlOnly()) {
                // TTL�t�@�C���̍쐬�݂̂�������A�t�@�C���쐬��A�_�C�A���O���o���B
                MessageDialog.openInformation(getParent().getShell(), "TTL�}�N������", "TTL�}�N���𐶐����܂����B");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * makeAndExecuteTTL<br>
     * �^����ꂽ��񂩂�TTL�𐶐����Ď��s���܂��B
     * 
     * @param target
     *            �ΏۃT�[�o�̃m�[�h
     * @param befaft
     *            �����敪
     */
    public void makeAndExecuteTTL(TargetNode target, int idx, String templateCmd) {
        // �ݒ�N���X���擾
        IPreferenceStore ps = ((ConnToolTabFolder) getParent()).getMain().getPreferenceStore();
        // �܂���TTL�t�@�C�����쐬����f�B���N�g�����擾
        String ttlDir = ps.getString(PreferenceConstants.WORK_DIR);
        if (this.authFlg) {
            ttlDir = ttlDir + "\\" + this.usrTxt.getText();
            if (!makeUserDirectory(ttlDir)) {
                return;
            }
        }
        // ========== �t�@�C���p�X���� �������� ==========
        // ��j C:\library\work\4��-�VWebAP_con_t-shiozaki.ttl
        StringBuilder ttlFile = new StringBuilder(ttlDir);
        ttlFile.append("\\");
        ttlFile.append(target.getParent().getName());
        ttlFile.append("-");
        ttlFile.append(target.getName());
        ttlFile.append("_");
        ttlFile.append(this.usrTxt.getText());
        ttlFile.append(".ttl");
        // ========== �t�@�C���p�X���� �����܂� ==========

        File outputFile = new File(ttlFile.toString());
        try {
            FileOutputStream fos = new FileOutputStream(outputFile);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            PrintWriter pw = new PrintWriter(osw);
            try {
                // ++++++++++++++++++++ �ڑ��A�F�ؕ�����̎擾 ++++++++++++++++++++ //
                pw.println(genConnText(target, idx, templateCmd));
            } catch (CommandGenException cge) {
                MessageDialog.openError(getParent().getShell(), "���s���G���[", "�R�}���h�̐����ŃG���[���������܂����B\n" + cge.getMessage());
                return;
            } finally {
                pw.close();
            }
            // �uTTL�t�@�C���̍쐬�̂݁v�Ƀ`�F�b�N�������Ă��邩�擾
            ConnToolTabFolder tabFolder = (ConnToolTabFolder) getParent();
            if (!((Main) tabFolder.getMain()).isTtlOnly()) { // TTL�쐬�݂̂łȂ�������{���Ɏ��s����
                Thread.sleep(FILE_INTERVAL);
                Runtime runtime = Runtime.getRuntime();
                String pwdArg = this.pwdTxt.getText();
                String ttpmacroexe = ps.getString(PreferenceConstants.TTPMACRO_EXE);
                runtime.exec(new String[] { ttpmacroexe, ttlFile.toString(), pwdArg });
            }
        } catch (FileNotFoundException fnfe) {
            MessageDialog.openError(getParent().getShell(), "���s���G���[", "��{�ݒ�ɂ����Ɨ̈�i�f�B���N�g���j�͂����ƍ쐬����Ă��܂����H" + fnfe.getMessage());
        } catch (Exception e) {
            MessageDialog.openError(getParent().getShell(), "���s���G���[", "���s���ɖ�肪����܂��B�����ݒ�͂��ς݂ł��傤���H\n" + e.getMessage());
        }
    }

    /**
     * genConnText<br>
     * �^����ꂽ��񂩂�ڑ��p�̃l�S�V�G�[�V����������𐶐����ĕԂ��܂��B
     * 
     * @param node
     *            �ΏۃT�[�o���
     * @return �ڑ��p�̃l�S�V�G�[�V����������
     */
    private String genConnText(TargetNode node, int idx, String templateCmd) throws CommandGenException {
        StringBuilder word = new StringBuilder();
        try {
            // �ݒ�N���X���擾
            IPreferenceStore ps = ((ConnToolTabFolder) getParent()).getMain().getPreferenceStore();

            // ---------- �����������擾 �������� ----------
            String authUsr = this.usrTxt.getText();
            String ipAddr = node.getIpAddr();
            String targetSvr = node.getName();
            String svrType = node.getParent().getName();
            String loginUsr = node.getLoginUsr();
            String loginPwd = node.getLoginPwd();
            // INI�t�@�C��
            String iniFile = ps.getString(PreferenceConstants.INIFILE_DIR) + "\\" + node.getIniFile();
            String seqNo = String.format("%03d. ", idx);
            // ---------- �����������擾 �����܂� ----------
            Map<String, String> valuesMap = new TreeMap<String, String>();
            valuesMap.put("authuser", authUsr);
            valuesMap.put("authpassword", "PASSWORD"); // �ڑ��̏ꍇ�͔F�؃p�X���[�h�͈����n���Ȃ̂ŕϐ������Ă����B
            valuesMap.put("ipaddress", ipAddr);
            valuesMap.put("loginuser", loginUsr);
            valuesMap.put("loginpassword", loginPwd);
            if (this.tab.getGateway() != null) {
                valuesMap.put("gateway_ipaddress", this.tab.getGateway().getGwIpAddr());
            }
            valuesMap.put("inifile", iniFile);

            StrSubstitutor sub = new StrSubstitutor(valuesMap);

            String connect = sub.replace(this.tab.getConnect());

            String NEW_LINE = System.getProperty("line.separator");
            if (this.tab.getNegotiation() != null && this.authFlg) {
                word.append("PASSWORD=param2" + NEW_LINE); // �F�؃p�X���[�h�̓Z�L�����e�B�̂��߃}�N�����s�����œn���܂��B
                word.append("strlen PASSWORD" + NEW_LINE);
                word.append("if result = 0 then" + NEW_LINE);
                word.append("    passwordbox '�p�X���[�h����͂��Ă��������B[" + authUsr + "]' '�F��'" + NEW_LINE);
                word.append("    strlen inputstr" + NEW_LINE);
                word.append("    if result = 0 then" + NEW_LINE);
                word.append("        exit" + NEW_LINE);
                word.append("    else" + NEW_LINE);
                word.append("        PASSWORD=inputstr" + NEW_LINE);
                word.append("    endif" + NEW_LINE);
                word.append("endif" + NEW_LINE);
            }
            word.append(connect + NEW_LINE);
            word.append("settitle '" + seqNo + svrType + " - " + targetSvr + "'" + NEW_LINE); // �^�C�g���̓T�[�o��ʂƃT�[�o��
            word.append(genLogOpen(node));
            if (this.tab.getNegotiation() != null) {
                for (String negoLine : sub.replace(this.tab.getNegotiation()).split("\r\n")) {
                    word.append(negoLine.trim() + NEW_LINE);
                }
            }
            // �����܂�

            // �e���v���[�g�Ή�
            word.append(genTemplateCmd(node, templateCmd));
        } catch (Exception e) {
            e.printStackTrace();
            throw new CommandGenException(e);
        }
        return word.toString();
    }

    private String genLogOpen(TargetNode node) {
        // �ݒ�N���X���擾
        IPreferenceStore ps = ((ConnToolTabFolder) getParent()).getMain().getPreferenceStore();
        String logDir = ps.getString(PreferenceConstants.LOG_DIR);

        // �[�������擾
        String pcName = System.getenv("COMPUTERNAME");

        // �^�C���X�^���v���擾
        Calendar objCal = Calendar.getInstance();
        SimpleDateFormat monthFmt = new SimpleDateFormat("yyyyMM");
        SimpleDateFormat dateFmt = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat timeFmt = new SimpleDateFormat("yyyyMMdd-HHmmss");
        String month = monthFmt.format(objCal.getTime());
        String date = dateFmt.format(objCal.getTime());
        String timestamp = timeFmt.format(objCal.getTime());

        // ���O�t�@�C���̃p�X�͌��ǂ͉��̂悤�ȍ\���ł��B
        // C:\library\log\201207\20120710\20120710-121212_WebAP_WebAP(A)#1_PTP95049.log
        String monthDir = logDir + "\\" + month;
        String dateDir = logDir + "\\" + month + "\\" + date;
        String[] dirArray = new String[] { logDir, monthDir, dateDir };

        // ���O�t�@�C��
        String svrType = node.getParent().getName();
        String targetSvr = node.getName();
        String logFile = dateDir + "\\" + timestamp + "_" + svrType + "_" + targetSvr + "_" + pcName + ".log";

        StringBuilder word = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        // dirArray�ɂ� 'YYYYMM' �� 'YYYYMM\YYYYMMDD' �������Ă���̂�
        // ���̃��[�v�ł͍ŏ���YYYYMM�ɂ��ă`�F�b�N�Ȃ���΍쐬���s��
        // ����YYYYMMDD�̃`�F�b�N�Ȃ���΍쐬������Ă���B
        // �܂��f�B���N�g�����Ⴄ�����ł܂��������������Ȃ̂Ń��[�v�ɂ��������ł��B
        for (String dir : dirArray) {
            word.append("logdir = '" + dir + "'" + NEW_LINE);
            word.append("filesearch logdir" + NEW_LINE);
            word.append("if result = 0 then" + NEW_LINE);
            word.append("    foldercreate logdir" + NEW_LINE);
            word.append("    if result != 0 then" + NEW_LINE);
            word.append("        desc = '\\n�̃f�B���N�g���쐬�Ɏ��s���܂����B\\n�Ǘ��҂ɖ₢���킹�Ă��������B'" + NEW_LINE);
            word.append("        strspecial desc" + NEW_LINE);
            word.append("        sprintf2 msg '%s%s' logdir desc" + NEW_LINE);
            word.append("        messagebox msg ''" + NEW_LINE);
            word.append("        closett" + NEW_LINE);
            word.append("        end" + NEW_LINE);
            word.append("    endif" + NEW_LINE);
            word.append("endif" + NEW_LINE);
        }
        // ���O��OPEN
        word.append("logopen '" + logFile + "' 0 0 0 0 1" + NEW_LINE); // ���O�̃_�C�A���O���o���Ȃ��悤�ɂ��Ă܂��B�Ō�̂P�������B�ڍׂ�Teraterm�}�N���̃w���v���Ă��������B
        return word.toString();
    }

    private String genTemplateCmd(File templateFile) throws Exception {
        StringBuilder word = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        BufferedReader br = null;
        // �e���v���[�g�t�@�C���̊e�s�ێ�List
        List<String> lines = new ArrayList<String>();
        // �ϊ�����L�[�ƒl�ێ�Map
        Map<String, String> valuesMap = new TreeMap<String, String>();
        // �ꎞ�I���l�ێ��pMap
        Map<String, String> answerMap = new HashMap<String, String>();
        try {
            // �܂��ŏ���List�Ɋe�s��ǂݍ���ł��܂��B
            br = new BufferedReader(new FileReader(templateFile));
            String readLine;
            while ((readLine = br.readLine()) != null) {
                lines.add(readLine);
            }

            // ���Ƀe���v���[�g�t�@�C�����̕ϐ����E���o���B
            for (String line : lines) {
                if (line.startsWith("@")) {
                    valuesMap.put(line.replaceFirst("@", ""), "");
                }
            }

            // �����Ēu�����ׂ��ϐ����܂킵�Ēl����͂��Ă��炤�B
            for (String key : valuesMap.keySet()) {
                String dialogMsg = String.format("���̃L�[[ %s ]�ɑΉ�����l����͂��Ă��������B", key);
                final String errorMsg = "�u������l����͂��Ă��������B";
                InputDialog dialog = new InputDialog(getParent().getShell(), "�e���v���[�g������u��", dialogMsg, "", new IInputValidator() {
                    @Override
                    public String isValid(String str) {
                        try {
                            if (str.isEmpty()) {
                                return errorMsg;
                            }
                        } catch (NumberFormatException nfe) {
                            return errorMsg;
                        }
                        return null;
                    }
                });
                if (dialog.open() == Dialog.OK) {
                    answerMap.put(key, dialog.getValue());
                }
            }

            if (valuesMap.size() != answerMap.size()) {
                throw new IllegalArgumentException("�e���v���[�g�ϐ��ɑ΂���u�������񂪎w�肳��Ă��܂���B");
            }

            // ���͂��Ă�������l��valuesMap�ɑ������B
            for (String key : answerMap.keySet()) {
                valuesMap.put(key, answerMap.get(key));
            }

            // �ϊ��p�̃N���X�𐶐�����B
            StrSubstitutor sub = new StrSubstitutor(valuesMap);
            // ���߂ăe���v���[�g����ttl�����쐬����B
            for (String line : lines) {
                // �ϐ��ɑ�������s�͖�������B
                if (line.startsWith("@")) {
                    continue;
                }

                String resolvedLine = sub.replace(line);
                // �V���O���N�H�[�e�[�V�����������悤�ɂ��Ă����B
                String correctLine = resolvedLine.replaceAll("'", Matcher.quoteReplacement("'#$27'"));
                if (correctLine.contains("?")) {
                    String waitStr = correctLine.split("\\?")[0];
                    String cmdStr = correctLine.split("\\?")[1].trim();
                    word.append("wait '" + waitStr + "'" + NEW_LINE);
                    word.append("sendln '" + cmdStr + "'" + NEW_LINE);
                } else {
                    word.append("wait ']$ '" + NEW_LINE);
                    word.append("sendln '" + correctLine.trim() + "'" + NEW_LINE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
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
            MessageDialog.openError(getParent().getShell(), "�G���[", "���[�U�[�f�B���N�g�����쐬�ł��܂���ł����B");
            return false;
        }
        return true;
    }

    /**
     * TreeContentProvider<br>
     * <p>
     * ���̃N���X��TreeViewer�ŕK�v�ł��B �`���΂������̂Ȃ̂ŁA���܂蒆�g��m��Ȃ��ėǂ��ł��B �ǂ�����TreeViewer���g�������ꍇ�͂��̕ӂ��K�v�Ȃ̂ŃR�s�[���Ďg���Ă��������B
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
     * �����TreeViewer�ɕK�v�ȃN���X�ł��B �ł���̂���͏d�v��<br>
     * �v��TreeViewer�̃m�[�h�̃^�C�g���Ƃ����ǂ̂悤�ɕ\�����邩 ���`����N���X�ł��B getText���\�b�h�͕\�������^�C�g����Ԃ��܂��B<br>
     * getToolTipText�̓}�E�X���m�[�h�ɏd�˂����ɂł�q���g������ł��B
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
                // �v�͎q���i�T�[�o���@�j�̏ꍇ
                builder.append(node.getLoginUsr());
                builder.append("@");
                builder.append(node.getIpAddr());
                if (node.getHostName() != null) {
                    builder.append(" [");
                    builder.append(node.getHostName());
                    builder.append("]");
                }
            } else {
                // �v�͐e�i�T�[�o��ʁj�̏ꍇ
                builder.append(node.getName());
                builder.append("(");
                builder.append(node.getChildren().size());
                builder.append("��)");
            }
            return builder.toString();
        }

        @Override
        public Font getToolTipFont(Object object) {
            FontRegistry fontRegistry = new FontRegistry(getDisplay());
            fontRegistry.put("MSGothic", new FontData[] { new FontData("�l�r �S�V�b�N", 9, SWT.NORMAL) });
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
     * ���̃��\�b�h�̒��g�͕��G�œ���̂Œ��g�����Ȃ��Ă��ǂ��ł��B<br>
     * �ꉞ�A�������Ă邩�Ƃ����ƁA����q�m�[�h���`�F�b�N������e���`�F�b�N������悤�ɂ���Ƃ�<br>
     * �e�m�[�h���`�F�b�N������A���̔z���̎q�m�[�h�S���Ƀ`�F�b�N������Ƃ����Ă܂��B
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
            if (usrGrp.equals(this.tab.getGateway().getPwdGroup())) {
                this.usrTxt.setText(usr);
            }
        }
        if (pwdGrp != null && pwd != null) {
            if (pwdGrp.equals(this.tab.getGateway().getPwdGroup())) {
                this.pwdTxt.setText(pwd);
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

    public Tab getTab() {
        return this.tab;
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

    @Override
    protected void checkSubclass() {
        // super.checkSubclass();
    }
}
