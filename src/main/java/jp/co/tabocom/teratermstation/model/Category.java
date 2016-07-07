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
    private String loginUsr;
    private String loginPwd;
    private String iniFile;
    private String procedure;
    private Map<String, String> variable;
    private Tab tab;

    public Category() {
        this.targetNode = new TargetNode();
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

    public String getLoginUsr() {
        if (this.loginUsr != null) {
            return this.loginUsr;
        }
        return this.tab.getLoginUsr();
    }

    public void setLoginUsr(String loginUsr) {
        this.loginUsr = loginUsr;
    }

    public String getLoginPwd() {
        if (this.loginPwd != null) {
            return this.loginPwd;
        }
        return this.tab.getLoginPwd();
    }

    public void setLoginPwd(String loginPwd) {
        this.loginPwd = loginPwd;
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

    public void sortTargetNode(List<String> orderList) {
        TargetNode sortedNode = new TargetNode();

        List<String> keys = new ArrayList<String>();
        for (TargetNode child : this.targetNode.getChildren()) {
            keys.add(child.getName());
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
            TargetNode child = this.getChild(key);
            child.sortChildren(orderList);
            sortedNode.addChild(child);
        }
        this.targetNode = sortedNode;
    }
}
