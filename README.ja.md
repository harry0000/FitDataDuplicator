# FitDataDuplicator
FIT SDK sample code for reading and writing FIT file.

See also https://github.com/harry0000/QiitaPosts/blob/master/article/2015/08/01/5931bbbf3d5b68fe8bc0.md

# What is this?

Garmin Edge等で使用されているFITファイル(.fit ファイル)のデータをFIT SDKでデコードし、取得したデータを新しく作成したファイルにエンコードするサンプルコードです。  
バイナリレベルで全く同じFITファイルを新規作成します。

- Ant+ FIT SDK  
http://www.thisisant.com/resources/fit

このサンプルコードではデコード/エンコード以外の処理を行いませんが、.fitファイルのアクティビティデータを表示したり、異常値のデータを補正して.fitファイルに保存したりするアプリケーション開発の参考になるかと思います。

# Processing flow

1. .fitファイルのHeaderをデコード
2. .fitファイルのMesgDefinition/Mesgをデコード
3. 取得したHeader、MesgDefinition/Mesgをファイルに出力(エンコード)
4. 出力したファイルサイズを調べ、Headerのデータサイズを更新
5. ファイルのCRCを計算し、ファイル終端に追加

# How to Build

### C&#35;

Visual Studio 2013以降が必要です。  
Visual Studioで`cs/FitDataDuplicator.sln`を開いてビルドしてください。

### Java

JDK 8が必要です。

- Gradleがインストールされている環境

```
> gradle jar  
```

- Gradleがインストールされていない環境

```
(for Windows)
> gradlew jar

(for UNIX)
$ ./gradlew jar
```

# How to use

ビルドしたプログラムの引数に.fitファイルへのパスを指定してください。  
同じディレクトリ内に全く同一のファイルが別名で保存されます。

- C#

```
> FitDataDuplicator.exe C:\path\to\fitfile.fit
```

- Java

```
> java -jar FitDataDuplicator.jar C:\path\to\fitfile.fit
```

# License

MIT
