# SEU_LEX&YACC
本项目为东南大学编译原理专题实践课程设计项目.
## SEULex的用法：
输入<u>.l文件</u>，SEU_LEX会对.l文件进行解析并生成词法分析程序<u>lex.yy.c</u>和<u>lex.yy.exe</u>

——>
在命令行**进入result目录**，键入./result/lex.yy.exe  (the path to "test.c")  (the path to "test.io")

——>
即可得到词法分析的token序列（写入test.io中，默认情况下，会选择当前目录的test.c并在当前目录生成test.io）
## SEUYacc的用法