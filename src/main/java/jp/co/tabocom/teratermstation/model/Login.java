/*
 * MIT License
 *　Copyright (c) 2015-2019 Tabocom
 *
 *　Permission is hereby granted, free of charge, to any person obtaining a copy
 *　of this software and associated documentation files (the "Software"), to deal
 *　in the Software without restriction, including without limitation the rights
 *　to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *　copies of the Software, and to permit persons to whom the Software is
 *　furnished to do so, subject to the following conditions:
 *
 *　The above copyright notice and this permission notice shall be included in all
 *　copies or substantial portions of the Software.
 *
 *　THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *　IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *　FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *　AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *　LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *　OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *　SOFTWARE.
 */

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
