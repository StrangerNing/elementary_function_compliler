import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

/**
 * @author Tsening Chu
 * @version 1.0
 * @date 2018/12/18
 */
public class GrammaticalAnalyzer {

    private Boolean readNext = true;
    private Stack<Integer> status = new Stack<>();
    private Stack<String> grammar = new Stack<>();
    private Stack<String> string = new Stack<>();
    private static final String VARIABLE_PARAMETER = "vari";
    private static final String CONSTANT_PARAMETER = "cons";
    private static final String FUNCTION_PARAMETER = "func";

    private List<String> translate(List<Integer> token) {
        List<String> expression = new ArrayList<>();
        for (Integer toke : token) {
            if (toke > 0 && toke < 9) {
                if (toke == 5) {
                    expression.add("^");
                } else {
                    expression.add(FUNCTION_PARAMETER);
                }
            } else {
                switch (toke) {
                    case 9:
                        expression.add("-");
                        continue;
                    case 10:
                        expression.add("+");
                        continue;
                    case 11:
                        expression.add("*");
                        continue;
                    case 12:
                        expression.add("/");
                        continue;
                    case 13:
                        expression.add("=");
                        continue;
                    case 17:
                        expression.add("(");
                        continue;
                    case 18:
                        expression.add(")");
                        continue;
                    case 19:
                        expression.add(";");
                        continue;
                    case 20:
                        expression.add(",");
                        continue;
                    case 21:
                        expression.add(CONSTANT_PARAMETER);
                        continue;
                    case 22:
                        expression.add(VARIABLE_PARAMETER);
                        continue;
                    case 23:
                        expression.add("?");
                        continue;
                    default:
                }
            }
        }
        return expression;
    }

    private void showSLR(Integer number) {
        System.out.format("%-5s", number);
        System.out.print(" ");
        StringBuilder str = new StringBuilder();
        for (Integer x : status) {
            str.append(x);
            str.append(" ");
        }
        System.out.format("%-30s", str.toString());
        System.out.print(" ");
        str.delete(0, str.length());
        for (String x : grammar) {
            str.append(x);
        }
        System.out.format("%-40s", str.toString());
        System.out.print(" ");
        str.delete(0, str.length());
        for (String x : string) {
            str.append(x);
        }
        System.out.format("%-40s", str.toString());
        System.out.print(" ");
    }

