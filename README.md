# TeratermStation

# ツールの概要

あらかじめ設定した接続先情報を元に TeratermMacro を経由して自動で接続を行います。  
接続時に、間にプロキシ認証を挟む必要がある場合でも設定を行うことで自動的に接続できます。  
本ツールを経由して Teraterm接続 を行うことで、自動的に操作ログが記録されます。


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
 「tab.ini」、「category.ini」に、階層ごとに設定を記載する。  
 下位の階層で定義されない場合、上位階層の定義が利用される。  
 
 
`～定義ディレクトリ`  
`  │  settings.yaml`  
`  │  マクロ（ツール）.macro`  
`  │`  
`  ├─開発環境`  
`  │  │  icon.png`  
`  │  │  tab.yaml`  
`  │  │  マクロ（タブ）.macro`  
`  │  │`  
`  │  ├─ｘｘチーム利用環境`  
`  │  ｜  │  category.yaml`  
`  │  ｜  │  マクロ（カテゴリ）.macro`  
`  │  ｜  │`  
`  │  ｜  ├─A面`  
`  │  ｜  │      DBサーバ.txt`  
`  │  ｜  │      Webサーバ.txt`  
`  │  ｜  │      group.yaml`  
`  │  ｜  │      マクロ（A面専用）.macro`  
`  │  ｜  │`  
`  │  ｜  └─B面`  
`  │  ｜          DBサーバ.txt`  
`  │  ｜          APサーバ.txt`  
`  │  ｜          group.yaml`  
`  │  ｜          マクロ（B面専用）.macro`  
`  │  ｜`  
`  │  └─ｘｘチーム利用環境`  
`  │      │  category.yaml`  
`  │      │  マクロ（カテゴリ）.macro`  
`  │      │`  
`  │      ├─C面`  
`  │      │      DBサーバ.txt`  
`  │      │      group.yaml`  
`  │      │      マクロ（C面専用）.macro`  
`  │      │`  
`  │      └─D面`  
`  │              DBサーバ.txt`  
`  │              group.yaml`  
`  │              マクロ（D面専用）.macro`  
`  │`  
`  └─plugins`  


## ファイルサンプル
### settings.yamlなど
    width: 600  
    height: 600  

    initial:
      ttpmacroexe: C:\Program Files (x86)\teraterm\ttpmacro.exe
      dir_work: C:\library\work
      dir_log: C:\library\log
      dir_ini: C:\library\ini
      
    inifile: DEV.INI

### tab.yamlなど
    gateway:
      ipaddress: xxx.xxx.xxx.xxx
      errptn: authentication failed
      auth: true
      password_memory: true
      password_autoclear: false
      password_group: dev
    
    connect: connect '${gateway_ipaddress} /nossh /T=1 /f="${inifile}"'
    
    negotiation: |
      wait 'Host name:'
      sendln '${ipaddress}'
      wait 'Username:'
      sendln '${authuser}'
      wait 'Password:'
      sendln ${authpassword}
      wait 'login:'
      sendln '${loginuser}'
      waitregex 'Password.*:'
      sendln '${loginpassword}'
    
    loginuser: aplusr
    loginpassword: aplpwd
    inifile: AWS.INI

### category.yaml, group.yamlなど
    loginuser: aplusr
    loginpassword: aplpwd
    inifile: AWS.INI

### サーバ別接続情報
    ipaddress = xxx.xxx.xxx.xxx
    hostname = 接続先サーバ名（メモ）
    loginuser = kibanusr
    loginpassword = kibanpwd


 
