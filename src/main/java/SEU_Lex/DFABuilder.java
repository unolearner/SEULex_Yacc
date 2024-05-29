package SEU_Lex;

import java.util.*;

public class DFABuilder {
    public static class DFAState{//DFA状态类
        int id;
        Set<Integer>identitySet;//一个DFA状态对应于NFA的状态子集，该set中存的是NFA的状态编号，方便比较两个DFA状态是否相同
        Map<Character,Integer>transitions;
        boolean isEnd=false;

        public DFAState(int id){
            this.id=id;
            identitySet=new HashSet<>();
            transitions=new HashMap<>();
        }
    }

    public class DFA{//DFA类
        int st=0;
        Map<Integer, RgToNFA.Rules>endStates;
        List<DFAState>states;//按顺序生成？

        public DFA(){
            endStates=new HashMap<>();
            states=new ArrayList<>();
        }

        public void show(){
            /*
            此函数用于展示对应的dfa
             */
//            for(int i=0;i<cnt;i++){
//                DFAState state=states.get(i);
//                System.out.println("当前状态为"+state.id+":");
//                System.out.println("该状态由NFA的这些状态组成：");
//                System.out.println(state.identitySet);
//                for(var c:state.transitions.keySet()){
//                    int next=state.transitions.get(c);
//                    System.out.println("输入"+c+"后，转到状态"+next);
//                }
//                if(state.isEnd){
//                    System.out.println("当前状态为终态，语义动作为："+endStates.get(state.id).actions);
//                }
//            }
            // Generate Graphviz code
            StringBuilder graphviz = new StringBuilder();
            graphviz.append("digraph DFA {\n");
            graphviz.append("rankdir=TB;\n");  // Top to Bottom layout

            // Add nodes
            for (DFAState state : this.states) {
                if (state.isEnd) {
                    graphviz.append(String.format("  %d [label=\"%d\", shape=doublecircle, color=red];\n", state.id, state.id));
                } else {
                    graphviz.append(String.format("  %d [label=\"%d\", shape=circle, color=blue];\n", state.id, state.id));
                }
            }

            // Add edges
            for (DFAState state : this.states) {
                for (Map.Entry<Character, Integer> entry : state.transitions.entrySet()) {
                    char c = entry.getKey();
                    int targetState = entry.getValue();
                    graphviz.append(String.format("  %d -> %d [label=\"%s\"];\n", state.id, targetState, c));
                }
            }

            graphviz.append("}\n");

            // Output the Graphviz code
            System.out.println(graphviz.toString());
        }

    }

    int cnt=0;
    char ep='\u0000';
    int ch_st=9,ch_ed=126;
    RgToNFA nfa;
    DFA final_dfa=new DFA();
    Map<Integer,Set<Integer> >epClosures=new HashMap<>();//将每个状态ID映射到其epsilon闭包，方便查询，避免频繁的求同一个状态的epsilon闭包
    Map<Set<Integer>,Integer> vis=new HashMap<>();//标记集合，将集合映射到对应的DFA状态

    public Set<Integer> getEpClosure(int id){
        /*
        深度优先搜索寻找某一状态的epsilon闭包
         */
        RgToNFA.NFAState tmp=nfa.states.get(id);
        Set<Integer>res=new HashSet<>();
        res.add(id);//首先自己在epsilon闭包里
        if (tmp.transitions.containsKey(ep)) {
            Set<Integer>ne=tmp.transitions.get(ep);//找到邻居的集合
            for(var next:ne){//遍历邻居
                res.add(next);
                res.addAll(getEpClosure(next));//将通过ep可以访问到的都加进来
            }
        }
        return res;
    }


    public DFABuilder(RgToNFA nfa){
        /*
        采用epsilon闭包法进行NFA的确定化
        nfa为待确定化的非确定自动机

        找到DFA的初态，即NFA初态的epsilon闭包
        ——>设置一个队列，队列中存放未处理过的DFA状态，队列为空时表明完成转换
        ——>每次取出队头，对队头状态的每一个NFA状态，尝试输入字符集中的每一个字符，看能到达哪些状态（这里需要求epsilon闭包），放入一个新的集合s中
        ——>判断当前集合s是否访问过（通过将每个产生的集合映射到一个整数，也就是DFA的状态id），未访问过则产生新的DFA状态，放入队列(还需判断当前新的状态是否为终态，若是则需要决定终态对应的动作）
                         而如果访问过，则可能需要添加新边
         */
        this.nfa=nfa;
        //NFA初态的epsilon闭包为DFA的初态
        int st=nfa.st;
        DFAState start= new DFAState(cnt++);
        start.identitySet=getEpClosure(st);
        epClosures.put(start.id, start.identitySet);
        vis.put(start.identitySet,start.id);
        final_dfa.st=start.id;
        final_dfa.states.add(start);

        Queue<DFAState>queue = new LinkedList<>();
        queue.offer(start);
        while(!queue.isEmpty()){
            DFAState front=queue.poll();
            int cur=front.id;
            Set<Integer>tmp=front.identitySet;
            for(char i=(char)ch_st;i<=(char)ch_ed;i++){//遍历字符全集
                Set<Integer>res=new HashSet<>();
                for(var nfa_st:tmp){//遍历当前状态的NFA状态集中的每个状态
                    if (nfa.states.get(nfa_st).transitions.containsKey(i)) {
                        Set<Integer>reachable=nfa.states.get(nfa_st).transitions.get(i);
                        for(var ne:reachable){
                            if(!epClosures.containsKey(ne))  epClosures.put(ne,getEpClosure(ne));
                            res.addAll(epClosures.get(ne));
                        }
                    }
                }
                if(res.isEmpty())   continue;
                if(!vis.containsKey(res)){//如果该集合是一个新的集合
                    DFAState new_st= new DFAState(cnt++);
                    new_st.identitySet=res;
                    vis.put(res,new_st.id);//标记访问
                    final_dfa.states.add(new_st);
                    //判断当前的新状态是否为终态
                    int edId=10000;//记录最小的终态标号，因为实际状态只有七千多个，所以此处可以将edId初始化为10000
                    for(var state_id:new_st.identitySet){
                        RgToNFA.NFAState temp=nfa.states.get(state_id);
                        if(temp.isEnd&&temp.id<edId)    edId=temp.id;
                    }
                    if(edId<10000){//是终态，标记语义动作，即具有最小标号的NFA终态
                        new_st.isEnd=true;
                        final_dfa.endStates.put(new_st.id,nfa.endStates.get(edId));
                    }
                    //添加边
                    final_dfa.states.get(cur).transitions.put(i, new_st.id);
                    queue.offer(new_st);//加入队列，待处理
                }else{//否则是一个出现过的集合，只需添加边
                    final_dfa.states.get(cur).transitions.put(i, vis.get(res));
                }
            }
        }
    }
}
