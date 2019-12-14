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

package jp.co.tabocom.teratermstation.model;

import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    private List<String> rootDirList;
    private String system;

    private Map<String, Map<String, Tab>> tabMapMap;
    private Map<String, Map<String, List<String>>> orderListMap;

    private Map<String, List<TeratermStationPlugin>> pluginListMap;
    private Map<String, List<Exception>> loadExceptionListMap;

    public ToolDefinition(List<String> rootDirList) {
        this.rootDirList = rootDirList;
        this.tabMapMap = new HashMap<String, Map<String, Tab>>();
        this.orderListMap = new HashMap<String, Map<String, List<String>>>();
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

    public void addTabMap(String rootDir, Map<String, Tab> tabMap) {
        this.tabMapMap.put(rootDir, tabMap);
    }

    public Map<String, Tab> getTabMap(String rootDir) {
        return tabMapMap.get(rootDir);
    }

    public void addOrderListMap(String rootDir, Map<String, List<String>> map) {
        this.orderListMap.put(rootDir, map);
    }

    public List<String> getOrderList(String rootDir, String type) {
        return orderListMap.get(rootDir).get(type);
    }

    public void addPluginList(String rootDir, List<TeratermStationPlugin> pluginList) {
        this.pluginListMap.put(rootDir, pluginList);
    }

    public List<TeratermStationPlugin> getPluginList(String rootDir) {
        return pluginListMap.get(rootDir);
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

    public void initialize() throws Exception {
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
            addPluginList(rootDir, pluginVisitor.getPluginList());
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
            addOrderListMap(rootDir, myVisitor.getOrderListMap());
        }
        setSystem(systemBuilder.toString());
    }
}
