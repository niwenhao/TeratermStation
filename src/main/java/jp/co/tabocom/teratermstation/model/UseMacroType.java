package jp.co.tabocom.teratermstation.model;

public enum UseMacroType {
    USE("true"), UNUSED("false"), FOLLOW("none");

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
