package jp.co.tabocom.teratermstation.model;

import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.co.tabocom.teratermstation.exception.FormatException;
import jp.co.tabocom.teratermstation.plugin.TeratermStationPlugin;

import org.yaml.snakeyaml.error.YAMLException;

public class ToolDefinition {

    // これは固定。タブ、カテゴリ、グループ、サーバで4階層ということ
    private static final int MAX_DEPTH = 4;

    private Path rootDirPath;
    private List<String> rootDirList;
    private String system;

    private Map<String, Tab> tabMap;
    private List<String> orderList;

    private Map<String, Map<String, Tab>> tabMapMap;
    private Map<String, List<String>> orderListMap;

    private List<TeratermStationPlugin> pluginList;
    private List<Exception> loadExceptionList;

    private Map<String, List<TeratermStationPlugin>> pluginListMap;
    private Map<String, List<Exception>> loadExceptionListMap;

    public ToolDefinition(Path path) {
        this.rootDirPath = path;
    }

    public ToolDefinition(List<String> rootDirList) {
        this.rootDirList = rootDirList;
        this.tabMapMap = new HashMap<String, Map<String, Tab>>();
        this.orderListMap = new HashMap<String, List<String>>();
        this.pluginListMap = new HashMap<String, List<TeratermStationPlugin>>();
        this.loadExceptionListMap = new HashMap<String, List<Exception>>();
    }

    public List<String> getRootDirList() {
        return rootDirList;
    }

    public boolean isTabMapEmpty() {
        if (this.tabMapMap == null) {
            return true;
        }
        return this.tabMapMap.isEmpty();
    }

    public Map<String, Tab> getTabMap() {
        return tabMap;
    }

    public void setTabMap(Map<String, Tab> tabMap) {
        this.tabMap = tabMap;
    }

    public void addTabMap(String rootDir, Map<String, Tab> tabMap) {
        this.tabMapMap.put(rootDir, tabMap);
    }

    public Map<String, Tab> getTabMap(String rootDir) {
        return tabMapMap.get(rootDir);
    }

    public List<String> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<String> orderList) {
        this.orderList = orderList;
    }

    public void addOrderList(String rootDir, List<String> orderList) {
        this.orderListMap.put(rootDir, orderList);
    }

    public List<String> getOrderList(String rootDir) {
        return orderListMap.get(rootDir);
    }

    public List<TeratermStationPlugin> getPluginList() {
        return pluginList;
    }

    public void setPluginList(List<TeratermStationPlugin> pluginList) {
        this.pluginList = pluginList;
    }

    public void addPluginList(String rootDir, List<TeratermStationPlugin> pluginList) {
        this.pluginListMap.put(rootDir, pluginList);
    }

    public List<TeratermStationPlugin> getPluginList(String rootDir) {
        return pluginListMap.get(rootDir);
    }

    public List<Exception> getLoadExceptionList() {
        return loadExceptionList;
    }

    public void setLoadExceptionList(List<Exception> loadExceptionList) {
        this.loadExceptionList = loadExceptionList;
    }

    public void addLoadExceptionList(String rootDir, List<Exception> loadExceptionList) {
        this.loadExceptionListMap.put(rootDir, loadExceptionList);
    }

    public List<Exception> getLoadExceptionList(String rootDir) {
        return loadExceptionListMap.get(rootDir);
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

    public Path getRootDirPath() {
        return rootDirPath;
    }

    public void initialize() throws Exception {
        if (this.rootDirList != null) {
            this.tabMap = new HashMap<String, Tab>();
            this.orderList = new ArrayList<String>();
            initializeMulti();
        } else {
            initializeSingle();
        }
    }

    private void initializeMulti() throws Exception {
        StringBuilder systemBuilder = new StringBuilder(":");
        for (String rootDir : this.rootDirList) {
            Path rootPath = Paths.get(rootDir);
            Set<FileVisitOption> options = EnumSet.allOf(FileVisitOption.class);

            // まず先にプラグインJarをロード
            PluginFileVisitor pluginVisitor = new PluginFileVisitor(rootPath.getNameCount() - 1);
            try {
                Files.walkFileTree(rootPath, options, MAX_DEPTH, pluginVisitor);
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
            addPluginList(rootDir, pluginVisitor.getNodePluginList());
            addLoadExceptionList(rootDir, pluginVisitor.getLoadExceptionList());

            // 次にサーバツリー定義をロード
            MyFileVisitor myVisitor = new MyFileVisitor(rootPath.getNameCount() - 1);
            try {
                Files.walkFileTree(rootPath, options, MAX_DEPTH, myVisitor);
            } catch (YAMLException ye) {
                throw new FormatException(ye.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
            if (myVisitor.getFmtNgMsgBuilder().length() > 0) {
                throw new FormatException(myVisitor.getFmtNgMsgBuilder().toString());
            }
            systemBuilder.append(myVisitor.getSystem() + ":");
            addTabMap(rootDir, myVisitor.getTabMap());
            addOrderList(rootDir, myVisitor.getOrderList());
        }
        setSystem(systemBuilder.toString());
    }

    private void initializeSingle() throws Exception {
        Set<FileVisitOption> options = EnumSet.allOf(FileVisitOption.class);

        // まず先にプラグインJarをロード
        PluginFileVisitor pluginVisitor = new PluginFileVisitor(this.rootDirPath.getNameCount() - 1);
        try {
            Files.walkFileTree(this.rootDirPath, options, MAX_DEPTH, pluginVisitor);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        setPluginList(pluginVisitor.getNodePluginList());
        setLoadExceptionList(pluginVisitor.getLoadExceptionList());

        // 次にサーバツリー定義をロード
        MyFileVisitor myVisitor = new MyFileVisitor(this.rootDirPath.getNameCount() - 1);
        try {
            Files.walkFileTree(this.rootDirPath, options, MAX_DEPTH, myVisitor);
        } catch (YAMLException ye) {
            throw new FormatException(ye.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        if (myVisitor.getFmtNgMsgBuilder().length() > 0) {
            throw new FormatException(myVisitor.getFmtNgMsgBuilder().toString());
        }
        setSystem(myVisitor.getSystem());
        setTabMap(myVisitor.getTabMap());
        setOrderList(myVisitor.getOrderList());
    }
}
