package SEU_Yacc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class YaccParser {
    private List<String> terminal = new ArrayList<>();
    private String start;
    private List<Map.Entry<String, List<String>>> producerList = new ArrayList<>();
    private String program2="";

    /**
     * definerules函数用于解析 Yacc 文件中关于定义的规则。
     * Author：Hank Captain
     * Date：2024/5/3
     * Version：1.0
     * @param ln
     */
    public void defineRules(String ln) {
        //两个字符串分别用来存储规则的左部与右部
        String left = "", right = "";
        //len定义每一行规则的长度，在后续循环中使用
        int len = ln.length();
        //循环变量i
        int i = 0;
        boolean flag = true;
        //规则段位于整个文本的最开始，出现%%定义段就结束了。
        if (ln.charAt(i) == '%' && ln.charAt(i + 1) != '%') {
            ++i;
            //函数移动 i 指针，跳过空格，直到找到规则的左部字符。
            while (i < len && ln.charAt(i) == ' ') ++i;
            //函数从当前位置 i 开始，逐个字符向 left 中添加字符，直到遇到空格或字符串末尾。
            while (i < len && !(ln.charAt(i) == ' ')) {
                left += ln.charAt(i);
                ++i;
            }

            //函数再次移动 i 指针，跳过空格，准备读取规则的右部。
            while (i < len && ln.charAt(i) == ' ')	i++;

            //如果左部是 "token"，则表示接下来是终结符的定义。函数会将右部中的字符串（可能是多个终结符）逐个添加到 terminal 列表中。
            if (left.equals("token")) {
                for (; i < len; i++){
                    if (ln.charAt(i) != ' ') {
                        right += ln.charAt(i);
                        if (i == len - 1)
                            terminal.add(right);
                    }
                    else {
                        terminal.add(right);
                        right = "";
                    }
                }
                //如果左部是 "start"，则表示接下来是起始符号的定义。函数会将右部中的字符串作为起始符号。
            } else if (left.equals("start")) {
                start = "";
                while (i < len) start += ln.charAt(i++);
            } else {
                //如果左部既不是 "token" 也不是 "start"，则函数抛出异常。这可能意味着 Yacc 文件中存在无效的规则定义。
                throw new RuntimeException();
            }
        }
    }

    public void readProducerRight(String str , BufferedReader ifile, String left) throws IOException {
        //String str;
        int i=0;
        do {
            List<String> curRight = new ArrayList<>();
            if(str.charAt(i)=='\t'){
                i++;
            }
            while((str.charAt(i)=='|') | (str.charAt(i)==':')){
                int len=str.length();
                i++;
                String temp="";
                char a=str.charAt(i);
                while(str.charAt(i)==' '){
                    i++;
                }
                while(i<len){
                    temp+=str.charAt(i);
                    i++;
                }
                curRight.add(temp);
                str = ifile.readLine();
                i=0;
                if(str.charAt(i)=='\t'){
                    i++;
                }
                //str = ifile.readLine();
            }
            /*
            while (!"|".equals(str) && !";".equals(str)) {
                curRight.add(str);
                str = ifile.readLine();
            }*/
            producerList.add(new AbstractMap.SimpleEntry<>(left, curRight));


        } while (!(str.charAt(i)==';'));
    }

    public void producerParsing(BufferedReader ifile) throws IOException {
        String left;
        do {
            left = ifile.readLine();
        } while (left.equals(""));

        while (!left.equals("%%")) {
            String str = ifile.readLine();
            int i=0;
            if(str.charAt(i)=='\t'){
                i++;
            }
            if (!(str.charAt(i)==':') && !(str.charAt(i)=='|'))
                //if (!":".equals(str.charAt(i)) && !"|".equals(str.charAt(i)))
                throw new RuntimeException();
            readProducerRight(str,ifile, left);

            do {
                left = ifile.readLine();
            } while (left.equals(""));
        }
    }

    public void readFromStream(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String ln;
        int step = 0;
        while ((ln = reader.readLine()) != null) {
            if (ln.equals("%%")) {
                ++step;
                if (step == 1) {
                    producerParsing(reader);
                    ++step;
                }
                continue;
            }
            if (ln.equals("")) continue;

            switch (step) {
                case 0:
                    defineRules(ln);
                    break;
                case 2:
                    program2 += ln + '\n';
                    break;
                default:
                    //break;
                    throw new RuntimeException();
            }
        }
        reader.close();
    }

    public boolean strListContain(List<String> str, String stt) {
        for (String s : str) {
            if (s.equals(stt)) return true;
        }
        return false;
    }

    public void lastDeal() {
        List<String> allTer = new ArrayList<>();
        List<String> allNotTer = new ArrayList<>();
        List<Integer> terIds = new ArrayList<>();
        List<Integer> gIds = new ArrayList<>();
        int terId = 300;
        int gId = 999;
        for (String s : terminal) {
            allTer.add(s);
            terIds.add(terId++);
        }
        allNotTer.add("__PROGRAM__");
        gIds.add(gId++);
        allNotTer.add(start);
        gIds.add(gId++);
        for (Map.Entry<String, List<String>> producer : producerList) {
            for (String k : producer.getValue()) {
                if (k.charAt(0) == '\'') {
                    if (strListContain(allTer, k)) continue;
                    else {
                        allTer.add(k);
                        terIds.add(k.charAt(1));
                    }
                } else {
                    if (strListContain(allNotTer, k) || strListContain(allTer, k)) continue;
                    else {
                        allNotTer.add(k);
                        gIds.add(gId++);
                    }
                }
            }
        }

        for (int i = 0; i < terIds.size(); ++i) {
            appendNewSymbol(terIds.get(i), allTer.get(i), true);
        }
        for (int i = 0; i < gIds.size(); ++i) {
            appendNewSymbol(gIds.get(i), allNotTer.get(i), false);
        }
        producers.add(new AbstractMap.SimpleEntry<>(idnameToIdx("__PROGRAM__"), new ArrayList<>(List.of(idnameToIdx(start)))));
        for (Map.Entry<String, List<String>> pro : producerList) {
            int left = idnameToIdx(pro.getKey());
            List<Integer> v = new ArrayList<>();
            for (String str : pro.getValue()) {
                v.add(idnameToIdx(str));
            }
            producers.add(new AbstractMap.SimpleEntry<>(left, v));
        }
    }

    public void writeTabs(String filename) throws IOException {
        FileWriter tabFile = new FileWriter(filename);
        writeTab(tabFile);
        tabFile.close();
    }

    // Dummy method placeholders
    private void appendNewSymbol(int id, String name, boolean isTerminal) {
        // Placeholder implementation
    }

    private int idnameToIdx(String name) {
        // Placeholder implementation
        return 0;
    }

    private void writeTab(FileWriter tabFile) throws IOException {
        // Placeholder implementation
    }

    public YaccParser() {
        // Default constructor
    }

    public YaccParser(String filename) throws IOException {
        initAll(filename);
    }

    public void initAll(String filename) throws IOException {
        readFromStream(filename);

        //lastDeal();
    }



    public static void main(String[] args) throws IOException {
        // Usage example
        YaccParser parser = new YaccParser();
        parser.readFromStream("littlec.y");
        System.out.println(parser.producerList);
        System.out.println(parser.program2);
        System.out.println(parser.start);
        System.out.println(parser.terminal);



    }


}
