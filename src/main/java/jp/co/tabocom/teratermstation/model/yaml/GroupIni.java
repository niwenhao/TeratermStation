package jp.co.tabocom.teratermstation.model.yaml;

import java.util.List;
import java.util.Map;

/**
 * 
 * group.yamlを読み込むためのクラスです。<br>
 * <br>
 * group.yamlの内容は下な感じ<br>
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
public class GroupIni {
    /**
     * サーバグループID（任意）
     */
    private String id;
    /**
     * サーバグループ全体に適用されるTera TermのINIファイル（任意）
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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
