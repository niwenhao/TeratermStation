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
import java.util.List;
import java.util.Map;

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
     * 認証グループのタイトル
     */
    private String title;
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

    /**
     * 認証チェックのためのttlのやり取り
     */
    private List<Map<String, Object>> optionInputs;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

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

    public List<Map<String, Object>> getOptionInputs() {
        return optionInputs;
    }

    public void setOptionInputs(List<Map<String, Object>> optionInputs) {
        this.optionInputs = optionInputs;
    }

}
