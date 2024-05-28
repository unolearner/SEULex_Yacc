package SEU_Lex;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CFileProducer {
    //.l文件中的直接拷贝部分
    String program1;
    String program2;
    //预置代码部分
    String prepared_program1=
            "#define ECHO echo_str(yytext)\n" +
            "#define error(x) printf(\"Line<%d>, Col<%d> : Lexical analysis fail: %s.\\n\", word_line, word_column+1, x)\n" +
            "\n" +
            "#include <string.h>\n" +
            "\n" +
            "FILE * yyin;\n" +
            "FILE * yyout;\n" +
            "\n" +
            "char yytext[65536];\n" +
            "\n" +
            "int yyleng;\n" +
            "\n" +
            "int yylineno;\n" +
            "//int column;\n" +
            "\n" +
            "int yylex();\n" +
            "\n" +
            "int yywrap();\n" +
            "\n" +
            "char allText[3000000];\n" +
            "int textPtr = 0;\n" +
            "\n" +
            "char input() {\n" +
            "\tchar ch = allText[textPtr];\n" +
            "\t++textPtr;\n" +
            "\tif (ch == '\\n') ++yylineno;\n" +
            "\treturn ch;\n" +
            "}\n" +
            "void unput() {\n" +
            "\tif (textPtr <= 0) return;\n" +
            "\tif (allText[textPtr - 1] == '\\n') {\n" +
            "\t\t--yylineno;\n" +
            "\t}\n" +
            "\t--textPtr;\n" +
            "}\n" +
            "void echo_str(char * yytext);\n" +
            "\n" +
            "int word_line;\n" +
            "int word_column;";
    String propared_program2=
            "\n" +
            "int DFAState;\n" +
            "\n" +
            "int DFAPush(char c);\n" +
            "\n" +
            "int DFAExec(void);\n" +
            "\n" +
            "int DFATry(void);\n" +
            "\n" +
            "int yylex() {\n" +
            "\tword_line = yylineno;\n" +
            "\tword_column = column;\n" +
            "\n" +
            "\tyyleng = 1;\n" +
            "\tyytext[0] = input();\n" +
            "\n" +
            "\tif (yytext[0] == 0) {\n" +
            "\t\tif (yywrap()) return -1;\n" +
            "\t}\n" +
            "\n" +
            "\tDFAState = 0;\n" +
            "\tint lastTerState = 0;\n" +
            "\tint toUnput = 0;\n" +
            "\tint state = DFAPush(yytext[0]);\n" +
            "\twhile (state == 0) {\n" +
            "\t\tif (DFATry()) {\n" +
            "\t\t\tlastTerState = DFAState;\n" +
            "\t\t\ttoUnput = 0;\n" +
            "\t\t}\n" +
            "\t\telse {\n" +
            "\t\t\t++toUnput;\n" +
            "\t\t}\n" +
            "\t\t++yyleng;\n" +
            "\t\tyytext[yyleng - 1] = input();\n" +
            "\t\tif (yytext[yyleng - 1] == 0) break;\n" +
            "\t\tstate = DFAPush(yytext[yyleng - 1]);\n" +
            "\t}\n" +
            "\t++toUnput;\n" +
            "\tfor (int i = 0; i < toUnput; ++i) {\n" +
            "\t\tunput();\n" +
            "\t\t--yyleng;\n" +
            "\t}\n" +
            "\tif (yyleng == 0) {\n" +
            "\t\tyyleng = 1;\n" +
            "\t\tyytext[0] = input();\n" +
            "\t}\n" +
            "\tyytext[yyleng] = '\\0';\n" +
            "\n" +
            "\tint label = DFAExec();\n" +
            "\n" +
            "\treturn label;\n" +
            "}\n" +
            "\n" +
            "char out_str[65536];\n" +
            "\n" +
            "void echo_str(char * yytext) {\n" +
            "\tsprintf(out_str, \" < %d , %d > %s\\x06\\n\", word_line, word_column, yytext);\n" +
            "}";
    String MainFUnction=
            "char inputFilename[65536];\n" +
            "char outputFilename[65536];\n" +
            "int main(int argc, char * argv[]) {\n" +
            "\tyyin = stdin;\n" +
            "\tyyout = stdout;\n" +
            "\n" +
            "\tchar inputFilename[] = \"test.c\";\n" +
            "\tchar outputFilename[] = \"test.lo\";\n" +
            "\n" +
            "\tif (argc == 3) {\n" +
            "\t\tstrcpy(inputFilename, argv[1]);\n" +
            "\t\tstrcpy(outputFilename, argv[2]);\n" +
            "\t}\n" +
            "\n" +
            "\tFILE * outFile = NULL;\n" +
            "\tif (freopen(inputFilename, \"r\", stdin) == NULL ||\n" +
            "\t\t(outFile = fopen(outputFilename, \"w\")) == NULL) {\n" +
            "\t\tprintf(\"fail\\n\");\n" +
            "\t\treturn 1;\n" +
            "\t};\n" +
            "\n" +
            "\tmemset(allText, 0, sizeof allText);\n" +
            "\n" +
            "\tchar c = 0;\n" +
            "\tint p = 0;\n" +
            "\twhile (scanf(\"%c\", &c) > 0) {\n" +
            "\t\tallText[p++] = c;\n" +
            "\t}\n" +
            "\n" +
            "\tyylineno = 1;\n" +
            "\tcolumn = 0;\n" +
            "\twhile (1) {\n" +
            "\t\tout_str[0] = '\\0';\n" +
            "\t\tint x = yylex();\n" +
            "\t\tif (x == -1) break;\n" +
            "\t\tif (x!=0){\n" +
            "\t\t\tchar echoed_str[65536] = \"\";\n" +
            "\t\t\tsprintf(echoed_str, \"%d\", x);\n" +
            "\t\t\tstrcat(echoed_str, out_str);\n" +
            "\t\t\tfprintf(outFile, \"%s\", echoed_str);\n" +
            "\t\t}\n" +
            "\t}\n" +
            "\n" +
            "\treturn 0;\n" +
            "}";
    //由DFA自动生成的代码
    String DFAProgram;

    public  String getDFAProgram(DFABuilder.DFA dfa){
        /*
        此函数用于将DFA转换为三个函数
        - DFAExec——识别出对应正则式，应该做些什么
        - DFATry——判断当前状态是否为终态
        - DFAPush——接收一个字符，正常接收时返回0，不能正常接收时返回1
          case_list由String映射到Set，因为有些状态的动作是一样的
          new HashSet的问题，注意先判断是否有这么一个键
         */
        String DFAExec="", DFATry ="",DFAPush="";
        Map<String, Set<Integer>>case_list=new HashMap<>();
        DFAExec+="int DFAExec(void) {\n";
//        for(var st:dfa.states){
//            if(st.isEnd){
//                String act=dfa.endStates.get(st.id).actions;
//                case_list.put(act,new HashSet<>());
//                case_list.get(act).add(st.id);
//            }
//        }
        for(var ed:dfa.endStates.keySet()){
            String act=dfa.endStates.get(ed).actions;
            if(!case_list.containsKey(act)) case_list.put(act,new HashSet<>());
            case_list.get(act).add(ed);
        }
        DFAExec+=SwitchCaseProducer.switchBuilder("DFAState",case_list,"error(\"unexpected word\");")+" return WHITESPACE;\n}";

        case_list.clear();
        DFATry +="int DFATry(void) {\n";
        String action ="return 1;";
        case_list.put(action,new HashSet<>());
//        for(var st:dfa.states){
//            if(st.isEnd){
//                case_list.get(action).add(st.id);
//            }
//        }
        for(var ed:dfa.endStates.keySet()){
            case_list.get(action).add(ed);
        }
        DFATry+=SwitchCaseProducer.switchBuilder("DFAState",case_list,"return 0;")+"\n} ";

        case_list.clear();
        DFAPush+="int DFAPush(char c) {\n";
        for(var st:dfa.states){
            Map<String,Set<Integer>>case_list1=new HashMap<>();
            for(var ch:st.transitions.keySet()){
                int nt=st.transitions.get(ch);
                String act="DFAState="+nt+"; ";//正常接收就返回0
                if(!case_list1.containsKey(act))    case_list1.put(act,new HashSet<>());
                case_list1.get(act).add(Integer.valueOf(ch));
            }
            String tmp=SwitchCaseProducer.switchBuilder("c",case_list1,"return 1; ")+"return 0; ";//default情况返回1，表示不接受
            if(!case_list.containsKey(tmp))  case_list.put(tmp,new HashSet<>());
            case_list.get(tmp).add(st.id);
        }
        DFAPush+=SwitchCaseProducer.switchBuilder("DFAState",case_list,"return 0; ")+"return 0;\n}";

        return DFAExec+'\n'+ DFATry +'\n'+DFAPush;
    }

    public void yieldLex(){
        /*
        生成完整的词法分析器程序，写入文件中，生成lex.yy.c
         */
        String final_program=
                "#define _CRT_SECURE_NO_WARNINGS\n"
                +program1
                        +"\n\n"
                +prepared_program1
                        +"\n\n"
                +program2
                        +"\n\n"
                +propared_program2
                        +"\n\n"
                +DFAProgram
                        +"\n\n"
                +MainFUnction;


        String fileName="lex.yy.c";
        String currentDir = System.getProperty("user.dir");
        String resultDir = Paths.get(currentDir, "result").toString();
        String path=resultDir+'\\'+fileName;
        try {
            File file = new File(path);

            // 如果文件不存在,则创建新文件
            if (!file.exists()) {
                File directory = new File(file.getParent());
                if (!directory.exists()) {
                    directory.mkdirs(); // 创建目录
                }
                file.createNewFile(); // 创建文件
            }

            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(final_program);
            bufferedWriter.flush();
            bufferedWriter.close();
            System.out.println("File written successfully.");
        } catch (IOException e) {
            System.err.println("An error occurred while writing the file.");
            e.printStackTrace();
        }
    }



}
