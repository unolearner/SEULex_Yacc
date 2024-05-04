package SEU_Yacc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class SymbolHandler {
    // Producer 类型表示一个整数索引和整数索引向量的对应关系
    static class Producer {
        int index;
        Vector<Integer> symbols;

        public Producer(int index, Vector<Integer> symbols) {
            this.index = index;
            this.symbols = symbols;
        }
    }
    Vector<Integer> num= new Vector<>();
    Producer a = new Producer(0, num) ;

    // 存储语法产生式的 Producer 实例的向量
    static Vector<Producer> producers = new Vector<>();

    // 将字符串名字与整数索引进行映射的 map
    static Map<String, Integer> mapIdnameIdx = new HashMap<>();

    // 将整数索引与字符串名字进行映射的 map
    static Map<Integer, String> mapIdxIdname = new HashMap<>();

    // 包含终结符索引的集合
    static Set<Integer> terminators = new HashSet<>();

    // 将字符串名字转换为对应索引的函数
    static int idnameToIdx(String name) {
        return mapIdnameIdx.get(name);
    }

    // 将整数索引转换为对应字符串名字的函数
    static String idxToIdname(int idx) {
        return mapIdxIdname.get(idx);
    }

    // 将新符号追加到映射和集合中，并标记是否为终结符的函数
    static void appendNewSymbol(int idx, String name, boolean isTerminator) {
        mapIdnameIdx.put(name, idx);
        mapIdxIdname.put(idx, name);
        if (isTerminator) {
            terminators.add(idx);
        }
    }

    // 检查符号是否为终结符的函数（通过索引）
    static boolean isTerminator(int idx) {
        return terminators.contains(idx);
    }

    // 检查符号是否为终结符的函数（通过字符串名字）
    static boolean isTerminator(String name) {
        int idx = idnameToIdx(name);
        return isTerminator(idx);
    }

    // 打印语法产生式的函数
    static void dumpProducers() {
        for (Producer producer : producers) {
            String left = idxToIdname(producer.index);
            System.out.print(left + " : => ");
            for (int item : producer.symbols) {
                System.out.print(idxToIdname(item) + " ");
            }
            System.out.println();
        }
    }

    // 将终结符写入文件的函数
    static void writeTab() {
        System.out.println("#define WHITESPACE 0");
        for (Map.Entry<String, Integer> entry : mapIdnameIdx.entrySet()) {
            String name = entry.getKey();
            int idx = entry.getValue();
            if (!isTerminator(idx)) continue;
            if (!(name.charAt(0) >= 'a' && name.charAt(0) <= 'z') &&
                    !(name.charAt(0) >= 'A' && name.charAt(0) <= 'Z')) continue;
            System.out.println("#define " + name + " " + idx);
        }
    }

}