    private Boolean move(List<String> expression) {
        Stack<Integer> status = new Stack<>();
        status.push(0);
        Stack<String> grammar = new Stack<>();
        grammar.push("#");
        Stack<String> string = new Stack<>();
        string.push("#");
        Integer index = 0;

        for (int i = expression.size(); i > 0; i--) {
            string.push(expression.get(i - 1));
        }
        System.out.format("%-5s %-30s %-40s %-40s\n", "步骤", "状态栈", "符号栈", "输入串");
        showSLR(index);
        System.out.println("移进");
        String ch = "";
        while ("#".equals(string.peek())) {
            if (readNext) {
                ch = string.pop();
            } else {
                readNext = true;
            }
            switch (status.peek()) {
                case 0:
                    if (VARIABLE_PARAMETER.equals(ch)) {
                        shiftIn(index, 3, ch);
                    }
                    continue;
                case 1:
                    if ("?".equals(ch)) {
                        shiftIn(index, 4, ch);
                    } else if (VARIABLE_PARAMETER.equals(ch)) {
                        showSLR(index);
                        grammar.pop();
                        status.pop();
                        status.push(3);
                        grammar.push(ch);
                        System.out.println("移进");
                    }
                    continue;
                case 2:
                    if (VARIABLE_PARAMETER.equals(ch) || "?".equals(ch) || "#".equals(ch)) {
                        Integer[] reducePushStatus = {0, 1, 2, 5};
                        reduce(index, 1, "S", reducePushStatus);
                        continue;
                    }
                case 3:
                    if ("=".equals(ch)) {
                        shiftIn(index, 6, ch);
                        continue;
                    }
                case 4:
                case 6:
                case 13:
                case 18:
                case 19:
                case 20:
                case 21:
                    switch (ch) {
                        case VARIABLE_PARAMETER:
                            shiftIn(index, 15, ch);
                            continue;
                        case "-":
                            shiftIn(index, 14, ch);
                            continue;
                        case FUNCTION_PARAMETER:
                            shiftIn(index, 12, ch);
                            continue;
                        case "(":
                            shiftIn(index, 13, ch);
                            continue;
                        case CONSTANT_PARAMETER:
                            shiftIn(index, 16, ch);
                            continue;
                        default:
                            continue;
                    }
                case 5:
                    if ("?".equals(ch) || "#".equals(ch)) {
                        Integer[] reducePushStatus = {0, 1, 2, 5};
                        reduce(index, 1, "S", reducePushStatus);
                        continue;
                    }
                case 7:
                    if (VARIABLE_PARAMETER.equals(ch)) {
                        status.pop();
                        grammar.pop();
                        grammar.push("S");
                        showSLR(index);
                        System.out.println("acc");
                        continue;
                    }
                case 8:
                    if (VARIABLE_PARAMETER.equals(ch) || "?".equals(ch) || ")".equals(ch) || "#".equals(ch) || ";".equals(ch)) {
                        Integer[] reducePushStatus = {4, 7, 6, 17, 13, 24};
                        reduce(index, 1, "B", reducePushStatus);
                    } else if ("+".equals(ch)) {
                        shiftIn(index, 18, ch);
                    } else if ("-".equals(ch)) {
                        shiftIn(index, 19, ch);
                    }
                    continue;
                case 9:
                    if (VARIABLE_PARAMETER.equals(ch) || "?".equals(ch) || "+".equals(ch) || "-".equals(ch) || ")".equals(ch) || "#".equals(ch) || ";".equals(ch)) {
                        Integer[] reducePushStatus = {4, 8, 6, 8, 13, 8};
                        reduce(index, 1, "C", reducePushStatus);
                    } else if ("*".equals(ch)) {
                        shiftIn(index, 20, ch);
                    } else if ("/".equals(ch)) {
                        shiftIn(index, 21, ch);
                    }
                    continue;
                case 10:
                    if (VARIABLE_PARAMETER.equals(ch) || "?".equals(ch) || "+".equals(ch) || "-".equals(ch) || "*".equals(ch) || "/".equals(ch) || ")".equals(ch) || "#".equals(ch)) {
                        Integer[] reducePushStatus = {4, 9, 6, 9, 13, 9, 18, 27, 19, 28};
                        reduce(index, 1, "T", reducePushStatus);
                        continue;
                    }
                case 11:
                    if (VARIABLE_PARAMETER.equals(ch) || "?".equals(ch) || "+".equals(ch) || "-".equals(ch) || "*".equals(ch) || "/".equals(ch) || ")".equals(ch) || "#".equals(ch) || ",".equals(ch) || ";".equals(ch)) {
                        Integer[] reducePushStatus = {4, 10, 6, 10, 13, 10, 18, 10, 19, 10, 20, 29, 21, 30};
                        reduce(index, 1, "F", reducePushStatus);
                    } else if ("^".equals(ch)) {
                        shiftIn(index, 22, ch);
                    }
                    continue;
                case 12:
                    if ("(".equals(ch)) {
                        shiftIn(index, 23, ch);
                    }
                    continue;
                case 14:
                    if ("(".equals(ch)) {
                        shiftIn(index, 25, ch);
                    }
                    continue;
                case 15:
                    if (VARIABLE_PARAMETER.equals(ch) || "?".equals(ch) || "+".equals(ch) || "-".equals(ch) || "*".equals(ch) || "/".equals(ch) || ")".equals(ch) || "#".equals(ch) || ",".equals(ch) || "^".equals(ch)) {
                        Integer[] reducePushStatus = {4, 11, 6, 11, 13, 11, 18, 11, 19, 11, 20, 11, 21, 11, 22, 31, 23, 33, 25, 34};
                        reduce(index, 1, "E", reducePushStatus);
                    }
                    continue;
                case 16:
                    if (VARIABLE_PARAMETER.equals(ch) || "?".equals(ch) || "+".equals(ch) || "-".equals(ch) || "*".equals(ch) || "/".equals(ch) || ")".equals(ch) || "#".equals(ch) || ",".equals(ch) || "^".equals(ch) || ";".equals(ch)) {
                        Integer[] reducePushStatus = {4, 11, 6, 11, 13, 11, 18, 11, 19, 11, 20, 11, 21, 11, 22, 31, 23, 33, 25, 34};
                        reduce(index, 1, "E", reducePushStatus);
                    }
                    continue;
                case 17:
                    if (";".equals(ch)) {
                        shiftIn(index, 26, ch);
                    }
                    continue;
                case 22:
                case 23:
                case 25:
                    switch (ch) {
                        case VARIABLE_PARAMETER:
                            shiftIn(index, 15, ch);
                            continue;
                        case "-":
                            shiftIn(index, 14, ch);
                            continue;
                        case "(":
                            shiftIn(index, 13, ch);
                            continue;
                        case CONSTANT_PARAMETER:
                            shiftIn(index, 16, ch);
                            continue;
                        default:
                            continue;
                    }
                case 24:
                    if (")".equals(ch)){
                        shiftIn(index,33,ch);
                    }
                    continue;
                case 26:
                    if (VARIABLE_PARAMETER.equals(ch) || "?".equals(ch) || "#".equals(ch) || ";".equals(ch)){
                        Integer[] reducePushStatus = {0,2};
                        reduce(index,4,"A",reducePushStatus);
                    }
                    continue;
                case 27:
                case 28:
                    if (VARIABLE_PARAMETER.equals(ch) || "?".equals(ch) || "+".equals(ch) || "-".equals(ch) || ")".equals(ch) || "#".equals(ch)){
                        Integer[] reducePushStatus = {4,8,6,8,13,8};
                        reduce(index,3,"C",reducePushStatus);
                    }else if ("*".equals(ch)){
                        shiftIn(index,20,ch);
                    }else if ("/".equals(ch)){
                        shiftIn(index,21,ch);
                    }
                    continue;
                default:

            }
        }
        return true;

    }

