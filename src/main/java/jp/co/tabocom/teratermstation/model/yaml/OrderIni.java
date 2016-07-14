package jp.co.tabocom.teratermstation.model.yaml;

import java.util.List;

/**
 * 
 * order.yamlを読み込むためのクラスです。<br>
 * <br>
 * order.yamlの内容は下な感じ<br>
 * 
 * <pre>
 * --------------------------------------------------------------------------------
 * tab:
 *   - 開発環境
 *   - UAT環境
 *   - 本番環境
 * # [開発環境, UAT環境, 本番環境] みたいにも書けます。
 * category:
 *   - 業務チーム
 *   - 基盤チーム
 * group:
 *   - D面
 *   - C面
 *   - B面
 *   - A面
 * server:
 *   - Webサーバ
 *   - DBサーバ
 * --------------------------------------------------------------------------------
 * </pre>
 * 
 * @author turbou
 * 
 */
public class OrderIni {
    /**
     * タブのオーダーリスト
     */
    private List<String> tab;
    /**
     * カテゴリのオーダーリスト
     */
    private List<String> category;
    /**
     * グループのオーダーリスト
     */
    private List<String> group;
    /**
     * サーバのオーダーリスト
     */
    private List<String> server;

    public List<String> getTab() {
        return tab;
    }

    public void setTab(List<String> tab) {
        this.tab = tab;
    }

    public List<String> getCategory() {
        return category;
    }

    public void setCategory(List<String> category) {
        this.category = category;
    }

    public List<String> getGroup() {
        return group;
    }

    public void setGroup(List<String> group) {
        this.group = group;
    }

    public List<String> getServer() {
        return server;
    }

    public void setServer(List<String> server) {
        this.server = server;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("tab      : %s\n", this.tab));
        builder.append(String.format("category : %s\n", this.category));
        builder.append(String.format("group    : %s\n", this.group));
        builder.append(String.format("server   : %s\n", this.server));
        return builder.toString();
    }

}
