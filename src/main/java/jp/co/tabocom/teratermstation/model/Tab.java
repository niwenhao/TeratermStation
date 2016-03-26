package jp.co.tabocom.teratermstation.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * タブの情報を保持するクラスです。<br>
 * TeratermStationの画面の中のまさにタブに相当するクラスです。
 * 
 * @author turbou
 *
 */
public class Tab implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String dirPath;
    private String iconPath;
    private Auth auth;
    private String connect;
    private String loginUsr;
    private String loginPwd;
    private String iniFile;
    private List<Category> categoryList;

    public Tab() {
        this.categoryList = new ArrayList<Category>();
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

    public Auth getAuth() {
        return auth;
    }

    public void setAuth(Auth auth) {
        this.auth = auth;
    }

    public String getConnect() {
        return connect;
    }

    public void setConnect(String connect) {
        this.connect = connect;
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

    public List<Category> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<Category> categoryList) {
        this.categoryList = categoryList;
    }

    @Override
    public String toString() {
        return this.name;
    }

}