    private void shiftIn(Integer index, Integer pushStatus, String pushGrammar) {
        showSLR(index);
        System.out.println("移进");
        status.push(pushStatus);
        grammar.push(pushGrammar);
    }

    private void reduce(Integer index, Integer reduceCount, String pushGrammar, Integer[] pushStatus) {
        showSLR(index);
        System.out.println("规约");
        for (int i = 0; i < reduceCount; i++) {
            status.pop();
            grammar.pop();
        }
        grammar.push(pushGrammar);
        reducePushStatus(pushStatus);
    }

    private void reducePushStatus(Integer[] pushStatus) {
        if (pushStatus.length % 2 != 0) {
            throw new RuntimeException("规约键值对出错！");
        } else {
            for (int i = 0; i < pushStatus.length; i++) {
                if (status.peek().equals(pushStatus[i++])) {
                    readNext = false;
                    status.push(pushStatus[i]);
                }
            }
        }
    }

    public static void main(String[] args) {
        GrammaticalAnalyzer grammaticalAnalyzer = new GrammaticalAnalyzer();
        List<Integer> token = LexicalAnalyzer.analyzer("D:/test.txt");
        List<String> expression = grammaticalAnalyzer.translate(token);
        for (String x : expression) {
            System.out.print(x);
        }
        System.out.println();
        grammaticalAnalyzer.move(expression);

    }
}
