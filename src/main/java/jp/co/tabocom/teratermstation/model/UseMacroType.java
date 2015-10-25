package jp.co.tabocom.teratermstation.model;

/**
 * マクロの使用に関する設定区分です。
 * 
 * @author turbou
 *
 */
public enum UseMacroType {

    /**
     * 上位の設定に関わらずマクロを使用する場合
     */
    USE("true"),
    /**
     * 上位の設定に関わらずマクロを使用しない場合
     */
    UNUSED("false"),
    /**
     * 上位の設定に従う場合
     */
    FOLLOW("none");

    private String value;

    private UseMacroType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static UseMacroType getType(String value) {
        for (UseMacroType type : UseMacroType.values()) {
            if (value.equals(type.toString())) {
                return type;
            }
        }
        return UseMacroType.FOLLOW;
    }
}
