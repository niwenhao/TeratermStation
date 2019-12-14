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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private Map<Integer, Login> loginMap;
    private String iniFile;
    private List<Category> categoryList;
    private Map<String, Object> inirewrite;

    public Tab() {
        this.categoryList = new ArrayList<Category>();
        this.loginMap = new HashMap<Integer, Login>();
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
    
    public Map<String, Object> getInirewrite() {
		return inirewrite;
	}

	public void setInirewrite(Map<String, Object> inirewrite) {
		this.inirewrite = inirewrite;
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

    @Override
    public String toString() {
        return this.name;
    }

    public Map<Integer, Login> getLoginMap() {
        return loginMap;
    }

}
