package SEU_Lex;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LFileParser {
    String program1=" ";
    Map<String, String> defineRules;
    ArrayList<Map.Entry<String, String>> regexRules;
    String program2=" ";

    //默认成员函数，初始化成员变量
    public LFileParser() {
        defineRules = new HashMap<>();
        regexRules = new ArrayList<>();
    }

    //将输入的字符串 ln 解析成一个键值对，并返回一个 Map.Entry 对象。
    private Map.Entry<String, String> lnToPair(String ln) {
        //去除字符串 ln 的前后空白字符。
        //使用正则表达式 \\s+ 将字符串 ln 按照一个或多个空白字符进行分割，最多分割成两部分。这里使用 \\s+ 表示一个或多个空格、制表符、换行符等空白字符。
        String[] parts = ln.trim().split("\\s+", 2);
        //根据分割结果创建一个新的 Map.Entry 对象。如果分割后的结果有两部分，则将第一部分作为键，第二部分作为值；如果分割后的结果只有一部分，则将第一部分作为键，值为空字符串。
        return new HashMap.SimpleEntry<>(parts[0], parts.length > 1 ? parts[1] : "");
    }

    private void readFromStream(BufferedReader reader) throws IOException {
        /*
        * 初始化变量：
        * ln：用于存储读取的每一行内容。
        * step：用于跟踪解析的步骤，初始值为0。
        *  inDef：用于跟踪当前是否处于定义部分，初始值为false。
        */
        String ln;
        int step = 0;
        boolean inDef = false;

        /*
        使用 reader.readLine() 方法逐行读取输入流中的内容，直到读取到文件末尾（即 null）为止。
        每次循环读取一行内容，并将其存储在变量 ln 中。
        */
        while ((ln = reader.readLine()) != null) {
            /*
            如果当前处于定义部分（inDef 为true）：
            如果读取到了定义部分的结束标记 %}，则将 inDef 设置为false，表示已经结束了定义部分。
            否则，将读取到的内容添加到 program1 中，表示定义部分的内容。
            */
            if (inDef) {
                if (ln.equals("%}")) {
                    inDef = false;
                } else {
                    program1 += ln + "\n";
                }
                continue;
            }
            /*如果读取到了定义部分的开始标记 %{，则将 inDef 设置为true，表示当前处于定义部分。*/
            if (ln.equals("%{")) {
                inDef = true;
                continue;
            }
            /*如果读取到了程序部分的分隔标记 %%，则将 step 加1，表示开始解析程序部分。*/
            if (ln.equals("%%")) {
                step++;
                continue;
            }
            /*如果读取到了空行，则继续下一次循环，不做处理。*/
            if (ln.isEmpty()) {
                continue;
            }
            /*
            * 在第一步，如果 step 为0，则表示正在解析定义部分的规则。调用 lnToPair(ln) 方法将读取的内容解析成键值对，并将其添加到 defineRules 中。
            * 在第二步，如果 step 为1，则表示正在解析正则表达式规则。同样调用 lnToPair(ln) 方法将读取的内容解析成键值对，并将其添加到 regexRules 中。
            * 在第三步，如果 step 为2，则表示已经进入程序部分，直接将读取到的内容添加到 program2 中。
            * 如果 step 不在0、1、2中，说明出现了错误，抛出一个运行时异常。
            * */
            switch (step) {
                case 0:
                    Map.Entry<String, String> defineRule = lnToPair(ln);
                    defineRules.put(defineRule.getKey(), defineRule.getValue());
                    break;
                case 1:
                    regexRules.add(lnToPair(ln));
                    break;
                case 2:
                    program2 += ln;
                    break;
                default:
                    throw new RuntimeException("Invalid step");
            }
        }


    }

    //初始化
    public void initAll(String filename) throws IOException {
        InputStream inputstream= Main.class.getClassLoader().getResourceAsStream(filename);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputstream))) {
            readFromStream(reader);
        }
    }

    public LFileParser(String filename) throws IOException {
        this();
        initAll(filename);
    }


}
