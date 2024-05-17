package SEU_Lex;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

public class Main {
        public static void main(String []args) throws IOException {
        LFileParser yyl = new LFileParser();
        yyl.initAll("littlec.l");
        /*输出测试*/
        System.out.println(yyl.regexRules);


        RegProcess.regs=yyl.defineRules;
        Map<String,String> processed=new HashMap<>();
        for(var p:RegProcess.regs.keySet()){
                var v=RegProcess.regs.get(p);
                v=RegProcess.replaceBraces(v);
                processed.put(p,v);
        }
        RegProcess.regs=processed;
        //System.out.println(yyl.regexRules.size());
//        String tmp=RegProcess.totalProcess("{D}*\\.{D}+{E}?{FS}?");
//        for(int i=0;i<tmp.length();i++){
//                System.out.println(tmp.charAt(i));
//        }
        for(var ety:yyl.regexRules){
                String k=ety.getKey();
                //System.out.println(k);
                k=RegProcess.totalProcess(k);
                int idx=yyl.regexRules.indexOf(ety);
                yyl.regexRules.set(idx, new AbstractMap.SimpleEntry<>(k,ety.getValue()));

        }
//        for(var ety:yyl.regexRules){
//                String test=ety.getKey();
//                System.out.println(test);
//        }
        RgToNFA ctrl=new RgToNFA();
        ctrl.regRules=yyl.regexRules;
        //System.out.println("后缀：");
        ctrl.turnToSufffix();
        ctrl.NFABuilder();
        //System.out.println(RgToNFA.test(RegProcess.totalProcess("{L}?'((\\\\.)|[^\\\\'\\n])+'")));
    }
}
