package SEU_Lex;

import java.util.*;

public class RgToNFA {
    static class NFAState{
        int id;
        Map<Character, Set<Integer>> transitions;
        boolean isStart=false;
        boolean isEnd=false;

        public NFAState(){

            transitions=new HashMap<>();
        }

    }
    static class Rules{
        String pattern;
        String  actions;


        public Rules(String p,String a){
            this.pattern=p;
            this.actions=a;
        }

    }

    ArrayList<Map.Entry<String,String> > regRules;
    int cnt=0;//工具,用于赋予状态ID
    char ep='\u0000';//空串
    int st;//初态
    Map<Integer,NFAState>states;//状态集
    Map<Integer,Rules>endStates;//终态

    public RgToNFA(){
        states=new HashMap<>();
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
            System.out.println("中缀："+k);
            StringBuilder res=new StringBuilder();
            int l=k.length();
            char tp;
            boolean flag=false;//标记前面一个是不是斜杠
            for(int i=0;i<l;i++){
                char c=k.charAt(i);
                if(c=='\\'&&!flag){
                    flag=true;
                    res.append(c);
                    continue;
                }
                switch(c){
                    case '(':
                        if(flag)    res.append(c);
                        else    stk.push(c);
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
            System.out.println("后缀："+res);
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
            System.out.println(c);
            if(c=='\\'&&!flag){
                flag=true;
                continue;
            }
            switch(c){
                case '(':
                    if(flag)    res.append(c);
                    else    stk.push(c);
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
        return res.toString();
    }

    public RgToNFA  buildFromChar(char c){
        NFAState t1=new NFAState();
        t1.isStart=true;
        t1.id=++cnt;
        NFAState t2=new NFAState();
        t2.isEnd=true;
        t2.id=++cnt;
        t1.transitions.put(c,new HashSet<>());
        t1.transitions.get(c).add(t2.id);
        RgToNFA tmp=new RgToNFA();
        tmp.st=t1.id;
        tmp.states.put(t1.id,t1);
        tmp.states.put(t2.id,t2);
        return tmp;
    }


    public  void show(){
        /*
        此函数用于展示NFA
         */
        System.out.println("初态为："+ st);
        for(var k:this.states.keySet()){
            NFAState cur=this.states.get(k);
            System.out.println("当前状态为"+cur.id+":");
            for(var c:cur.transitions.keySet()){
                var stSet=cur.transitions.get(c);
                System.out.println("输入"+c+"后，转换到状态：");
                System.out.println(stSet);
            }
            if(cur.isEnd){
                System.out.println("当前状态为终态，对应的语义动作为："+this.endStates.get(cur.id).actions);
            }
        }
    }

    public void NFABuilder(){
        /*
        利用Thompson算法，扫描后缀表达式，构建NFA
         */
        //int j=0;
        Stack<RgToNFA>stk1=new Stack<>();
        for(var ety:regRules){
            Stack<RgToNFA>stk=new Stack<>();
            String p=ety.getKey();
            int lp=p.length();
            boolean flag=false;
            //System.out.println("当前正规式为："+p);
//            log.debug("当前正规式为："+p);
            for (int i=0;i<lp;i++){
                char c=p.charAt(i);
                //System.out.println("当前字符为："+c);
                if(c=='\\'&&!flag) {
                    flag=true;
                    continue;
                }
                switch (c){
                    case '.':
                        if(flag){
                            stk.push(buildFromChar(c));
                            break;
                        }
                        RgToNFA n1=stk.pop();
                        RgToNFA n2=stk.pop();
                        //n2在前，n1在后，修改n2的终结点和n1的起始节点
                        int old_st=n1.st;
                        n1.states.get(old_st).isStart=false;
                        for(var k:n2.states.keySet()){
                            NFAState t=n2.states.get(k);
                            if(t.isEnd){
                                t.isEnd=false;
                                t.transitions.put(ep,new HashSet<>());
                                t.transitions.get(ep).add(old_st);
                                n2.states.put(k,t);
                            }
                        }
                        for(var k:n1.states.keySet()){//合并
                            n2.states.put(k,n1.states.get(k));
                        }
                        stk.push(n2);
                        break;
                    case '|':
                        if(flag){
                            stk.push(buildFromChar(c));
                            break;
                        }
                        RgToNFA t1=stk.pop();
                        RgToNFA t2=stk.pop();
                        RgToNFA new_n=new RgToNFA();
                        NFAState new_st=new NFAState();
                        new_st.id=++cnt;
                        new_st.isStart=true;
                        new_st.transitions.put(ep,new HashSet<>());
                        new_n.st=new_st.id;
                        NFAState new_ed=new NFAState();
                        new_ed.id=++cnt;
                        new_ed.isEnd=true;
                        for(var k:t1.states.keySet()){
                            NFAState t=t1.states.get(k);
                            if(t.isStart){
                                t.isStart=false;
                                new_st.transitions.get(ep).add(k);
                            }
                            if(t.isEnd){
                                t.isEnd=false;
                                t.transitions.put(ep,new HashSet<>());
                                t.transitions.get(ep).add(new_ed.id);
                            }
                            new_n.states.put(k,t);
                        }
                        for(var k:t2.states.keySet()){
                            NFAState t=t2.states.get(k);
                            if(t.isStart){
                                t.isStart=false;
                                new_st.transitions.get(ep).add(k);
                            }
                            if(t.isEnd){
                                t.isEnd=false;
                                t.transitions.put(ep,new HashSet<>());
                                t.transitions.get(ep).add(new_ed.id);
                            }
                            new_n.states.put(k,t);
                        }
                        new_n.states.put(new_st.id,new_st);
                        new_n.states.put(new_ed.id,new_ed);
                        stk.push(new_n);
                        break;
                    case '*':
                        if(flag){
                            stk.push(buildFromChar(c));
                            break;
                        }
                        RgToNFA top=stk.pop();
                        RgToNFA new_top=new RgToNFA();
                        NFAState st1=new NFAState();
                        st1.isStart=true;
                        st1.id=++cnt;
                        st1.transitions.put(ep,new HashSet<>());
                        new_top.st=st1.id;
                        NFAState st2=new NFAState();
                        st2.id=++cnt;
                        st2.isEnd=true;
                        st1.transitions.get(ep).add(st2.id);
                        for(var k:top.states.keySet()){
                            NFAState t=top.states.get(k);
                            if(t.isStart){
                                t.isStart=false;
                                st1.transitions.get(ep).add(k);
                            }
                            if(t.isEnd){
                                t.isEnd=false;
                                t.transitions.put(ep,new HashSet<>());
                                t.transitions.get(ep).add(st2.id);
                                t.transitions.get(ep).add(top.st);
                            }
                            new_top.states.put(k,t);
                        }
                        new_top.states.put(st1.id,st1);
                        new_top.states.put(st2.id,st2);
                        stk.push(new_top);
                        break;
                    default://普通字符，新建一个NFA，压入栈中
                        stk.push(buildFromChar(c));
                        break;
                }
                if(flag)    flag=false;
            }
            //设置终态集
            RgToNFA top=stk.pop();
            for(var k:top.states.keySet()){
                NFAState tmp=top.states.get(k);
                if(tmp.isEnd){
                    top.endStates.put(k,new Rules(p,ety.getValue()));
                    break;
                }
            }
//            System.out.println("对应的NFA如下：");
//            top.show();
            stk1.push(top);
            //j++;
        }
        //System.out.println("共有"+j+"个NFA生成");
        //合并所有NFA
        NFAState final_st=new NFAState();
        final_st.transitions.put(ep,new HashSet<>());
        final_st.isStart=true;
        final_st.id=++cnt;
        this.st= final_st.id;
        while(!stk1.empty()){
            RgToNFA tmp=stk1.pop();
            //tmp.show();
            int cur_st=tmp.st;
            final_st.transitions.get(ep).add(cur_st);
            tmp.states.get(cur_st).isStart=false;
            for(var k:tmp.states.keySet()){
                NFAState t=tmp.states.get(k);
                if(t.isEnd){
                    this.endStates.put(k,tmp.endStates.get(k));
                }
                this.states.put(k,t);
            }
        }
        this.states.put(final_st.id,final_st);
    }


}
