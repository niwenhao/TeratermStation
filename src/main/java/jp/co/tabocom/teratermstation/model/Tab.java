package jp.co.tabocom.teratermstation.model;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Tab implements Serializable {
    private static final long serialVersionUID = -2959784033887335152L;

    private String name;
    private String dirPath;
    private String iconPath;
    private Gateway gateway;
    private String connect;
    private String negotiation;
    private String loginUsr;
    private String loginPwd;
    private String iniFile;
    private UseMacroType useMacroType;
    private List<Category> categoryList;
    private List<File> macroList;

    public Tab() {
        this.categoryList = new ArrayList<Category>();
        this.macroList = new ArrayList<File>();
    }

    public void addCategory(Category category) {
        this.categoryList.add(category);
    }

    public Category getCategory(String name) {
        for (Category category : this.categoryList) {
            if (category.getName().equals(name)) {
                return category;
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

    public String getDirPath() {
        return dirPath;
    }

    public void setDirPath(String dirPath) {
        this.dirPath = dirPath;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public Gateway getGateway() {
        return gateway;
    }

    public void setGateway(Gateway gateway) {
        this.gateway = gateway;
    }

    public String getConnect() {
        return connect;
    }

    public void setConnect(String connect) {
        this.connect = connect;
    }

    public String getNegotiation() {
        return negotiation;
    }

    public void setNegotiation(String negotiation) {
        this.negotiation = negotiation;
    }

    public String getLoginUsr() {
        return loginUsr;
    }

    public void setLoginUsr(String loginUsr) {
        this.loginUsr = loginUsr;
    }

    public String getLoginPwd() {
        return loginPwd;
    }

    public void setLoginPwd(String loginPwd) {
        this.loginPwd = loginPwd;
    }

    public String getIniFile() {
        if (this.iniFile != null && !this.iniFile.isEmpty()) {
            return this.iniFile;
        }
        return "";
    }

    public void setIniFile(String iniFile) {
        this.iniFile = iniFile;
    }

    public UseMacroType getUseMacroType() {
        if (useMacroType != UseMacroType.UNUSED) {
            return UseMacroType.USE;
        }
        return UseMacroType.UNUSED;
    }

    public void setUseMacroType(String useMacroType) {
        this.useMacroType = UseMacroType.getType(useMacroType);
    }

    public List<Category> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<Category> categoryList) {
        this.categoryList = categoryList;
    }

    public void addMacro(File file) {
        this.macroList.add(file);
    }

    public void setMacroList(List<File> macroList) {
        this.macroList = macroList;
    }

    public List<File> getMacroList() {
        List<File> list = new ArrayList<File>();
        list.addAll(this.macroList);
        return list;
    }

    @Override
    public String toString() {
        return this.name;
    }

}
