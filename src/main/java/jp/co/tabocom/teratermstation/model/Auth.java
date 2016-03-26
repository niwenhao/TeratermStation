package jp.co.tabocom.teratermstation.model;

import java.io.Serializable;

/**
 * 
 * 認証などに関する情報を保持するクラスです。<br>
 * TeratermStation上部の認証エリアの機能をサポートします。
 *
 * @author turbou
 *
 */
public class Auth implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 認証情報の記憶を可能とするか否か
     */
    private boolean memory;
    /**
     * PCがロックされた場合に認証パスワードを自動でクリアするか否か
     */
    private boolean autoclear;
    /**
     * 他のタブと認証情報を共有する場合の共有識別子
     */
    private String group;
    /**
     * 認証チェックのためのttlのやり取り
     */
    private String check;

    public boolean isMemory() {
        return memory;
    }

    public void setMemory(boolean memory) {
        this.memory = memory;
    }

    public boolean isAutoclear() {
        return autoclear;
    }

    public void setAutoclear(boolean autoclear) {
        this.autoclear = autoclear;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getCheck() {
        return check;
    }

    public void setCheck(String check) {
        this.check = check;
    }

}
