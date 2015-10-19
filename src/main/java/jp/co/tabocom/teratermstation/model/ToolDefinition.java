package jp.co.tabocom.teratermstation.model;

import java.io.File;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.co.tabocom.teratermstation.plugin.TeraTermStationPlugin;

public class ToolDefinition {

    private static final int MAX_DEPTH = 4;

    private Path rootDirPath;
    private String system;
    private int width;
    private int height;

    private Map<String, Tab> tabMap;

    private List<TeraTermStationPlugin> nodePluginList;

    private Initial initial;

    public ToolDefinition() {
        this.tabMap = new LinkedHashMap<String, Tab>();
    }

    public ToolDefinition(Path path) {
        this.rootDirPath = path;
    }

    public void addTab(Tab tab) {
        this.tabMap.put(tab.getName(), tab);
    }

    public Map<String, Tab> getTabMap() {
        return tabMap;
    }

    public void setTabMap(Map<String, Tab> tabMap) {
        this.tabMap = tabMap;
    }

    public List<TeraTermStationPlugin> getNodePluginList() {
        return nodePluginList;
    }

    public void setNodePluginList(List<TeraTermStationPlugin> nodePluginList) {
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

    public void initialize() throws Exception {
        Set<FileVisitOption> options = EnumSet.allOf(FileVisitOption.class);
        // TODO: walkFileTreeの前にプラグインを読み込む処理を入れる。
        PluginFileVisitor pluginVisitor = new PluginFileVisitor(this.rootDirPath.getNameCount() - 1);
        try {
            Files.walkFileTree(this.rootDirPath, options, MAX_DEPTH, pluginVisitor);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        setNodePluginList(pluginVisitor.getNodePluginList());

        // TODO: walkFileTreeの中ではpluginsと、plugins下のファイルは無視するようにする。
        MyFileVisitor myVisitor = new MyFileVisitor(this.rootDirPath.getNameCount() - 1);
        try {
            Files.walkFileTree(this.rootDirPath, options, MAX_DEPTH, myVisitor);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        for (Tab tab : myVisitor.getTabMap().values()) {
            // macro
            List<File> list = new ArrayList<File>();
            list.addAll(myVisitor.getMacroList());
            list.addAll(tab.getMacroList());
            tab.setMacroList(list);
            // inifile
            if (tab.getIniFile() == null) {
                tab.setIniFile(myVisitor.getIniFile());
            }
        }
        setSystem(myVisitor.getSystem());
        setWidth(myVisitor.getWidth());
        setHeight(myVisitor.getHeight());
        setInitial(myVisitor.getInitial());
        setTabMap(myVisitor.getTabMap());
    }
}
