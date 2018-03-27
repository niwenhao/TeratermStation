package jp.co.tabocom.teratermstation.preference;

public class PreferenceConstants {

    // サーバツリー基点ディレクトリリスト
    public static final String TARGET_DIRS = new String("jp.co.tabocom.teratermstation.targetDirs");

    // TeraTermマクロexeのフルパス
    public static final String TTPMACRO_EXE = new String("jp.co.tabocom.teratermstation.ttpmacroExe");
    // ========== 実行に必要なディレクトリ関連 ========== //
    // 作業用ディレクトリ（ttlファイルの置き場所）
    public static final String WORK_DIR = new String("jp.co.tabocom.teratermstation.workDir");
    // ログディレクトリ（logファイルが置かれます）
    public static final String LOG_DIR = new String("jp.co.tabocom.teratermstation.logDir");
    // TeraTermのINIファイルの置き場所（基本この中のINIファイルを使用します）
    public static final String INIFILE_DIR = new String("jp.co.tabocom.teratermstation.inifileDir");

    // ========== TTLマクロ生成に関する設定 ========== //
    // TTLファイルの出力文字コード
    public static final String TTL_CHARCODE = new String("jp.co.tabocom.teratermstation.ttlCharCode");
    // 認証パスワード伏字文字列
    public static final String TTL_AUTH_PWD_HIDE = new String("jp.co.tabocom.teratermstation.ttlAuthPwdHide");
    // Teratermのlogopenのオプションについて
    public static final String LOGOPEN_OPTION = new String("jp.co.tabocom.teratermstation.logopenOption");

    // ========== その他 ========== //
    // 開いてるタブの位置の保存用（次にツールを起動した時に最後に開いてたタブが選択されるようにするため）
    public static final String OPENED_TAB_IDX = new String("jp.co.tabocom.teratermstation.openedTabIdx");
    // 開発環境で使用するときに毎回IDとパスワードを入れるのが面倒なので、それで使用するためのもの（読み込み専用）
    public static final String AUTH_USER_PWD = new String("jp.co.tabocom.teratermstation.authinfo_");
    // 認証情報オプション接頭辞
    public static final String AUTH_OPTION = new String("jp.co.tabocom.teratermstation.authoption_");
    // ウインドウサイズの幅
    public static final String MEM_WIDTH = new String("jp.co.tabocom.teratermstation.memWidth");
    // ウインドウサイズの縦
    public static final String MEM_HEIGHT = new String("jp.co.tabocom.teratermstation.memHeight");
}
