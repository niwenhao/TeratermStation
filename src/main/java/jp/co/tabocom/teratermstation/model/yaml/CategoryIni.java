package jp.co.tabocom.teratermstation.model.yaml;

import java.util.Map;

/**
 * 
 * category.yamlを読み込むためのクラスです。<br>
 * <br>
 * category.yamlの内容は下な感じ<br>
 * 
 * <pre>
 * --------------------------------------------------------------------------------
 * loginuser: kibanusr
 * loginpassword: kibanpwd
 * inifile: AWS.INI
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
     * カテゴリ全体に適用されるサーバログインユーザー（任意）
     */
    private String loginuser;
    /**
     * カテゴリ全体に適用されるサーバログインパスワード（任意）
     */
    private String loginpassword;
    /**
     * ログイン後の手続き（任意）
     */
    private String procedure;
    /**
     * 変数マップ
     */
    private Map<String, String> variable;

    public String getInifile() {
        return inifile;
    }

    public void setInifile(String inifile) {
        this.inifile = inifile;
    }

    public String getLoginuser() {
        return loginuser;
    }

    public void setLoginuser(String loginuser) {
        this.loginuser = loginuser;
    }

    public String getLoginpassword() {
        return loginpassword;
    }

    public void setLoginpassword(String loginpassword) {
        this.loginpassword = loginpassword;
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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("inifile       : %s\n", this.inifile));
        builder.append(String.format("loginuser     : %s\n", this.loginuser));
        builder.append(String.format("loginpassword : %s\n", this.loginpassword));
        builder.append(String.format("procedure     : %s\n", this.procedure));
        builder.append(String.format("variable      : %s\n", this.variable));
        return builder.toString();
    }
}
