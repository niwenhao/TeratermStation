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

/**
 * グループ（サーバグループ）またはサーバの情報を保持するクラスです。<br>
 * グループの場合はツリーの親、サーバの場合はツリーの子に相当します。
 * 
 * @author turbou
 * 
 */
public class TargetNode implements Comparable<TargetNode>, PropertyChangeListener, Serializable {
    private static final long serialVersionUID = 1L;

    private String name;

    private List<TargetNode> i_children = new ArrayList<TargetNode>();

    private TargetNode i_parent;

    private boolean i_isEnabled;

    private boolean i_isGrayed;

    private Category category;

    // ========== 実データここから ========== //
    private File file;
    private String id;
    private String ipAddr;
    private String hostName;
    private String iniFile;
    private String procedure;
    private Map<String, String> variable;
    private Map<Integer, Login> loginMap;
    // ========== 実データここまで ========== //
    private int loginUserIdx = 1;

    public void propertyChange(final PropertyChangeEvent event) {
        if ("userswitch".equals(event.getPropertyName())) {
            int idx = ((Integer) event.getNewValue()).intValue();
            if (idx > -1) {
                loginUserIdx = idx;
            }
        }
    }

    public TargetNode() {
        this.loginMap = new HashMap<Integer, Login>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TargetNode getParent() {
        if (i_parent.getName() == null || i_parent.getName().isEmpty()) {
            return null;
        }
        return i_parent;
    }

    public String getParentName() {
        if (getParent() != null) {
            return getParent().getName();
        }
        return getCategory().getName();
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

    public String getId() {
        if (this.id == null || this.id.isEmpty()) {
            return this.hostName;
        }
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    private Map<Integer, Login> getLoginMap() {
        if (this.loginMap != null && !this.loginMap.isEmpty()) {
            return this.loginMap;
        }
        if (this.category != null) {
            return this.category.getLoginMap();
        }
        return getParent().getLoginMap();
    }

    private Login getLogin() {
        Map<Integer, Login> map = getLoginMap();
        Login login = null;
        if (map.containsKey(loginUserIdx)) {
            login = map.get(loginUserIdx);
        }
        return login;
    }

    public String getLoginUsr() {
        Login login = getLogin();
        if (login != null) {
            return login.getUser();
        }
        return "";
    }

    public String getLoginPwd() {
        Login login = getLogin();
        if (login != null) {
            return login.getPassword();
        }
        return "";
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

    public Map<String, Object> getInirewrite() {
        if (this.category != null) {
            return this.category.getInirewrite();
        }
        return null;
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

    public void setCategory(Category category) {
        this.category = category;
    }

    public Category getCategory() {
        return category;
    }

    public String getProcedure() {
        Login login = getLogin();
        if (login != null) {
            if (login.getProcedure() != null) {
                return login.getProcedure();
            }
        }
        if (this.procedure != null && !this.procedure.isEmpty()) {
            return this.procedure;
        }
        if (this.category != null) {
            return this.category.getProcedure();
        }
        return getParent().getProcedure();
    }

    public void setProcedure(String procedure) {
        this.procedure = procedure;
    }

    public Map<String, String> getVariable() {
        Login login = getLogin();
        if (login != null) {
            if (login.getVariable() != null) {
                return login.getVariable();
            }
        }
        if (this.variable != null) {
            return this.variable;
        }
        if (this.category != null) {
            return this.category.getVariable();
        }
        return getParent().getVariable();
    }

    public void setVariable(Map<String, String> variable) {
        this.variable = variable;
    }

    public void sortChildren(List<String> orderList) {
        if (orderList == null || orderList.isEmpty()) {
            return;
        }
        List<String> keys = new ArrayList<String>();
        for (TargetNode child : this.getChildren()) {
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
