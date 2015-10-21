package jp.co.tabocom.teratermstation.model;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Category implements Serializable {
    private static final long serialVersionUID = -4090217249921939133L;

    private String name;
    private TargetNode targetNode;
    private String loginUsr;
    private String loginPwd;
    private String iniFile;
    private UseMacroType useMacroType;
    private List<File> macroList;
    private Tab tab;

    public Category() {
        this.targetNode = new TargetNode();
        this.macroList = new ArrayList<File>();
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

    public UseMacroType getUseMacroType() {
        if (useMacroType != UseMacroType.FOLLOW) {
            return useMacroType;
        }
        return this.tab.getUseMacroType();
    }

    public void setUseMacroType(String useMacroType) {
        this.useMacroType = UseMacroType.getType(useMacroType);
    }

    public void addMacro(File file) {
        this.macroList.add(file);
    }

    public List<File> getMacroList() {
        List<File> list = new ArrayList<File>();
        list.addAll(this.tab.getMacroList());
        list.addAll(this.macroList);
        return list;
    }

    public void setTab(Tab tab) {
        this.tab = tab;
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
                sortMap.put(String.valueOf(idx), key);
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
