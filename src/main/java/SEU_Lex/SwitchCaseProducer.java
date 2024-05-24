package SEU_Lex;

import java.util.Map;
import java.util.Set;

public class SwitchCaseProducer {
    /*
    此类用于由DFA生成Switch-case语句
     */
    public static String switchBuilder(String var, Map<String, Set<Integer> >case_list,String default_case){
        /*
        此函数接收一个Map型的case_list，键String为对应值Set时的动作
        var:控制部分的变量名（Switch c，则c就是var）
        default_case:默认情况下的代码部分
         */
        String bg="switch("+var+") {\n";
        String ed="default: { "+default_case+" } }\n";//第一个}，default的，第二个}，整个Switch的
        String core="";
        for(var ety:case_list.entrySet()){//遍历case_list
            String code=ety.getKey();
            Set<Integer>cases=ety.getValue();
            String tmp="";
            for(var c:cases){
                tmp+="case "+c+": ";
            }
            core+=tmp+" { "+code+" break; "+"}\n";
        }

        return bg+core+ed;
    }
}
