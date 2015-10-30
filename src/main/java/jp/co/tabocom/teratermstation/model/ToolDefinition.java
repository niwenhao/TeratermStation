package jp.co.tabocom.teratermstation.model;

import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.co.tabocom.teratermstation.plugin.TeratermStationPlugin;

public class ToolDefinition {

    // これは固定。タブ、カテゴリ、グループ、サーバで4階層ということ
    private static final int MAX_DEPTH = 4;

    private Path rootDirPath;
    private String system;
    private int width;
    private int height;

    private Map<String, Tab> tabMap;
    private List<String> orderList;

    private List<TeratermStationPlugin> nodePluginList;

    private Initial initial;

    public ToolDefinition(Path path) {
        this.rootDirPath = path;
    }

    public Map<String, Tab> getTabMap() {
        return tabMap;
    }

    public void setTabMap(Map<String, Tab> tabMap) {
        this.tabMap = tabMap;
    }

    public List<String> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<String> orderList) {
        this.orderList = orderList;
    }

    public List<TeratermStationPlugin> getNodePluginList() {
        return nodePluginList;
    }

    public void setNodePluginList(List<TeratermStationPlugin> nodePluginList) {
        this.nodePluginList = nodePluginList;
    }

    public String getSystem() {
        if (system == null || system.isEmpty()) {
            return "未設定";
        }
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Initial getInitial() {
        return initial;
    }

    public void setInitial(Initial initial) {
        this.initial = initial;
    }

    public Path getRootDirPath() {
        return rootDirPath;
    }

    public void initialize() throws Exception {
        Set<FileVisitOption> options = EnumSet.allOf(FileVisitOption.class);

        // まず先にプラグインJarをロード
        PluginFileVisitor pluginVisitor = new PluginFileVisitor(this.rootDirPath.getNameCount() - 1);
        try {
            Files.walkFileTree(this.rootDirPath, options, MAX_DEPTH, pluginVisitor);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        setNodePluginList(pluginVisitor.getNodePluginList());

        // 次にサーバツリー定義をロード
        MyFileVisitor myVisitor = new MyFileVisitor(this.rootDirPath.getNameCount() - 1);
        try {
            Files.walkFileTree(this.rootDirPath, options, MAX_DEPTH, myVisitor);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        for (Tab tab : myVisitor.getTabMap().values()) {
            // inifile
            if (tab.getIniFile() == null || tab.getIniFile().isEmpty()) {
                tab.setIniFile(myVisitor.getIniFile());
            }
        }
        setSystem(myVisitor.getSystem());
        setWidth(myVisitor.getWidth());
        setHeight(myVisitor.getHeight());
        setInitial(myVisitor.getInitial());
        setTabMap(myVisitor.getTabMap());
        setOrderList(myVisitor.getOrderList());
    }
}
