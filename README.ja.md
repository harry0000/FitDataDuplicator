# FitDataDuplicator
This is a sample code for reading and writing FIT file using the FIT SDK.

# What is this?

Garmin Edge���Ŏg�p����Ă���FIT�t�@�C��(.fit �t�@�C��)�̃f�[�^��FIT SDK�Ńf�R�[�h���A����ꂽ�I�u�W�F�N�g��V�����쐬�����t�@�C���ɃG���R�[�h����T���v���R�[�h�ł��B  
�o�C�i�����x���őS������FIT�t�@�C�����V���ɍ쐬����܂��B

- Ant+ FIT SDK  
http://www.thisisant.com/resources/fit

���̃T���v���R�[�h�ł̓f�R�[�h/�G���R�[�h�ȊO�̏������s���܂��񂪁A.fit�t�@�C���̃A�N�e�B�r�e�B�f�[�^��\��������A�ُ�l�̃f�[�^��␳����.fit�t�@�C���ɕۑ������肷��A�v���P�[�V�����J���̎Q�l�ɂȂ邩�Ǝv���܂��B

# �����̗���

1. .fit�t�@�C����Header���f�R�[�h
2. .fit�t�@�C����MesgDefinition/Mesg���f�R�[�h
3. �擾����Header�AMesgDefinition/Mesg���t�@�C���ɏo��(�G���R�[�h)
4. �o�͂����t�@�C���T�C�Y�𒲂ׁAHeader�̃f�[�^�T�C�Y���X�V
5. �t�@�C����CRC���v�Z���A�t�@�C���I�[�ɒǉ�

# �r���h���@

- C��  
Visual Studio 2013�ȍ~��[```cs/FitDataDuplicator.sln```](./cs/FitDataDuplicator.sln)���J���A�r���h���Ă��������B

- Java  

  - Gradle���C���X�g�[������Ă����

          > gradle jar  

  - Gradle���C���X�g�[������Ă��Ȃ���

          (for Windows)
          > gradlew jar
         
          (for UNIX)
          $ ./gradlew jar

# �g�p���@

�r���h�����v���O�����̈�����.fit�t�@�C���ւ̃p�X���w�肵�Ă��������B  
�����f�B���N�g�����ɑS������̃t�@�C�����ʖ��ŕۑ�����܂��B

- C#

        > FitDataDuplicator.exe C:\path\to\fitfile.fit

- Java

        > FitDataDuplicator.jar C:\path\to\fitfile.fit

# ���C�Z���X

MIT