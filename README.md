# FitDataDuplicator
FIT SDK sample code for reading and writing FIT file.

- Ant+ FIT SDK  
http://www.thisisant.com/resources/fit

for Japanese [README.ja.md](README.ja.md)

# How to build

- C#

You will need to install Visual Studio 2013 or later.  
Open `cs/FitDataDuplicator.sln` in Visual Studio and build.

- Java

You will need to install JDK 8.

```
> git clone https://github.com/harry0000/FitDataDuplicator.git
> cd FitDataDuplicator\java\
> gradlew jar
```

# How to use

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