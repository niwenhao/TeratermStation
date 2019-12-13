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

package jp.co.tabocom.teratermstation.model.yaml;

import java.util.List;
import java.util.Map;

/**
 * 
 * category.yamlを読み込むためのクラスです。<br>
 * <br>
 * category.yamlの内容は下な感じ<br>
 * 
 * <pre>
 * --------------------------------------------------------------------------------
 * login:
 *   - index: 1
 *     user: aplusr
 *     password: aplpwd
 *     inifile: DEV.INI
 *     procedure: |
 *       wait ']$'
 *       sendln 'date'
 *     variable:
 *       APL_DIR: /APL/group01/local
 *       APL_PKG01_DIR: /APL/group01/pkg01
 *   - index: 2
 *     user: libusr
 *     password: libpwd
 * procedure: |
 *   wait ']$'
 *   sendln 'date'
 * variable:
 *   APL_DIR: /APL/group01/local
 *   APL_PKG01_DIR: /APL/group01/pkg01
 * --------------------------------------------------------------------------------
 * </pre>
 * 
 * @author turbou
 * 
 */
public class CategoryIni {
    /**
     * タブ全体に適用されるTera TermのINIファイル（任意）
     */
    private String inifile;
    /**
     * ログイン後の手続き（任意）
     */
    private String procedure;
    /**
     * 変数マップ
     */
    private Map<String, String> variable;
    /**
     * ログインリスト
     */
    private List<Map<String, Object>> login;

    /**
     * INIファイルの中味を書き換える情報（任意）
     */
    private Map<String, Object> inirewrite;

    public String getInifile() {
        return inifile;
    }

    public void setInifile(String inifile) {
        this.inifile = inifile;
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

    public void setVariable(Map<String, String> variable) {
        this.variable = variable;
    }

    public List<Map<String, Object>> getLogin() {
        return login;
    }

    public void setLogin(List<Map<String, Object>> login) {
        this.login = login;
    }

    public Map<String, Object> getInirewrite() {
        return inirewrite;
    }

    public void setInirewrite(Map<String, Object> inirewrite) {
        this.inirewrite = inirewrite;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("inifile       : %s\n", this.inifile));
        builder.append(String.format("login         : %s\n", this.login));
        builder.append(String.format("procedure     : %s\n", this.procedure));
        builder.append(String.format("variable      : %s\n", this.variable));
        return builder.toString();
    }
}
