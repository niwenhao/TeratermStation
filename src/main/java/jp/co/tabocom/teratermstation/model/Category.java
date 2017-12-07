package jp.co.tabocom.teratermstation.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * カテゴリの情報を保持するクラスです。<br>
 * タブの中の各枠に相当するクラスです。
 * 
 * @author turbou
 * 
 */
public class Category implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private TargetNode targetNode;
    private String iniFile;
    private String procedure;
    private Map<String, String> variable;
    private Map<Integer, Login> loginMap;
    private Map<String, Object> inirewrite;
    private Tab tab;

    public Category() {
        this.targetNode = new TargetNode();
        this.loginMap = new HashMap<Integer, Login>();
    }

    public TargetNode addChild(TargetNode child) {
        targetNode.addChild(child);
        return targetNode;
    }

    public TargetNode getChild(String name) {
        for (TargetNode child : targetNode.getChildren()) {
            if (child.getName().equals(name)) {
                return child;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TargetNode getTargetNode() {
        return targetNode;
    }

    public void setTargetNode(TargetNode targetNode) {
        this.targetNode = targetNode;
    }

    public String getIniFile() {
        if (this.iniFile != null && !this.iniFile.isEmpty()) {
            return this.iniFile;
        }
        return this.tab.getIniFile();
    }

    public void setIniFile(String iniFile) {
        this.iniFile = iniFile;
    }

    public String getProcedure() {
        return procedure;
    }

    public void setProcedure(String procedure) {
        this.procedure = procedure;
    }

    public Map<String, String> getVariable() {
        return variable;
    }

    public String getVariableValue(String key) {
        if (this.variable != null) {
            return this.variable.get(key);
        }
        return null;
    }

    public void setVariable(Map<String, String> variable) {
        this.variable = variable;
    }

    public Map<String, Object> getInirewrite() {
        if (this.inirewrite != null && !this.inirewrite.isEmpty()) {
            return this.inirewrite;
        }
        return this.tab.getInirewrite();
    }

    @SuppressWarnings("unchecked")
    public void setLoginMap(List<Map<String, Object>> login) {
        if (login == null) {
            return;
        }
        for (Map<String, Object> map : login) {
            Login l = new Login();
            l.setIndex(map.get("index"));
            l.setUser(map.get("user"));
            l.setPassword(map.get("password"));
            l.setIniFile(map.get("inifile"));
            l.setProcedure(map.get("procedure"));
            l.setVariable((Map<String, String>) map.get("variable"));
            this.loginMap.put(l.getIndex(), l);
        }
    }

    public void setInirewrite(Map<String, Object> inirewrite) {
        this.inirewrite = inirewrite;
    }

    public void setTab(Tab tab) {
        this.tab = tab;
    }

    public Tab getTab() {
        return tab;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public void sortTargetNode(List<String> groupOrderList, List<String> serverOrderList) {
        TargetNode sortedNode = new TargetNode();

        if (groupOrderList == null) {
            // グループオーダーリストがnullの場合はサイズ０のリストとしておく。
            groupOrderList = new ArrayList<String>();
        }

        List<String> keys = new ArrayList<String>();
        for (TargetNode child : this.targetNode.getChildren()) {
            keys.add(child.getName());
        }
        Map<String, String> sortMap = new HashMap<String, String>();
        for (String key : keys) {
            int idx = groupOrderList.indexOf(key);
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
            TargetNode child = this.getChild(key);
            child.sortChildren(serverOrderList);
            sortedNode.addChild(child);
        }
        this.targetNode = sortedNode;
    }

    public Map<Integer, Login> getLoginMap() {
        if (this.loginMap != null && !this.loginMap.isEmpty()) {
            return this.loginMap;
        }
        return this.tab.getLoginMap();
    }
}
