package SEU_Lex;

import java.util.Map;

public class RegProcess {
    /*
    该类用于处理正规表达式，
    消除复杂语法，转换成只有()|*的简单正规式，
    便于利用Thompson算法将rg变成NFA

    字符全集：\t-~
     */
    public static Map<String,String> regs;
    public static void replaceBraces(Map<String,String> reg){
        /*
        将分层定义的内容直接替换
        {D}——>[0-9]
         */
        for(String key:reg.keySet()){
            String val= reg.get(key);
            for(String k:reg.keySet()){
                String tmp='{'+k+'}';
                if(val.contains(tmp))   val=val.replace(tmp,reg.get(k));
            }
            reg.put(key,val);
        }
    }

    public static String  replaceBraces(String val){
        /*
        将分层定义的内容直接替换
        {D}——>[0-9]
         */
        for(String k:regs.keySet()){
            String tmp='{'+k+'}';
            if(val.contains(tmp))   val=val.replace(tmp,regs.get(k));
        }
        return val;
    }

    public static String turnBracketToOr(String src){
        /*
        将中括号中的字符用|连接
        [xX]——>x|X
         */
        return "";
    }

    public static String quoteErase(String src){
        /*
        将一些用引号包裹的正规式或关键字提取出来
        "auto"——>auto
         */

        return "";
    }

    public static String spChProcess(String src){
        /*
        处理特殊字符：
        ?——>0个或一个前面的
        +——>一个前面的，然后*
        .——>字符全集
        [^]——>字符全集-^后面的所有字符
         */

        return "";
    }
}
