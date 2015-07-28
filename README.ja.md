# FitDataDuplicator
This is a sample code for reading and writing FIT file using the FIT SDK.

# What is this?

Garmin Edge等で使用されているFITファイル(.fit ファイル)のデータをFIT SDKでデコードし、得られたオブジェクトを新しく作成したファイルにエンコードするサンプルコードです。  
バイナリレベルで全く同じFITファイルが新たに作成されます。

- Ant+ FIT SDK  
http://www.thisisant.com/resources/fit

このサンプルコードではデコード/エンコード以外の処理を行いませんが、.fitファイルのアクティビティデータを表示したり、異常値のデータを補正して.fitファイルに保存したりするアプリケーション開発の参考になるかと思います。

# 処理の流れ

1. .fitファイルのHeaderをデコード
2. .fitファイルのMesgDefinition/Mesgをデコード
3. 取得したHeader、MesgDefinition/Mesgをファイルに出力(エンコード)
4. 出力したファイルサイズを調べ、Headerのデータサイズを更新
5. ファイルのCRCを計算し、ファイル終端に追加

# ビルド方法

- C＃  
Visual Studio 2013以降で[```cs/FitDataDuplicator.sln```](./cs/FitDataDuplicator.sln)を開き、ビルドしてください。

- Java  

  - Gradleがインストールされている環境

          > gradle jar  

  - Gradleがインストールされていない環境

          (for Windows)
          > gradlew jar
         
          (for UNIX)
          $ ./gradlew jar

# 使用方法

ビルドしたプログラムの引数に.fitファイルへのパスを指定してください。  
同じディレクトリ内に全く同一のファイルが別名で保存されます。

- C#

        > FitDataDuplicator.exe C:\path\to\fitfile.fit

- Java

        > java -jar FitDataDuplicator.jar C:\path\to\fitfile.fit

# ライセンス

MIT
