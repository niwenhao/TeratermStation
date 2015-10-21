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
`  │  settings.ini`  
`  │  マクロ（ツール）.macro`  
`  │`  
`  ├─開発環境`  
`  │  │  icon.png`  
`  │  │  tab.ini`  
`  │  │  マクロ（タブ）.macro`  
`  │  │`  
`  │  ├─ｘｘチーム利用環境`  
`  │  ｜  │  category.ini`  
`  │  ｜  │  マクロ（カテゴリ）.macro`  
`  │  ｜  │`  
`  │  ｜  ├─A面`  
`  │  ｜  │      DBサーバ.txt`  
`  │  ｜  │      Webサーバ.txt`  
`  │  ｜  │      group.ini`  
`  │  ｜  │      マクロ（A面専用）.macro`  
`  │  ｜  │`  
`  │  ｜  └─B面`  
`  │  ｜          DBサーバ.txt`  
`  │  ｜          APサーバ.txt`  
`  │  ｜          group.ini`  
`  │  ｜          マクロ（B面専用）.macro`  
`  │  ｜`  
`  │  └─ｘｘチーム利用環境`  
`  │      │  category.ini`  
`  │      │  マクロ（カテゴリ）.macro`  
`  │      │`  
`  │      ├─C面`  
`  │      │      DBサーバ.txt`  
`  │      │      group.ini`  
`  │      │      マクロ（C面専用）.macro`  
`  │      │`  
`  │      └─D面`  
`  │              DBサーバ.txt`  
`  │              group.ini`  
`  │              マクロ（D面専用）.macro`  
`  │`  
`  └─plugins`  


## ファイルサンプル

### tab.iniなど

`gateway_ipaddress = xxx.xxx.xxx.xxx`  
`gateway_errptn = authentication failed`  
`gateway_auth = true`  
`gateway_password_memory = true`  
`gateway_password_autoclear = false`  
`gateway_password_group = aws`  
`connect = connect '${gateway_ipaddress} /nossh /T=1 /f="${inifile}"'`  
`negotiation = \`  
`wait 'Host name:'\r\n\`  
`sendln '${ipaddress}'\r\n\`  
`wait 'Username:'\r\n\`  
`sendln '${authuser}'\r\n\`  
`wait 'Password:'\r\n\`  
`sendln ${authpassword}\r\n\`  
`wait 'login:'\r\n\`  
`sendln '${loginuser}'\r\n\`  
`waitregex 'Password.*:'\r\n\`  
`sendln '${loginpassword}'\`  
`usemacro = true`  
`inifile = AWS.INI`  

### サーバ別接続情報

`ipaddress = xxx.xxx.xxx.xxx`  
`hostname = 接続先サーバ名（メモ）`  
`loginuser = loginUser`  
`loginpassword = loginPassword`  


 