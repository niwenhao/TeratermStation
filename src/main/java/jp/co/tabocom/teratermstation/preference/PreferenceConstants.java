package jp.co.tabocom.teratermstation.preference;

public class PreferenceConstants {

    public static final String TARGET_DIR = new String("jp.co.tabocom.conntool.targetDir");

    // TeraTermマクロexeのフルパス
    public static final String TTPMACRO_EXE = new String("jp.co.tabocom.conntool.ttpmacroExe");
    // ========== 実行に必要なディレクトリ関連 ========== //
    // 作業用ディレクトリ（ttlファイルの置き場所）
    public static final String WORK_DIR = new String("jp.co.tabocom.conntool.workDir");
    // ログディレクトリ（logファイルが置かれます）
    public static final String LOG_DIR = new String("jp.co.tabocom.conntool.logDir");
    // TeraTermのINIファイルの置き場所（基本この中のINIファイルを使用します）
    public static final String INIFILE_DIR = new String("jp.co.tabocom.conntool.inifileDir");

    // ========== その他 ========== //
    // 開いてるタブの位置の保存用（次にツールを起動した時に最後に開いてたタブが選択されるようにするため）
    public static final String OPENED_TAB_IDX = new String("jp.co.tabocom.conntool.openedTabIdx");
    // 開発環境で使用するときに毎回IDとパスワードを入れるのが面倒なので、それで使用するためのもの（読み込み専用）
    public static final String AUTH_USER_PWD = new String("jp.co.tabocom.conntool.authinfo_");
}
