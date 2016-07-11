package jp.co.tabocom.teratermstation.model;

import java.io.Serializable;
import java.util.Map;

public class Login implements Serializable {
    private static final long serialVersionUID = 1L;

    private int index;
    private String user;
    private String password;
    private String iniFile;
    private String procedure;
    private Map<String, String> variable;

    public int getIndex() {
        return index;
    }

    public void setIndex(Object index) {
        this.index = new Integer(index.toString());
    }

    public String getUser() {
        if (user == null) {
            return "";
        }
        return user;
    }

    public void setUser(Object user) {
        if (user != null) {
            this.user = user.toString();
        }
    }

    public String getPassword() {
        if (password == null) {
            return "";
        }
        return password;
    }

    public void setPassword(Object password) {
        if (password != null) {
            this.password = password.toString();
        }
    }

    public String getIniFile() {
        return iniFile;
    }

    public void setIniFile(Object iniFile) {
        if (iniFile != null) {
            this.iniFile = iniFile.toString();
        }
    }

    public String getProcedure() {
        return procedure;
    }

    public void setProcedure(Object procedure) {
        if (procedure != null) {
            this.procedure = procedure.toString();
        }
    }

    public Map<String, String> getVariable() {
        return variable;
    }

    public void setVariable(Map<String, String> variable) {
        this.variable = variable;
    }

}
