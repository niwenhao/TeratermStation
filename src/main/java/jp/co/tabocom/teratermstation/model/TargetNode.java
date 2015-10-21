package jp.co.tabocom.teratermstation.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TargetNode implements Comparable<TargetNode>, PropertyChangeListener, Serializable {
    private static final long serialVersionUID = 6795171432373000432L;

    private String name;

    private List<TargetNode> i_children = new ArrayList<TargetNode>();

    private TargetNode i_parent;

    private boolean i_isEnabled;

    private boolean i_isGrayed;

    private Category category;

    // ========== 実データここから ========== //
    private File file;
    private String ipAddr;
    private String hostName;
    private String iniFile;
    private String loginUsr;
    private String loginPwd;
    private String keyValue;
    private UseMacroType useMacroType;
    private List<File> macroList;
    private List<String> orderList;

    // ========== 実データここまで ========== //

    public void propertyChange(final PropertyChangeEvent event) {
    }

    public TargetNode() {
        this.macroList = new ArrayList<File>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TargetNode getParent() {
        return i_parent;
    }

    public TargetNode addChild(TargetNode child) {
        i_children.add(child);
        child.i_parent = this;
        return this;
    }

    public TargetNode getChild(String name) {
        for (TargetNode child : this.getChildren()) {
            if (child.getName().equals(name)) {
                return child;
            }
        }
        return null;
    }

    public TargetNode addChildren(List<TargetNode> children) {
        if (children != null) {
            for (TargetNode child : children) {
                i_children.add(child);
                Collections.sort(i_children);
                child.i_parent = this;
            }
        }
        return this;
    }

    public List<TargetNode> getChildren() {
        return i_children;
    }

    public boolean isEnabled() {
        return i_isEnabled;
    }

    public void setIsEnabled(boolean enabled) {
        i_isEnabled = enabled;
    }

    public boolean isGrayed() {
        return i_isGrayed;
    }

    public void setIsGrayed(boolean grayed) {
        i_isGrayed = grayed;
    }

    public String toString() {
        return name;
    }

    public int compareTo(TargetNode o) {
        return name.compareTo(o.toString());
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public String getIniFile() {
        if (this.iniFile != null && !this.iniFile.isEmpty()) {
            return this.iniFile;
        }
        if (this.category != null) {
            return this.category.getIniFile();
        }
        return getParent().getIniFile();
    }

    public void setIniFile(String iniType) {
        this.iniFile = iniType;
    }

    public String getLoginUsr() {
        if (this.loginUsr != null) {
            return this.loginUsr;
        }
        if (this.category != null) {
            return this.category.getLoginUsr();
        }
        return getParent().getLoginUsr();
    }

    public void setLoginUsr(String loginUsr) {
        this.loginUsr = loginUsr;
    }

    public String getLoginPwd() {
        if (this.loginPwd != null) {
            return this.loginPwd;
        }
        if (this.category != null) {
            return this.category.getLoginPwd();
        }
        return getParent().getLoginPwd();
    }

    public void setLoginPwd(String loginPwd) {
        this.loginPwd = loginPwd;
    }

    public boolean isParent() {
        return this.ipAddr == null;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public UseMacroType getUseMacroType() {
        if (useMacroType != UseMacroType.FOLLOW) {
            return useMacroType;
        }
        if (this.category != null) {
            return this.category.getUseMacroType();
        }
        return getParent().getUseMacroType();
    }

    public void setUseMacroType(String useMacroType) {
        this.useMacroType = UseMacroType.getType(useMacroType);
    }

    public void addMacro(File file) {
        this.macroList.add(file);
    }

    public List<File> getMacroList() {
        List<File> list = new ArrayList<File>();
        if (this.category != null) {
            list.addAll(this.category.getMacroList());
        }
        if (getParent() != null) {
            list.addAll(getParent().getMacroList());
        }
        list.addAll(this.macroList);
        return list;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }
    
    public void sortChildren(List<String> orderList) {
    	List<String> keys = new ArrayList<String>();
    	for (TargetNode child : this.getChildren()) {
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
        List<TargetNode> sortedList = new ArrayList<TargetNode>();
        for (String idx : idxList) {
            String key = sortMap.get(idx);
            TargetNode child = this.getChild(key);
            sortedList.add(child);
        }
        this.i_children.clear();
        for (TargetNode child : sortedList) {
        	this.addChild(child);
        }
    }
}
