package SEU_Lex;

public class Main {
        public static void main(String []args) throws IOException {
        LFileParser yyl = new LFileParser();
        yyl.initAll("littlec.l");
        /*输出测试*/
        /*
        System.out.println(yyl.defineRules);
        System.out.println(yyl.program1);
        System.out.println(yyl.program2);
        System.out.println(yyl.regexRules);
        */
    }
}
