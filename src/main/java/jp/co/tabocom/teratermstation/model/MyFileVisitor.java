package jp.co.tabocom.teratermstation.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;

public class MyFileVisitor extends SimpleFileVisitor<Path> {

    private String system;
    private int width;
    private int height;
    private Initial initial;
    private int depthCnt;
    private Map<String, Tab> tabMap;
    private List<File> macroList;
    private String iniFile;

    public MyFileVisitor(int depthCnt) {
        this.initial = new Initial();
        this.depthCnt = depthCnt;
        this.tabMap = new HashMap<String, Tab>();
        this.macroList = new ArrayList<File>();
    }

    @Override
    public FileVisitResult postVisitDirectory(Path arg0, IOException arg1) throws IOException {
        return super.postVisitDirectory(arg0, arg1);
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dirPath, BasicFileAttributes attr) throws IOException {
        String dirName = dirPath.getFileName().toString();
        int depth = dirPath.getNameCount() - this.depthCnt;

        switch (depth) {
        // ========== BASE ========== //
            case 1: {
                System.out.format("基点: %s\n", dirName);
                this.system = dirName;
                break;
            }
            // ========== TAB ========== //
            case 2: { // タブ
                if (dirName.equals("plugins")) {
                    break;
                }
                System.out.format("タブ: %s\n", dirName);
                Tab tab = new Tab();
                tab.setName(dirName);
                tab.setDirPath(dirPath.toString());
                this.tabMap.put(dirName, tab);
                break;
            }
            // ========== GROUP ========== //
            case 3: { // グループ
                System.out.format("カテゴリ: %s\n", dirName);
                Category category = new Category();
                category.setName(dirName);
                Tab tab = this.tabMap.get(dirPath.getName(1 + this.depthCnt).toString());
                category.setTab(tab);
                tab.addCategory(category);
                break;
            }
            // ========== GROUP ========== //
            case 4: { // グループ
                System.out.format("グループ: %s\n", dirName);
                TargetNode node = new TargetNode();
                node.setFile(dirPath.toFile());
                node.setName(dirName);
                Tab tab = this.tabMap.get(dirPath.getName(1 + this.depthCnt).toString());
                Category category = tab.getCategory(dirPath.getName(2 + this.depthCnt).toString());
                node.setCategory(category);
                category.addChild(node);
                break;
            }
            default:
        }
        return super.preVisitDirectory(dirPath, attr);
    }

