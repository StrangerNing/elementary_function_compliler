import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Tsening Chu
 * @version 1.0
 * @date 2018/12/18
 */
public class LexicalAnalyzer {

    private String[] func = {"sin","cos","tg","ctg","^","log","lg","ln"};
    private String[] compart = {" ","\t","\r","(",")",";"};
    private String[] operation = {"+","*","/","="};
    private List<String> num = new ArrayList<>();
    private List<String> vari = new ArrayList<>();
    private List<Integer> token = new ArrayList<>();
    private Integer restart = 0;
    /**
     * 字符缓冲区
     */
    private List<Character> ch = new ArrayList<>();

    private Boolean isChar(Character c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    private Boolean isNumber(Character c) {
        return c >= '0' && c <= '9';
    }

    private Boolean isPositiveNum(Character c) {
        return c >= '1' && c <= '9';
    }

    private Boolean isFunc(String c){
        for (String fun : func){
            if (fun.equals(c)){
                return true;
            }
        }
        return false;
    }

    private Boolean isUnderline(Character c){
        return c.equals('_');
    }

    private Boolean isOperation(Character c){
        for (String op : operation){
            if (op.equals(c.toString())){
                return true;
            }
        }
        return false;
    }

    private Boolean isCompart(Character c){
        for (String comp : compart){
            if (comp.equals(c.toString())){
                return true;
            }
        }
        return false;
    }

    private Integer move(Integer state,Character c){
        Boolean isChar = isChar(c);
        Boolean isNum = isNumber(c);
        Boolean isPosNum = isPositiveNum(c);
        Boolean isUnderline = isUnderline(c);
        Boolean isOperate = isOperation(c);
        Boolean isCompart = isCompart(c);

        restart = 0;

        if (state ==1) {
            if (c.equals('-')) {
                state = 2;
                System.out.println(c + "\t运算符<9>");
                token.add(9);
                return state;
            }

            if (isOperate || isCompart) {
                ch.add(c);
                for (int i = 0; i < operation.length; i++) {
                    if (c.toString().equals(operation[i])) {
                        System.out.println(c + "\t运算符<" + (i + 10) + ">");
                        token.add(i + 10);
                        ch.clear();
                        return 1;
                    }
                }
                for (int i = 0; i < compart.length; i++) {
                    if (c.toString().equals(compart[i])) {
                        System.out.println(c + "\t分隔符<" + (i + 14) + ">");
                        token.add(i + 14);
                        ch.clear();
                        return 1;
                    }
                }
            }

            if (c.equals('?')){
                System.out.println(c+"\t运算符<22>");
                token.add(22);
                return 1;
            }

            if (c.equals('^')){
                System.out.println(c+"\t函数<05>");
                token.add(5);
                return 1;
            }

            if (isPosNum){
                ch.add(c);
                return 3;
            }

            if (c.equals('0')){
                ch.add(c);
                return 4;
            }

            if (c.equals('E')){
                ch.add(c);
                return 5;
            }

            if (isChar || isUnderline){
                ch.add(c);
                return 8;
            }
        }

        if (state ==2){
            if (isPosNum){
                ch.add(c);
                return 3;
            }

            if (c.equals('0')){
                ch.add(c);
                return 4;
            }
        }

        if (state == 3 || state == 4){
            if (isNum){
                ch.add(c);
                return 3;
            }

            if (c.equals('.')){
                ch.add(c);
                return 6;
            }
        }

        if (state == 6){
            if (isPosNum) {
                ch.add(c);
                return 7;
            }

            if (c.equals('0')){
                ch.add(c);
                return 6;
            }
        }

        if (state == 7){
            if (c.equals('0')){
                ch.add(c);
                return 6;
            }
            if (isPosNum){
                ch.add(c);
                return 7;
            }
        }

        if (state == 3 || state == 4 || state == 5 || state == 7){
            StringBuilder cacheCh = new StringBuilder();
            for (Character cha : ch){
                cacheCh.append(cha);
            }
            String cache = cacheCh.toString();
            int p = 0;
            for (String str : num){
                if (cache.equals(str)){
                    p=1;
                    break;
                }
            }
            if (p == 1){
                System.out.println(cache+"\t常量<20>");
            }else {
                num.add(cache);
                System.out.println(cache+"\t常量<20>");
            }
            token.add(20);
            restart = 1;
            ch.clear();
            return 1;
        }

        if (state == 8){
            if (isChar || isNum || isUnderline){
                ch.add(c);
                return 8;
            }

            StringBuilder cacheCh = new StringBuilder();
            for (Character cha :ch){
                cacheCh.append(cha);
            }
            String cache = cacheCh.toString();
            if ("PI".equals(cache)){
                System.out.println(cache+"\t常量<20>");
                token.add(20);
                ch.clear();
                restart = 1;
                return 1;
            }

            int i,p = 0;
            for (i = 0;i<func.length;i++){
                if (cache.equals(func[i])){
                    p = 1;
                    break;
                }
            }
            if (p == 1){
                System.out.println(cache+"\t函数<"+(i+1)+">");
                token.add(i+1);
            }else {
                for (String variable : vari){
                    if (variable.equals(cache)){
                        p = 1;
                        break;
                    }
                }
                if (p == 1){
                    System.out.println(cache+"\t变量<21>");
                }else {
                    vari.add(cache);
                    System.out.println(cache+"\t变量<21>");
                }
                token.add(21);
            }
            restart = 1;
            ch.clear();
            return 1;
        }
        System.out.println("error");
        return 1;
    }

    public static List<Integer> analyzer(String filename){
        try {
            LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer();
            Integer state = 1;
            File file = new File(filename);
            InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
            BufferedReader br = new BufferedReader(reader);
            while (true){
                Character ch = (char)br.read();
                if (ch == '\n'){
                    ch = (char)br.read();
                }else if ((int)ch == 65535){
                    break;
                }
                state = lexicalAnalyzer.move(state,ch);
                if (lexicalAnalyzer.restart == 1){
                    state = lexicalAnalyzer.move(state,ch);
                }
            }
            br.close();
            return lexicalAnalyzer.token;
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
