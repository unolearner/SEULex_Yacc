package SEU_Lex;

import java.util.Map;
import java.util.Stack;


public class RegProcess {
    /*
    该类用于处理正规表达式，
    消除复杂语法，转换成只有()|*的简单正规式，
    便于利用Thompson算法将rg变成NFA
    字符全集：\t-~
     */
    public static Map<String,String> regs;
    public static int ch_st=9,ch_ed=126;
    public static String op="(|)?*.-^+[]\\";//正则表达式中出现的运算符，{m,n}这种
    public static String epsilon="\u0000";

    public static String turnToReal(String src){
        /*
        将\n,\t,\r等变成其真正表示的字符，使两个字符变成一个
         */
        int srcLen = src.length();
        String ans = "";
        for (int i = 0; i < srcLen; ++i) {
            char c = src.charAt(i);
            if (c == '\\' && i + 1 < srcLen) {
                ++i;
                c = src.charAt(i);
                switch (c) {
                    case 'p':
                        c = ' ';
                        break;
                    case 'r':
                        c = '\r';
                        break;
                    case 'n':
                        c = '\n';
                        break;
                    case 't':
                        c = '\t';
                        break;
                    case 'v':
                        c = '\u000b';
                        break;
                    case 'f':
                        c = '\f';
                        break;
                    default:
                        ans += '\\';
                        break;
                }
                ans += c;
                continue;
            }
            ans += c;
        }
        return ans;
    }

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
        [^]——>字符全集-^后面的
        注意处理未转义的-
         */
        int l=src.length(),rev=0,st=-1,ed=-1;
        String tmp="",src_cp=src;
        boolean flag=false;
        for(int i=0;i<l;i++){
            char c=src.charAt(i);
            if(c=='\\'&&!flag){
                flag=true;
                continue;
            }
            if(c=='['){
//                if((i-1>=0&&src.charAt(i-1)=='\\'))  continue;
                if(flag)    continue;
                st=i;
                if(src.charAt(i+1)=='^'){
                    rev=1;
                    i++;
                }
            }
            else if(c==']') {
//                if((i-1>=0&&src.charAt(i-1)=='\\'))  continue;
                if(flag)    continue;
                ed=i;
                if(rev==1){
                    String s="";
                    for(int j=ch_st;j<=ch_ed;j++){
                        char ch=(char)j;
                        if(!tmp.contains(String.valueOf(ch))) s+=ch;
                    }
                    tmp=s;
                }
                String ori=src.substring(st,ed+1);
                int lt=tmp.length();
                String t=tmp;
                StringBuilder res= new StringBuilder("");
                for(int j=0;j<lt-1;j++){
                    char ch=tmp.charAt(j);
                    if(op.contains(String.valueOf(ch))) {
                        //t=t.replace(String.valueOf(ch),"\\"+ch+"|");
                        res.append("\\"+ch+'|');
                    }
                    else    res.append(ch+"|");
                        //t=t.replace(String.valueOf(ch),ch+"|");
                    //t=t.replace(String.valueOf(ch),ch+"|");
                }
                if(op.contains(String.valueOf(tmp.charAt(lt-1))) )  res.append("\\"+tmp.charAt(lt-1));
                else res.append(tmp.charAt(lt-1)+"");
                tmp="";
                t=res.toString();
                src_cp=src_cp.replace(ori,'('+t+')');
                st=ed=-1;
                if(rev==1)  rev=0;
            }
            else{
                if(c=='-'&&!flag&&(i+1<l&&src.charAt(i+1)!=']'&&src.charAt(i+1)>src.charAt(i-1))){
                    char pre=src.charAt(i-1);
                    ++i;
                    c=src.charAt(i);
                    for(char j = (char) (pre+1); j<=c; j++){
                        tmp+=j;
                    }
                }
                else if(st!=-1) tmp+=c;//在中括号内的正常字符，直接加进去
            }
            if(flag)    flag=false;
        }
        return src_cp;
    }

    public static String quoteErase(String src){
        /*
        将一些用引号包裹的正规式或关键字提取出来并在对其中的特殊字符作转义处理
        "auto"——>auto
         */
        int l=src.length();
        String str="";
        for(int i=1;i<l-1;i++){
            char ch=src.charAt(i);
            if(op.contains(String.valueOf(ch))){
                str+='\\';
                str+=ch;
            }else{
                str+=ch;
            }
        }
        return str;
    }

    public static String spChProcess(String src){
        /*
        处理特殊字符：
        ?——>0个或一个前面的
        +——>一个前面的，然后*，注意表达式中有[+-]，如何处理这里的+呢（即怎么区分运算符+和字符+）
        .——>字符全集,[\t-~]，字符全集中遇到特殊字符也得加转义
        littlec.l中似乎没有对单个字符的+，由此不考虑这种情况（因而将运算符+和字符+区分开来）
        如何将整块替换？记录的左右括号位置是否还有效?
         */
        String src_copy=src;
        int l=src.length(),rPar=-1;
        int lp = 0,pos=0;
        int []offset=new int[l];
        Stack<Integer>lPar = new Stack<>();
        StringBuilder res= new StringBuilder(src);
        boolean flag=false;
        for(int i=0;i<l;i++){
            char ch=src.charAt(i);
            if(ch=='\\'&&!flag){
                flag=true;
                continue;
            }
            switch(ch){
                case '?':
//                    if(i-1>=0&&src.charAt(i-1)=='\\')   continue;
                    if(flag)    break;
                    if(rPar==i-1){
//                        if(lp>pos)  lp+=offset;
//                        if(rPar>pos)    rPar+=offset;
                        int tm=lp;
                        for(int j=0;j<tm;j++)   lp+=offset[j];
                        tm=rPar;
                        for(int j=0;j<tm;j++) rPar+=offset[j];
                        String tmp=res.substring(lp,rPar+1);
                        //src_copy=src_copy.replace(tmp+'?','('+epsilon+'|'+tmp+')');
                        String to_replace='('+epsilon+'|'+tmp+')';
                        res.replace(lp,rPar+2,to_replace);
                        offset[i]=to_replace.length()-tmp.length()-1;
                    }else{
                        char c=src.charAt(i-1);
                        //String to_replace="("+epsilon+'|'+c+')';
                        //src_copy=src_copy.replace(String.valueOf(c)+'?',to_replace);
//                        if(i>pos)   res.replace(i-1+offset,i+offset,"("+epsilon+'|'+c+')');
//                        else    res.replace(i-1,i,"("+epsilon+'|'+c+')');
                        int tmp=0;
                        for(int j=0;j<i;j++)    tmp+=offset[j];
                        res.replace(i-1+tmp,i+tmp,"("+epsilon+'|'+c+')');
                        offset[i]=3;
                        pos=i;
                    }
                    break;
                case '+':
//                    if(i-1>=0&&src.charAt(i-1)=='\\')   continue;
                    if(flag)    break;
                    if(rPar==i-1){
//                        if(lp>pos)  lp+=offset;
//                        if(rPar>pos)    rPar+=offset;
                        int tm=lp;
                        for(int j=0;j<tm;j++)   lp+=offset[j];
                        tm=rPar;
                        for(int j=0;j<tm;j++) rPar+=offset[j];
                        String tmp=res.substring(lp,rPar+1);
                        //src_copy=src_copy.replace(tmp+'+',tmp+tmp+'*');
                        String to_replace=tmp+tmp+'*';
                        res.replace(lp,rPar+2,to_replace);
                        offset[i]=to_replace.length()-tmp.length()-1;
                    }
                    break;
                case '.':
//                    if(i-1>=0&&src.charAt(i-1)=='\\')   continue;
                    if(flag)    break;
                    //src_copy=src_copy.replace(String.valueOf('.'),"["+(char)ch_st+"-"+(char)ch_ed+"]");
//                    if(i>pos)   res.replace(i+offset,i+1+offset,"["+(char)ch_st+"-"+(char)ch_ed+"]");
//                    else res.replace(i,i+1,"["+(char)ch_st+"-"+(char)ch_ed+"]");
                    int tmp=0;
                    for(int j=0;j<i;j++)    tmp+=offset[j];
                    res.replace(i+tmp,i+1+tmp,"["+(char)ch_st+"-"+(char)ch_ed+"]");
                    offset[i]=4;
                    break;
                case '(':
                case '[':
//                    if(i-1>=0&&src.charAt(i-1)=='\\')   continue;
                    if(flag)    break;
                    lPar.push(i);
                    break;
                case ')':
                case ']':
//                    if(i-1>=0&&src.charAt(i-1)=='\\')   continue;
                    if(flag)    break;
                    rPar=i;
                    lp=lPar.pop();
                    break;
                default:
                    //res.append(ch);
                    break;
            }
            if(flag)    flag=false;
        }
        return res.toString();
//        return src_copy;
    }

    public static String setDots(String src){
        /*
        为简化后的正则表达式加.，.在Thompson算法中表示连接符，用于连接两个子表达式
        不加点的情况：
        - 正规表达式的最后一个字符
        - 当前字符为非转义的（和|
        - 当前字符的后一个字符为|、*、）
         */
        StringBuilder res= new StringBuilder();
        int l=src.length();
        for(int i=0;i<l;i++){
            char c=src.charAt(i);
            res.append(c);
            if(i==l-1)  continue;
            if((c=='('||c=='|'||c=='\\')&&(i-1<0||src.charAt(i-1)!='\\'))  continue;
            char pos=src.charAt(i+1);
            if(pos=='|'||pos=='*'||pos==')')    continue;
            res.append('.');
        }
        return res.toString();
    }
    public static String totalProcess(String src){
        if(src.charAt(0)=='\"')   src=RegProcess.quoteErase(src);
        src=RegProcess.turnToReal(src);
        src=RegProcess.replaceBraces(src);
        src=RegProcess.spChProcess(src);
        src=RegProcess.turnBracketToOr(src);
        src=RegProcess.setDots(src);
        return src;
    }
}
