package SEU_Lex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Main {
        public static void testDFAbuilder() throws IOException {
                RgToNFA test=new RgToNFA();
                test.st=7;
                RgToNFA.NFAState []states=new RgToNFA.NFAState[9];
                for (int i = 0; i < states.length; i++) {
                        states[i] = new RgToNFA.NFAState();
                }
                states[1].id=1;
                states[1].isStart=false;
                states[1].isEnd=false;
                states[1].transitions.put('a',new HashSet<>());
                states[1].transitions.get('a').add(3);
                states[1].transitions.put('b',new HashSet<>());
                states[1].transitions.get('b').add(4);
                states[2].id=2;
                states[2].isStart=false;
                states[2].isEnd=false;
                states[2].transitions.put('\u0000',new HashSet<>());
                states[2].transitions.get('\u0000').add(6);
                states[3].id=3;
                states[3].isStart=false;
                states[3].isEnd=false;
                states[3].transitions.put('a',new HashSet<>());
                states[3].transitions.get('a').add(2);
                states[4].id=4;
                states[4].isStart=false;
                states[4].isEnd=false;
                states[4].transitions.put('b',new HashSet<>());
                states[4].transitions.get('b').add(2);
                states[5].id=5;
                states[5].isStart=false;
                states[5].isEnd=false;
                states[5].transitions.put('a',new HashSet<>());
                states[5].transitions.get('a').add(5);
                states[5].transitions.put('b',new HashSet<>());
                states[5].transitions.get('b').add(5);
                states[5].transitions.put('\u0000',new HashSet<>());
                states[5].transitions.get('\u0000').add(1);
                states[6].id=6;
                states[6].isStart=false;
                states[6].isEnd=false;
                states[6].transitions.put('a',new HashSet<>());
                states[6].transitions.get('a').add(6);
                states[6].transitions.put('b',new HashSet<>());
                states[6].transitions.get('b').add(6);
                states[6].transitions.put('\u0000',new HashSet<>());
                states[6].transitions.get('\u0000').add(8);
                states[7].id=7;
                states[7].isStart=true;
                states[7].isEnd=false;
                states[7].transitions.put('\u0000',new HashSet<>());
                states[7].transitions.get('\u0000').add(5);
                states[8].id=8;
                states[8].isStart=false;
                states[8].isEnd=true;
                test.states.put(8,states[8]);
                test.endStates.put(8,new RgToNFA.Rules("test","test"));
                test.states.put(1,states[1]);
                test.states.put(2,states[2]);
                test.states.put(3,states[3]);
                test.states.put(4,states[4]);
                test.states.put(5,states[5]);
                test.states.put(6,states[6]);
                test.states.put(7,states[7]);
                test.show();
                DFABuilder builder=new DFABuilder(test);
                builder.final_dfa.show();
        }


        public static void main(String []args) throws IOException, InterruptedException {
        LFileParser yyl = new LFileParser();
        yyl.initAll("littlec.l");
        /*输出测试*/
        System.out.println(yyl.regexRules);


        RegProcess.regs=yyl.defineRules;
        Map<String,String> processed=new HashMap<>();
        for(var p:RegProcess.regs.keySet()){//先处理前面那部分正规表达式定义中的嵌套
                var v=RegProcess.regs.get(p);
                v=RegProcess.replaceBraces(v);
                processed.put(p,v);
        }
        RegProcess.regs=processed;
        for(var ety:yyl.regexRules){
                String k=ety.getKey();
                k=RegProcess.totalProcess(k);
                int idx=yyl.regexRules.indexOf(ety);
                yyl.regexRules.set(idx, new AbstractMap.SimpleEntry<>(k,ety.getValue()));

        }
        RgToNFA ctrl=new RgToNFA();
        ctrl.regRules=yyl.regexRules;
        ctrl.turnToSufffix();
        ctrl.NFABuilder();
        //ctrl.show();
        DFABuilder builder=new DFABuilder(ctrl);
        //builder.final_dfa.show();
//
        CFileProducer generator=new CFileProducer();
        generator.DFAProgram=generator.getDFAProgram(builder.final_dfa);
        generator.program1= yyl.program1;
        generator.program2=yyl.program2;
        generator.yieldLex();

        //testDFAbuilder();
    }
}
