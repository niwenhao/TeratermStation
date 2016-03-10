# TeratermStation

# ツールの概要

あらかじめ設定した接続先情報を元に TeratermMacro を経由して自動で接続を行います。  
接続時に、間にプロキシ認証を挟む必要がある場合でも設定を行うことで自動的に接続できます。  
本ツールを経由して Teraterm接続 を行うことで、自動的に操作ログが記録されます。

# 前提条件

Tera Termは4.58以上をサポートします。

# 接続先の定義方法

接続先は、フォルダ構造で定義を行う。  

 * 第一層（タブ）　フォルダ名がタブ名になる
 * 第二層（カテゴリ）　フォルダ名がカテゴリ名になる
 * 第三層（グループ）　フォルダ名がグループ名になる
 * 第四層（サーバ定義）　ファイル名がサーバ名になる
 
 
# 接続先の定義例
 
## フォルダ構成
 
 下記のフォルダ構成を作成する。  
 「DBサーバ.txt」などにサーバの接続情報を記載する。  
 「tab.yaml」、「category.yaml」に、階層ごとに設定を記載する。  
 下位の階層で定義されない場合、上位階層の定義が利用される。  
 
 **txt, ini, yamlのいずれも内容に日本語を含む場合はUTF-8で保存する必要があります。**
 
```
～定義ディレクトリ  
  │  settings.yaml  
  │  マクロ（ツール）.macro  
  │  
  ├─開発環境  
  │  │  icon.png  
  │  │  tab.yaml  
  │  │  マクロ（タブ）.macro  
  │  │  
  │  ├─ｘｘチーム利用環境  
  │  │  │  category.yaml  
  │  │  │  マクロ（カテゴリ）.macro  
  │  │  │  
  │  │  ├─A面  
  │  │  │  DBサーバ.txt  
  │  │  │  Webサーバ.txt  
  │  │  │  group.yaml  
  │  │  │  マクロ（A面専用）.macro  
  │  │  │  
  │  │  └─B面  
  │  │     DBサーバ.txt  
  │  │     APサーバ.txt  
  │  │     group.yaml  
  │  │     マクロ（B面専用）.macro  
  │  │  
  │  └─ｘｘチーム利用環境  
  │      │  category.yaml  
  │      │  マクロ（カテゴリ）.macro  
  │      │  
  │      ├─C面  
  │      │  DBサーバ.txt  
  │      │  group.yaml  
  │      │  マクロ（C面専用）.macro  
  │      │  
  │      └─D面  
  │         DBサーバ.txt  
  │         group.yaml  
  │         マクロ（D面専用）.macro  
  │  
  └─plugins  
      プラグインjarはここに置いてください。
```

## ファイルサンプル
### settings.yamlなど
ツール全体に適用される設定です。  

    width: 600  # ツールの横サイズ
    height: 600 # ツールの縦サイズ
    
    # 初期値
    initial:
      ttpmacroexe: C:\Program Files (x86)\teraterm\ttpmacro.exe
      dir_work: C:\library\work
      dir_log: C:\library\log
      dir_ini: C:\library\ini
      
    inifile: DEV.INI # すべての接続に適用されるTeratermのINIファイル

### tab.yamlなど
タブ配下の接続に適用される設定です。

    # プロキシ経由の場合など(任意)
    gateway:
      ipaddress: xxx.xxx.xxx.xxx    # IPアドレス
      auth: true                    # 認証が必要な場合
      password_memory: true         # 認証パスワードの記憶可否
      password_autoclear: false     # PCログオフ時の認証パスワードクリア
      password_group: dev           # 認証パスワードグループ(タブで認証情報を共有したい場合)
    
    # Teratermでの接続コマンド
    connect: connect '${gateway_ipaddress} /nossh /T=1 /f="${inifile}"'
    
    # 接続後のやりとりを定義(任意)
    negotiation: |
      wait 'Host name:'
      sendln '${ipaddress}'
      wait 'Username:'
      sendln '${authuser}'
      wait 'Password:'
      sendln '${authpassword}'
      wait 'login:'
      sendln '${loginuser}'
      waitregex 'Password.*:'
      sendln '${loginpassword}'
    
    loginuser: aplusr     # タブ配下の接続に適用されるログインユーザー(任意)
    loginpassword: aplpwd # タブ配下の接続に適用されるログインパスワード(任意)
    inifile: AWS.INI      # タブ配下の接続に適用されるTeratermのINIファイル(任意)

### category.yamlなど(category.yamlの配置自体も任意です)
    loginuser: aplusr     # カテゴリ配下の接続に適用されるログインユーザー(任意)
    loginpassword: aplpwd # カテゴリ配下の接続に適用されるログインパスワード(任意)
    inifile: AWS.INI      # カテゴリ配下の接続に適用されるTeratermのINIファイル(任意)
    # ログイン後の処理を書けます(任意)
    procedure: |
      wait ']$ '
      sendln 'pwd'

### group.yamlなど(group.yamlの配置自体も任意です)
    id: group01_01        # ノードID(任意)
    loginuser: aplusr     # グループ配下の接続に適用されるログインユーザー(任意)
    loginpassword: aplpwd # グループ配下の接続に適用されるログインパスワード(任意)
    inifile: AWS.INI      # グループ配下の接続に適用されるTeratermのINIファイル(任意)
    # ログイン後の処理を書けます(任意)
    procedure: |
      wait ']$ '
      sendln 'hostname'
- ノードIDを設定することで、プラグインからノードの判別に利用することができます。

### サーバ別接続情報(*.txt)
    id = kibanusr@websvr001     # ノードID(任意)
    ipaddress = xxx.xxx.xxx.xxx # IPアドレス(必須)
    hostname = websvr001        # ホスト名(必須)
    loginuser = kibanusr        # ログインユーザー(任意)
    loginpassword = kibanpwd    # ログインパスワード(任意)
    inifile = SVR.INI           # TeratermのINIファイル(任意)
- ID(ノードID)を設定することで、プラグインからノードの判別に利用することができます。  
ID(ノードID)を指定しない場合はhostname(ホスト名)の値がIDとして代用されます。  
サーバノード全体をホスト名だけで一意に保てない場合などにID(ノードID)を設定してください。

#### yaml内での変数の利用について
connect、negotiation、procedureには以下の変数を使うことができます。
- gateway_ipaddress  
ゲートウェイIPアドレス
- authuser  
ゲートウェイ認証ユーザー
- authpassword  
ゲートウェイ認証パスワード
- ipaddress  
サーバIPアドレス
- loginuser  
サーバログインユーザー
- loginpassword  
サーバログインパスワード
- inidir  
INIファイル格納ディレクトリ（ツール本体基本設定の値）
- inifile  
INIファイルパス（inidir + ¥ + INIファイル名）
- logdir  
ログファイル出力ディレクトリ（ツール本体基本設定の値）
- workdir  
TTLファイル出力ディレクトリ（ツール本体基本設定の値）
