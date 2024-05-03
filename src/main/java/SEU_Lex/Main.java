package SEU_Lex;

import java.io.IOException;

public class Main {
        public static void main(String []args) throws IOException {
        LFileParser yyl = new LFileParser();
//        ClassLoader classLoader = Main.class.getClassLoader();
//        URL resource = classLoader.getResource("resources/littlec.l");
//        String filePath = resource.getPath();
        yyl.initAll("littlec.l");
        /*输出测试*/

//        System.out.println(yyl.defineRules);
//        System.out.println("test");
//        System.out.println(yyl.program1);
//        System.out.println("test");
//        System.out.println(yyl.program2);
//        System.out.println("test");
//        System.out.println(yyl.regexRules);

    }
}
