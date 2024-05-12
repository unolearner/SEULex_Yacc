package SEU_Lex;

import java.util.*;

public class RgToNFA {
    class NFAState{
        int id;
        Map<Character, Set<Integer>> transitions;
        boolean isStart;
        boolean isEnd;

        public NFAState(){
            transitions=new HashMap<>();
        }

    }
    class Rules{
        String pattern;
        String  actions;
    }

    ArrayList<Map.Entry<String,String> > regRules;
    int st=0;//初态
    Set<NFAState>states;//状态集
    Map<Integer,Rules>endStates;//终态

    public RgToNFA(){
        states=new HashSet<>();
        endStates=new HashMap<>();
    }

    public void turnToSufffix(){
        /*
        将正规式变成后缀形式
        注意转义字符的处理
        注意栈的判空。空时不能取栈顶，不能pop
         */
        for(var ety:regRules){
            Stack<Character>    stk = new Stack<>();
            String k=ety.getKey();
            StringBuilder res=new StringBuilder();
            int l=k.length();
            char tp;
            boolean flag=false;//标记前面一个是不是斜杠
            for(int i=0;i<l;i++){
                char c=k.charAt(i);
                if(c=='\\'){
                    flag=true;
                    res.append(c);
                    continue;
                }
                switch(c){
                    case '(':
                        stk.push(c);
                        break;
                    case '*':
                        if (!stk.empty()&&!flag) {
                            tp=stk.peek();
                            while(!stk.empty()&&tp=='*'){
                                stk.pop();
                                res.append(tp);
                                if(!stk.empty())    tp=stk.peek();
                            }
                        }
                        if(!flag)   stk.push(c);
                        else res.append(c);
                        break;
                    case '.':
                        if (!stk.empty()&&!flag) {
                            tp=stk.peek();
                            while(!stk.empty()&&(tp=='*'||tp=='.')){
                                stk.pop();
                                res.append(tp);
                                if(!stk.empty()) tp=stk.peek();
                            }
                        }
                        if(!flag)   stk.push(c);
                        else res.append(c);
                        break;
                    case '|':
                        if (!stk.empty()&&!flag) {
                            tp=stk.peek();
                            while(!stk.empty()&&tp!='('){
                                stk.pop();
                                res.append(tp);
                                if(!stk.empty()) tp=stk.peek();
                            }
                        }
                        if(!flag)   stk.push(c);
                        else res.append(c);
                        break;
                    case ')':
                        if (!flag) {
                            tp=stk.peek();
                            while(!stk.empty()&&tp!='('){
                                res.append(stk.pop());
                                if(!stk.empty()) tp=stk.peek();
                            }
                            stk.pop();
                        }
                        else res.append(c);
                        break;
                    default:
                        res.append(c);
                        break;
                }
                if(flag)    flag=false;
            }
            while(!stk.empty()){
                res.append(stk.pop());
            }
            //System.out.println(res);
            int idx=regRules.indexOf(ety);
            regRules.set(idx,new AbstractMap.SimpleEntry<>(res.toString(),ety.getValue()));
        }
    }
    public static String test(String k){
        /*
        此函数用于测试
         */
        Stack<Character>    stk = new Stack<>();
        StringBuilder res=new StringBuilder();
        int l=k.length();
        char tp;
        boolean flag=false;//标记前面一个是不是斜杠
        for(int i=0;i<l;i++){
            char c=k.charAt(i);
            if(c=='\\'){
                flag=true;
                continue;
            }
            switch(c){
                case '(':
                    stk.push(c);
                    break;
                case '*':
                    if (!stk.empty()&&!flag) {
                        tp=stk.peek();
                        while(!stk.empty()&&tp=='*'){
                            stk.pop();
                            res.append(tp);
                            if(!stk.empty())    tp=stk.peek();
                        }
                    }
                    if(!flag)   stk.push(c);
                    else res.append(c);
                    break;
                case '.':
                    if (!stk.empty()&&!flag) {
                        tp=stk.peek();
                        while(!stk.empty()&&(tp=='*'||tp=='.')){
                            stk.pop();
                            res.append(tp);
                            if(!stk.empty()) tp=stk.peek();
                        }
                    }
                    if(!flag)   stk.push(c);
                    else res.append(c);
                    break;
                case '|':
                    if (!stk.empty()&&!flag) {
                        tp=stk.peek();
                        while(!stk.empty()&&tp!='('){
                            stk.pop();
                            res.append(tp);
                            if(!stk.empty()) tp=stk.peek();
                        }
                    }
                    if(!flag)   stk.push(c);
                    else res.append(c);
                    break;
                case ')':
                    if (!flag) {
                        tp=stk.peek();
                        while(!stk.empty()&&tp!='('){
                            res.append(stk.pop());
                            if(!stk.empty()) tp=stk.peek();
                        }
                        stk.pop();
                    }
                    else res.append(c);
                    break;
                default:
                    res.append(c);
                    break;
            }
        }
        while(!stk.empty()){
            res.append(stk.pop());
        }
        return res.toString();
    }



}
