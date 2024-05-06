package SEU_Lex;

import java.io.IOException;
import java.util.AbstractMap;

public class Main {
        public static void main(String []args) throws IOException {
        LFileParser yyl = new LFileParser();
        yyl.initAll("littlec.l");
        /*输出测试*/
        System.out.println(yyl.defineRules);
        System.out.println("test");
        System.out.println(yyl.program1);
        System.out.println("test");
        System.out.println(yyl.program2);
        System.out.println("test");
        System.out.println(yyl.regexRules);

        RegProcess.replaceBraces(yyl.defineRules);
        //System.out.println(yyl.defineRules);
        RegProcess.regs=yyl.defineRules;
        for(var ety:yyl.regexRules){
                String k=ety.getKey();
                String tmp=k;
                k=RegProcess.replaceBraces(k);
                if(tmp.equals(k)){
                        int idx=yyl.regexRules.indexOf(ety);
                        yyl.regexRules.set(idx, new AbstractMap.SimpleEntry<>(k,ety.getValue()));
                }
        }
        //System.out.println(yyl.regexRules);
    }
}