    @Override
    public FileVisitResult visitFile(Path filePath, BasicFileAttributes attr) throws IOException {
        String fileName = filePath.getFileName().toString();
        File file = filePath.toFile();
        Properties prop = new Properties();
        InputStream inputStream = new FileInputStream(file);
        try {
            prop.load(inputStream);
        } catch (Exception e) {
        } finally {
            inputStream.close();
        }
        int depth = filePath.getNameCount() - this.depthCnt;

        switch (depth) {
            case 2: {
                // ========== SETTINGS.INI ========== //
                if (fileName.equals("settings.ini")) {
                    System.out.println("基本設定");
                    this.width = Integer.parseInt(prop.getProperty("width"));
                    this.height = Integer.parseInt(prop.getProperty("height"));
                    this.iniFile = prop.getProperty("inifile");
                    this.initial.setTtpMacroExe(prop.getProperty("ttpmacroexe"));
                    this.initial.setWorkDir(prop.getProperty("dir_work"));
                    this.initial.setLogDir(prop.getProperty("dir_log"));
                    this.initial.setIniFileDir(prop.getProperty("dir_ini"));
                }
                // ========== *.MACRO ========== //
                if (fileName.endsWith(".macro")) {
                    this.macroList.add(file);
                }
                break;
            }
            case 3: {
                // ========== TAB.INI ========== //
                if (fileName.equals("tab.ini")) {
                    System.out.println("タブ設定");
                    Gateway gw = new Gateway();
                    gw.setGwIpAddr(prop.getProperty("gateway_ipaddress"));
                    gw.setErrPtn(prop.getProperty("gateway_errptn"));
                    gw.setAuth(Boolean.valueOf(prop.getProperty("gateway_auth", "false")));
                    gw.setMemoryPwd(Boolean.valueOf(prop.getProperty("gateway_password_memory", "false")));
                    gw.setPwdAutoClear(Boolean.valueOf(prop.getProperty("gateway_password_autoclear", "false")));
                    gw.setPwdGroup(prop.getProperty("gateway_password_group", "default"));

                    Tab tab = this.tabMap.get(filePath.getName(1 + this.depthCnt).toString());
                    tab.setConnect(prop.getProperty("connect"));
                    tab.setGateway(gw);
                    tab.setNegotiation(prop.getProperty("negotiation"));
                    tab.setLoginUsr(prop.getProperty("loginuser", null));
                    tab.setLoginPwd(prop.getProperty("loginpassword", null));
                    tab.setIniFile(prop.getProperty("inifile"));
                    tab.setUseMacroType(prop.getProperty("usemacro", "none"));
                }
                // ========== ICON.PNG ========== //
                if (fileName.equals("icon.png")) {
                    System.out.println("タブアイコン");
                    Tab tab = this.tabMap.get(filePath.getName(1 + this.depthCnt).toString());
                    tab.setIconPath(filePath.toString());
                }
                // ========== *.MACRO ========== //
                if (fileName.endsWith(".macro")) {
                    Tab tab = this.tabMap.get(filePath.getName(1 + this.depthCnt).toString());
                    tab.addMacro(file);
                }
                break;
            }
            case 4: {
                // ========== CATEGORY.INI ========== //
                if (fileName.equals("category.ini")) {
                    System.out.println("カテゴリ設定");
                    Tab tab = this.tabMap.get(filePath.getName(1 + this.depthCnt).toString());
                    Category category = tab.getCategory(filePath.getName(2 + this.depthCnt).toString());
                    category.setLoginUsr(prop.getProperty("loginuser", null));
                    category.setLoginPwd(prop.getProperty("loginpassword", null));
                    category.setIniFile(prop.getProperty("inifile"));
                    category.setUseMacroType(prop.getProperty("usemacro", "none"));
                }
                // ========== SERVER.TXT ========== //
                if (fileName.endsWith(".txt")) {
                    System.out.println("サーバ定義（グループ所属なし）");
                    TargetNode node = new TargetNode();
                    node.setFile(file);
                    node.setName(FilenameUtils.getBaseName(file.getName())); // 拡張子txtを取り除く
                    node.setHostName(prop.getProperty("hostname"));
                    node.setIpAddr(prop.getProperty("ipaddress"));
                    node.setLoginUsr(prop.getProperty("loginuser"));
                    node.setLoginPwd(prop.getProperty("loginpassword"));
                    node.setUseMacroType(prop.getProperty("usemacro", "none"));
                    Tab tab = this.tabMap.get(filePath.getName(1 + this.depthCnt).toString());
                    Category category = tab.getCategory(filePath.getName(2 + this.depthCnt).toString());
                    node.setCategory(category);
                    category.addChild(node);
                }
                // ========== *.MACRO ========== //
                if (fileName.endsWith(".macro")) {
                    Tab tab = this.tabMap.get(filePath.getName(1 + this.depthCnt).toString());
                    Category category = tab.getCategory(filePath.getName(2 + this.depthCnt).toString());
                    category.addMacro(file);
                }
                break;
            }
            case 5: {
                // ========== GROUP.INI ========== //
                if (fileName.equals("group.ini")) {
                    System.out.println("グループ設定");
                    Tab tab = this.tabMap.get(filePath.getName(1 + this.depthCnt).toString());
                    Category category = tab.getCategory(filePath.getName(2 + this.depthCnt).toString());
                    TargetNode group = category.getChild(filePath.getName(3 + this.depthCnt).toString());
                    group.setLoginUsr(prop.getProperty("loginuser", null));
                    group.setLoginPwd(prop.getProperty("loginpassword", null));
                    group.setIniFile(prop.getProperty("inifile"));
                    group.setUseMacroType(prop.getProperty("usemacro", "none"));
                }
                // ========== SERVER.TXT ========== //
                if (fileName.endsWith(".txt")) {
                    System.out.println("サーバ定義");
                    TargetNode node = new TargetNode();
                    node.setFile(file);
                    node.setName(FilenameUtils.getBaseName(file.getName())); // 拡張子txtを取り除く
                    node.setHostName(prop.getProperty("hostname"));
                    node.setIpAddr(prop.getProperty("ipaddress"));
                    node.setLoginUsr(prop.getProperty("loginuser"));
                    node.setLoginPwd(prop.getProperty("loginpassword"));
                    node.setUseMacroType(prop.getProperty("usemacro", "none"));
                    Tab tab = this.tabMap.get(filePath.getName(1 + this.depthCnt).toString());
                    Category category = tab.getCategory(filePath.getName(2 + this.depthCnt).toString());
                    TargetNode group = category.getChild(filePath.getName(3 + this.depthCnt).toString());
                    group.addChild(node);
                }
                // ========== *.MACRO ========== //
                if (fileName.endsWith(".macro")) {
                    Tab tab = this.tabMap.get(filePath.getName(1 + this.depthCnt).toString());
                    Category category = tab.getCategory(filePath.getName(2 + this.depthCnt).toString());
                    TargetNode group = category.getChild(filePath.getName(3 + this.depthCnt).toString());
                    group.addMacro(file);
                }
                break;
            }
            default:
        }
        // System.out.format("File: %s - %d\n", arg0, arg0.getNameCount());
        return super.visitFile(filePath, attr);
    }

    @Override
    public FileVisitResult visitFileFailed(Path arg0, IOException arg1) throws IOException {
        return super.visitFileFailed(arg0, arg1);
    }

    public String getSystem() {
        return system;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getIniFile() {
        return iniFile;
    }

    public Initial getInitial() {
        return initial;
    }

    public Map<String, Tab> getTabMap() {
        return tabMap;
    }

    public List<File> getMacroList() {
        return macroList;
    }
}
