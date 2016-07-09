#### TeratermStationの概要

WindowsからLinux/Unixサーバへの接続では広くteratermが使われています。  
開発現場ではteratermでの接続手順をttlファイルというマクロファイルに記述して  
ttpmacro.exeを使って接続していると思います。  
実際にはこのttlファイルをサーバ分用意しておく、またはteratermマクロのコマンドなどを  
駆使して接続先を選択させたり、認証情報を入力させたりして接続の効率化をしています。  

TeratermStationはこのようなttlの管理を無くし、さらにわかりやすいインターフェースで  
目的のサーバへの接続、また接続後に行う付加機能を提供します。  
TeratermStationの基本機能はteratermの接続を便利にする機能がメインとなりますが  
拡張プラグインをサポートすることで、目的のサーバに対して、または目的のサーバから何かを  
行うための任意の機能をTeratermStationに持たせることができます。  

***

#### スクリーンショット
![screenshot](https://github.com/turbou/TeratermStation/wiki/images/screenshot.png)

***

#### 動作条件
- Tera Term 4.58以上がインストールされている。
- JRE1.7以上がインストールされている。

***

#### 接続情報の設定
TeratermStationではサーバへの接続情報をPC上の通常のフォルダツリーから  
ロードして内部に接続情報を保持します。 (下はシンプルな接続定義の例です)  
```
XXXXProject
  └─開発環境
    ├─基盤チーム
    │  ├─A面
    │  │  DBサーバ.yaml
    │  │  Webサーバ.yaml
    │  └─B面
    │     DBサーバ.yaml
    │     Webサーバ.yaml
    └─業務チーム
       ├─C面
       │  DBサーバ.yaml
       │  Webサーバ.yaml
       └─D面
          DBサーバ.yaml
          Webサーバ.yaml
```
TeratermStationには基点ディレクトリである__XXXXProject__を指定します。  
TeratermStationではこのようにツール本体とは独立して接続設定を用意することができます。  
フォルダツリーで構成する接続設定は通常のフォルダ操作でサーバ構成を変更したり  
接続情報やその他設定ファイルは普通のテキストエディタで編集することが可能です。  

プロジェクトによってはこの接続設定フォルダを各開発者に提供する際
- SVNリポジトリで管理して、各開発者にチェックアウトさせて提供する。
- 共有フォルダにアーカイブした接続定義フォルダを配置して取得させる。
- 共有フォルダにそのまま接続定義フォルダを置いておく。
のような方法で展開することもできます。

TeratermStationの詳細については[Wiki](https://github.com/turbou/TeratermStation/wiki)を参照してください。
