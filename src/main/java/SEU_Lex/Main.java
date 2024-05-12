package SEU_Lex;

import java.io.IOException;
import java.util.AbstractMap;

public class Main {
        public static void main(String []args) throws IOException {
        LFileParser yyl = new LFileParser();
        yyl.initAll("littlec.l");
        /*输出测试*/
        System.out.println(yyl.regexRules);


        RegProcess.regs=yyl.defineRules;
        //System.out.println(yyl.regexRules.size());
        for(var ety:yyl.regexRules){
                String k=ety.getKey();
                k=RegProcess.totalProcess(k);
                int idx=yyl.regexRules.indexOf(ety);
                yyl.regexRules.set(idx, new AbstractMap.SimpleEntry<>(k,ety.getValue()));

        }
        for(var ety:yyl.regexRules){
                String test=ety.getKey();
                System.out.println(test);
        }
        RgToNFA ctrl=new RgToNFA();
        ctrl.regRules=yyl.regexRules;
        //System.out.println("后缀：");
        ctrl.turnToSufffix();
//        for(var ety:ctrl.regRules){
//                String k=ety.getKey();
//                int l=k.length();
//                for(int i=0;i<l;i++){
//                        char c=k.charAt(i);
//                        switch(c){
//                                case '|':
//                                        break;
//                                case '*':
//                                        break;
//                                case '.':
//                                        break;
//                                default:
//
//                                        break;
//                        }
//                }
//        }
    }
}
