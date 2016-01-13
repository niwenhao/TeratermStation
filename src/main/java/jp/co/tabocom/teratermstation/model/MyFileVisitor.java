package jp.co.tabocom.teratermstation.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import org.yaml.snakeyaml.Yaml;

import jp.co.tabocom.teratermstation.model.yaml.CategoryIni;
import jp.co.tabocom.teratermstation.model.yaml.GroupIni;
import jp.co.tabocom.teratermstation.model.yaml.SettingsIni;
import jp.co.tabocom.teratermstation.model.yaml.TabIni;

/**
 * サーバ接続定義を読み込むクラスです。
 * 
 * @author turbou
 *
 */
public class MyFileVisitor extends SimpleFileVisitor<Path> {

    private String system;
    private int width;
    private int height;
    private Initial initial;
    private int depthCnt;
    private Map<String, Tab> tabMap;
    private List<String> orderList;
    private String iniFile;

    /**
     * デフォルトコンストラクタ
     * 
     * @param depthCnt
     */
    public MyFileVisitor(int depthCnt) {
        this.initial = new Initial();
        this.depthCnt = depthCnt;
        this.tabMap = new HashMap<String, Tab>();
        this.orderList = new ArrayList<String>();
    }

    @Override
    public FileVisitResult postVisitDirectory(Path arg0, IOException arg1) throws IOException {
        return super.postVisitDirectory(arg0, arg1);
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dirPath, BasicFileAttributes attr) throws IOException {
        String dirName = dirPath.getFileName().toString();
        int depth = dirPath.getNameCount() - this.depthCnt;

        try {
            switch (depth) {
                case 1: {
                    // ========== BASE ========== //
                    System.out.format("基点: %s\n", dirName);
                    this.system = dirName;
                    break;
                }
                case 2: {
                    // ========== TAB ========== //
                    if (dirName.startsWith(".")) {
                        throw new NullPointerException();
                    }
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
                case 3: {
                    // ========== CATEGORY ========== //
                    System.out.format("カテゴリ: %s\n", dirName);
                    Category category = new Category();
                    category.setName(dirName);
                    Tab tab = this.tabMap.get(dirPath.getName(1 + this.depthCnt).toString());
                    category.setTab(tab);
                    tab.addCategory(category);
                    break;
                }
                case 4: {
                    // ========== GROUP ========== //
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
        } catch (NullPointerException npe) {
            // NullPointerが発生するのはだいたいは隠しフォルダ絡みだったりするので一旦ここは無視としておく。
            System.out.format("隠しフォルダまたはその配下なので無視しました -> %s\n", dirPath);
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
                // ========== SETTINGS.YAML ========== //
                if (fileName.equals("settings.yaml")) {
                    System.out.println("基本設定");
                    Yaml yaml = new Yaml();
                    InputStream is = new FileInputStream(file);
                    SettingsIni settingsIni = yaml.loadAs(is, SettingsIni.class);
                    is.close();
                    System.out.println(settingsIni);
                    this.width = settingsIni.getWidth();
                    this.height = settingsIni.getHeight();
                    this.iniFile = settingsIni.getInifile();
                    this.initial.setTtpMacroExe(settingsIni.getInitialTtpmacroexe());
                    this.initial.setWorkDir(settingsIni.getInitialDirWork());
                    this.initial.setLogDir(settingsIni.getInitialDirLog());
                    this.initial.setIniFileDir(settingsIni.getInitialDirIni());
                }
                // ========== ORDER.INI ========== //
                if (fileName.equals("order.ini")) {
                    System.out.println("並び順設定");
                    BufferedReader br = null;
                    try {
                        br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
                        String line;
                        while ((line = br.readLine()) != null) {
                            if (line.length() > 0) {
                                orderList.add(line);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            br.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            }
            case 3: {
                // ========== TAB.YAML ========== //
                if (fileName.equals("tab.yaml")) {
                    System.out.println("タブ設定");
                    Yaml yaml = new Yaml();
                    InputStream is = new FileInputStream(file);
                    TabIni tabIni = yaml.loadAs(is, TabIni.class);
                    is.close();
                    System.out.println(tabIni);
                    Gateway gw = new Gateway();
                    gw.setGwIpAddr(tabIni.getGatewayIpaddress());
                    gw.setAuth(tabIni.isGatewayAuth());
                    gw.setMemoryPwd(tabIni.isGatewayPasswordMemory());
                    gw.setPwdAutoClear(tabIni.isGatewayPasswordAutoclear());
                    gw.setPwdGroup(tabIni.getGatewayPasswordGroup());
                    gw.setAuthcheck(tabIni.getGatewayAuthCheckNegotiation());
                    gw.setAuthcheckOkPtn(tabIni.getGatewayAuthCheckOkPtn());
                    gw.setAuthcheckNgPtn(tabIni.getGatewayAuthCheckNgPtn());

                    Tab tab = this.tabMap.get(filePath.getName(1 + this.depthCnt).toString());
                    tab.setConnect(tabIni.getConnect());
                    tab.setGateway(gw);
                    tab.setNegotiation(tabIni.getNegotiation());
                    tab.setLoginUsr(tabIni.getLoginuser());
                    tab.setLoginPwd(tabIni.getLoginpassword());
                    tab.setIniFile(tabIni.getInifile());
                }
                // ========== ICON.PNG ========== //
                if (fileName.equals("icon.png")) {
                    System.out.println("タブアイコン");
                    Tab tab = this.tabMap.get(filePath.getName(1 + this.depthCnt).toString());
                    tab.setIconPath(filePath.toString());
                }
                break;
            }
            case 4: {
                // ========== CATEGORY.YAML ========== //
                if (fileName.equals("category.yaml")) {
                    System.out.println("カテゴリ設定");
                    Yaml yaml = new Yaml();
                    InputStream is = new FileInputStream(file);
                    CategoryIni categoryIni = yaml.loadAs(is, CategoryIni.class);
                    is.close();
                    System.out.println(categoryIni);
                    Tab tab = this.tabMap.get(filePath.getName(1 + this.depthCnt).toString());
                    Category category = tab.getCategory(filePath.getName(2 + this.depthCnt).toString());
                    category.setLoginUsr(categoryIni.getLoginuser());
                    category.setLoginPwd(categoryIni.getLoginpassword());
                    category.setIniFile(categoryIni.getInifile());
                    category.setProcedure(categoryIni.getProcedure());
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
                    node.setIniFile(prop.getProperty("inifile"));
                    Tab tab = this.tabMap.get(filePath.getName(1 + this.depthCnt).toString());
                    Category category = tab.getCategory(filePath.getName(2 + this.depthCnt).toString());
                    node.setCategory(category);
                    category.addChild(node);
                }
                break;
            }
            case 5: {
                // ========== GROUP.YAML ========== //
                if (fileName.equals("group.yaml")) {
                    System.out.println("グループ設定(YAML)");
                    Yaml yaml = new Yaml();
                    InputStream is = new FileInputStream(file);
                    GroupIni groupIni = yaml.loadAs(is, GroupIni.class);
                    is.close();
                    System.out.println(groupIni);
                    Tab tab = this.tabMap.get(filePath.getName(1 + this.depthCnt).toString());
                    Category category = tab.getCategory(filePath.getName(2 + this.depthCnt).toString());
                    TargetNode group = category.getChild(filePath.getName(3 + this.depthCnt).toString());
                    group.setLoginUsr(groupIni.getLoginuser());
                    group.setLoginPwd(groupIni.getLoginpassword());
                    group.setIniFile(groupIni.getInifile());
                    group.setProcedure(groupIni.getProcedure());
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
                    node.setIniFile(prop.getProperty("inifile"));
                    Tab tab = this.tabMap.get(filePath.getName(1 + this.depthCnt).toString());
                    Category category = tab.getCategory(filePath.getName(2 + this.depthCnt).toString());
                    TargetNode group = category.getChild(filePath.getName(3 + this.depthCnt).toString());
                    group.addChild(node);
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

    public List<String> getOrderList() {
        return orderList;
    }
}
