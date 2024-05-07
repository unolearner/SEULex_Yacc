package SEU_Lex;

import java.io.IOException;
import java.util.AbstractMap;

public class Main {
        public static void main(String []args) throws IOException {
        LFileParser yyl = new LFileParser();
        yyl.initAll("littlec.l");
        /*输出测试*/
//        System.out.println(yyl.defineRules);
//        System.out.println("test");
//        System.out.println(yyl.program1);
//        System.out.println("test");
//        System.out.println(yyl.program2);
//        System.out.println("test");
        System.out.println(yyl.regexRules);

        RegProcess.replaceBraces(yyl.defineRules);
        //System.out.println(yyl.defineRules);
        RegProcess.regs=yyl.defineRules;
        //System.out.println(yyl.regexRules.size());
        for(var ety:yyl.regexRules){
                String k=ety.getKey();
                if(k.charAt(0)=='\"')   k=RegProcess.quoteErase(k);
                k=RegProcess.turnToReal(k);
                k=RegProcess.replaceBraces(k);
                k=RegProcess.spChProcess(k);
                k=RegProcess.turnBracketToOr(k);
                //System.out.println(k);
                int idx=yyl.regexRules.indexOf(ety);
                yyl.regexRules.set(idx, new AbstractMap.SimpleEntry<>(k,ety.getValue()));

        }
        for(var ety:yyl.regexRules){
                String test=ety.getKey();
                System.out.println(test);
        }
    }
}
