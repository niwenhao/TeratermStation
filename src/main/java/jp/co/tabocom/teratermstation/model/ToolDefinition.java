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

import org.yaml.snakeyaml.error.YAMLException;

import jp.co.tabocom.teratermstation.exception.FormatException;
import jp.co.tabocom.teratermstation.plugin.TeratermStationPlugin;

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

    public ToolDefinition(Path path) {
        this.rootDirPath = path;
    }
    
    public ToolDefinition(List<String> rootDirList) {
        this.rootDirList = rootDirList;
        this.tabMapMap = new HashMap<String, Map<String, Tab>>();
        this.orderListMap = new HashMap<String, List<String>>();
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

    public List<String> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<String> orderList) {
        this.orderList = orderList;
    }

    public void addOrderList(String rootDir, List<String> orderList) {
        this.orderListMap.put(rootDir, orderList);
    }

    public List<TeratermStationPlugin> getPluginList() {
        return pluginList;
    }

    public void setPluginList(List<TeratermStationPlugin> pluginList) {
        this.pluginList = pluginList;
    }

    public List<Exception> getLoadExceptionList() {
        return loadExceptionList;
    }

    public void setLoadExceptionList(List<Exception> loadExceptionList) {
        this.loadExceptionList = loadExceptionList;
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
        StringBuilder systemBuilder = new StringBuilder("-");
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
            setPluginList(pluginVisitor.getNodePluginList());
            setLoadExceptionList(pluginVisitor.getLoadExceptionList());

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
            systemBuilder.append(myVisitor.getSystem() + "-");
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
