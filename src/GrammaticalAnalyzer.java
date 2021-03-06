import java.util.ArrayList;
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
    private static final String VARIABLE_PARAMETER = "i";
    private static final String CONSTANT_PARAMETER = "n";
    private static final String FUNCTION_PARAMETER = "f";
    private Integer index = 0;

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

    private void showSLR() {
        System.out.format("%-5s", index++);
        System.out.print(" ");
        StringBuilder str = new StringBuilder();
        for (Integer x : status) {
            str.append(x);
            str.append(" ");
        }
        System.out.format("%-50s", str.toString());
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
        status.push(0);
        grammar.push("#");
        string.push("#");

        for (int i = expression.size(); i > 0; i--) {
            string.push(expression.get(i - 1));
        }
        System.out.format("%-5s %-50s %-40s %-40s\n", "步骤", "状态栈", "符号栈", "输入串");
        showSLR();
        System.out.println("移进");

        String ch = "";
        while (!"s".equals(grammar.peek()) && !string.empty()) {
            if (readNext) {
                ch = string.pop();
            } else {
                readNext = true;
            }
            switch (status.peek()) {
                case 0:
                    if (VARIABLE_PARAMETER.equals(ch)) {
                        shiftIn(3, ch);
                    }else {
                        return false;
                    }
                    continue;
                case 1:
                    if ("?".equals(ch)) {
                        shiftIn(4, ch);
                    } else if (VARIABLE_PARAMETER.equals(ch)) {
                        showSLR();
                        grammar.pop();
                        status.pop();
                        status.push(3);
                        grammar.push(ch);
                        System.out.println("移进");
                    }else {
                        return false;
                    }
                    continue;
                case 2:
                    if (VARIABLE_PARAMETER.equals(ch) || "?".equals(ch) || "#".equals(ch)) {
                        Integer[] reducePushStatus = {0, 1, 2, 5};
                        reduce(1, "S", reducePushStatus);
                    }else {
                        return false;
                    }
                    continue;
                case 3:
                    if ("=".equals(ch)) {
                        shiftIn(6, ch);
                    }else {
                        return false;
                    }
                    continue;
                case 4:
                case 6:
                case 13:
                case 18:
                case 19:
                case 20:
                case 21:
                    switch (ch) {
                        case VARIABLE_PARAMETER:
                            shiftIn(15, ch);
                            continue;
                        case "-":
                            shiftIn(14, ch);
                            continue;
                        case FUNCTION_PARAMETER:
                            shiftIn(12, ch);
                            continue;
                        case "(":
                            shiftIn(13, ch);
                            continue;
                        case CONSTANT_PARAMETER:
                            shiftIn(16, ch);
                            continue;
                        default:
                            return false;
                    }
                case 5:
                    if ("?".equals(ch) || "#".equals(ch)) {
                        Integer[] reducePushStatus = {0, 1, 2, 5};
                        reduce(1, "S", reducePushStatus);
                    }else {
                        return false;
                    }
                    continue;
                case 7:
                    if (VARIABLE_PARAMETER.equals(ch) || ";".equals(ch)) {
                        reduce(3,"s",null);
                        showSLR();
                        System.out.println("acc");
                    }else {
                        return false;
                    }
                    continue;
                case 8:
                    if (VARIABLE_PARAMETER.equals(ch) || "?".equals(ch) || ")".equals(ch) || "#".equals(ch) || ";".equals(ch)) {
                        Integer[] reducePushStatus = {4, 7, 6, 17, 13, 24};
                        reduce(1, "B", reducePushStatus);
                    } else if ("+".equals(ch)) {
                        shiftIn(18, ch);
                    } else if ("-".equals(ch)) {
                        shiftIn(19, ch);
                    }else {
                        return false;
                    }
                    continue;
                case 9:
                    if (VARIABLE_PARAMETER.equals(ch) || "?".equals(ch) || "+".equals(ch) || "-".equals(ch) || ")".equals(ch) || "#".equals(ch) || ";".equals(ch)) {
                        Integer[] reducePushStatus = {4, 8, 6, 8, 13, 8};
                        reduce(1, "C", reducePushStatus);
                    } else if ("*".equals(ch)) {
                        shiftIn(20, ch);
                    } else if ("/".equals(ch)) {
                        shiftIn(21, ch);
                    } else {
                        return false;
                    }
                    continue;
                case 10:
                    if (VARIABLE_PARAMETER.equals(ch) || "?".equals(ch) || "+".equals(ch) || "-".equals(ch) || "*".equals(ch) || "/".equals(ch) || ")".equals(ch) || "#".equals(ch) || ";".equals(ch)) {
                        Integer[] reducePushStatus = {4, 9, 6, 9, 13, 9, 18, 27, 19, 28};
                        reduce(1, "T", reducePushStatus);
                    }else {
                        return false;
                    }
                    continue;
                case 11:
                    if (VARIABLE_PARAMETER.equals(ch) || "?".equals(ch) || "+".equals(ch) || "-".equals(ch) || "*".equals(ch) || "/".equals(ch) || ")".equals(ch) || "#".equals(ch) || ",".equals(ch) || ";".equals(ch)) {
                        Integer[] reducePushStatus = {4, 10, 6, 10, 13, 10, 18, 10, 19, 10, 20, 29, 21, 30};
                        reduce(1, "F", reducePushStatus);
                    } else if ("^".equals(ch)) {
                        shiftIn(22, ch);
                    }else {
                        return false;
                    }
                    continue;
                case 12:
                    if ("(".equals(ch)) {
                        shiftIn(23, ch);
                    }else {
                        return false;
                    }
                    continue;
                case 14:
                    if ("(".equals(ch)) {
                        shiftIn(25, ch);
                    }else {
                        return false;
                    }
                    continue;
                case 15:
                    if (VARIABLE_PARAMETER.equals(ch) || "?".equals(ch) || "+".equals(ch) || "-".equals(ch) || "*".equals(ch) || "/".equals(ch) || ")".equals(ch) || "#".equals(ch) || ",".equals(ch) || "^".equals(ch) || ";".equals(ch)) {
                        Integer[] reducePushStatus = {4, 11, 6, 11, 13, 11, 18, 11, 19, 11, 20, 11, 21, 11, 22, 31, 23, 32, 25, 34};
                        reduce(1, "E", reducePushStatus);
                    }else {
                        return false;
                    }
                    continue;
                case 16:
                    if (VARIABLE_PARAMETER.equals(ch) || "?".equals(ch) || "+".equals(ch) || "-".equals(ch) || "*".equals(ch) || "/".equals(ch) || ")".equals(ch) || "#".equals(ch) || ",".equals(ch) || "^".equals(ch) || ";".equals(ch)) {
                        Integer[] reducePushStatus = {4, 11, 6, 11, 13, 11, 18, 11, 19, 11, 20, 11, 21, 11, 22, 31, 23, 33, 25, 34};
                        reduce(1, "E", reducePushStatus);
                    }else {
                        return false;
                    }
                    continue;
                case 17:
                    if (";".equals(ch)) {
                        shiftIn(26, ch);
                    }else {
                        return false;
                    }
                    continue;
                case 22:
                case 23:
                case 25:
                case 36:
                    switch (ch) {
                        case VARIABLE_PARAMETER:
                            shiftIn(15, ch);
                            continue;
                        case "-":
                            shiftIn(14, ch);
                            continue;
                        case "(":
                            shiftIn(13, ch);
                            continue;
                        case CONSTANT_PARAMETER:
                            shiftIn(16, ch);
                            continue;
                        default:
                            return false;
                    }
                case 24:
                    if (")".equals(ch)) {
                        shiftIn(33, ch);
                    }else {
                        return false;
                    }
                    continue;
                case 26:
                    if (VARIABLE_PARAMETER.equals(ch) || "?".equals(ch) || "#".equals(ch) || ";".equals(ch)) {
                        Integer[] reducePushStatus = {0, 2};
                        reduce(4, "A", reducePushStatus);
                    }else {
                        return false;
                    }
                    continue;
                case 27:
                case 28:
                    if (VARIABLE_PARAMETER.equals(ch) || "?".equals(ch) || "+".equals(ch) || "-".equals(ch) || ")".equals(ch) || "#".equals(ch) || ";".equals(ch)) {
                        Integer[] reducePushStatus = {4, 8, 6, 8, 13, 8};
                        reduce(3, "C", reducePushStatus);
                    } else if ("*".equals(ch)) {
                        shiftIn(20, ch);
                    } else if ("/".equals(ch)) {
                        shiftIn(21, ch);
                    } else {
                        return false;
                    }
                    continue;
                case 29:
                case 30:
                    if (VARIABLE_PARAMETER.equals(ch) || "?".equals(ch) || "+".equals(ch) || "-".equals(ch) || "*".equals(ch) || "/".equals(ch) || ")".equals(ch) || "#".equals(ch) || ";".equals(ch)) {
                        Integer[] reducePushStatus = {4, 9, 6, 9, 13, 9, 18, 27, 19, 28};
                        reduce(3, "T", reducePushStatus);
                    }else {
                        return false;
                    }
                    continue;
                case 31:
                    if (VARIABLE_PARAMETER.equals(ch) || "?".equals(ch) || "+".equals(ch) || "-".equals(ch) || "*".equals(ch) || "/".equals(ch) || ")".equals(ch) || "#".equals(ch) || ",".equals(ch) || "^".equals(ch) || ";".equals(ch)) {
                        Integer[] reducePushStatus = {4, 10, 6, 10, 13, 10, 18, 10, 19, 10, 20, 29, 21, 30};
                        reduce(3, "F", reducePushStatus);
                    }else {
                        return false;
                    }
                    continue;
                case 32:
                    if (")".equals(ch)) {
                        shiftIn(35, ch);
                    } else if (",".equals(ch)) {
                        shiftIn(36, ch);
                    }else {
                        return false;
                    }
                    continue;
                case 33:
                    if (VARIABLE_PARAMETER.equals(ch) || "?".equals(ch) || "+".equals(ch) || "-".equals(ch) || "*".equals(ch) || "/".equals(ch) || ")".equals(ch) || "#".equals(ch) || ",".equals(ch) || "^".equals(ch) || ";".equals(ch)) {
                        Integer[] reducePushStatus = {4, 11, 6, 11, 13, 11, 18, 11, 19, 11, 20, 11, 21, 11, 22, 31, 23, 33, 25, 34};
                        reduce(3, "E", reducePushStatus);
                    }else {
                        return false;
                    }
                    continue;
                case 34:
                    if (")".equals(ch)) {
                        shiftIn(37, ch);
                    }else {
                        return false;
                    }
                    continue;
                case 35:
                    if (VARIABLE_PARAMETER.equals(ch) || "?".equals(ch) || "+".equals(ch) || "-".equals(ch) || "*".equals(ch) || "/".equals(ch) || ")".equals(ch) || "#".equals(ch) || ",".equals(ch) || "^".equals(ch) || ";".equals(ch)) {
                        Integer[] reducePushStatus = {4, 10, 6, 10, 13, 10, 18, 10, 19, 10, 20, 29, 21, 30};
                        reduce(4, "F", reducePushStatus);
                    }else {
                        return false;
                    }
                    continue;
                case 37:
                    if (VARIABLE_PARAMETER.equals(ch) || "?".equals(ch) || "+".equals(ch) || "-".equals(ch) || "*".equals(ch) || "/".equals(ch) || ")".equals(ch) || "#".equals(ch) || ",".equals(ch) || "^".equals(ch)){
                        Integer[] reducePushStatus = {4,11,6,11,13,11,18,11,19,11,20,11,21,11,22,31,23,33,25,34};
                        reduce(4,"E",reducePushStatus);
                    }else {
                        return false;
                    }
                    continue;
                case 38:
                    if (")".equals(ch)){
                        shiftIn(39,ch);
                    }else {
                        return false;
                    }
                    continue;
                case 39:
                    if (VARIABLE_PARAMETER.equals(ch) || "?".equals(ch) || "+".equals(ch) || "-".equals(ch) || "*".equals(ch) || "/".equals(ch) || ")".equals(ch) || "#".equals(ch) || ",".equals(ch) || "^".equals(ch)){
                        Integer[] reducePushStatus = {4,10,6,10,13,10,18,10,19,10,20,29,21,30};
                        reduce(6,"F",reducePushStatus);
                    }else {
                        return false;
                    }
                    continue;
                default:

            }
        }
        return true;
    }

    private void shiftIn(Integer pushStatus, String pushGrammar) {
        status.push(pushStatus);
        grammar.push(pushGrammar);
        showSLR();
        System.out.println("移进");
    }

    private void reduce(Integer reduceCount, String pushGrammar, Integer[] pushStatus) {
        showSLR();
        System.out.println("规约");
        for (int i = 0; i < reduceCount; i++) {
            status.pop();
            grammar.pop();
        }
        grammar.push(pushGrammar);
        reducePushStatus(pushStatus);
    }

    private void reducePushStatus(Integer[] pushStatus) {
        if (null != pushStatus) {
            if (pushStatus.length % 2 != 0) {
                throw new RuntimeException("规约键值对出错！");
            } else {
                for (int i = 0; i < pushStatus.length; i++) {
                    if (status.peek().equals(pushStatus[i++])) {
                        readNext = false;
                        status.push(pushStatus[i]);
                        break;
                    }
                }
            }
        }
    }

    public static Boolean analyzer(List<Integer> token) {
        GrammaticalAnalyzer grammaticalAnalyzer = new GrammaticalAnalyzer();
        List<String> expression = grammaticalAnalyzer.translate(token);
        for (String x : expression) {
            System.out.print(x);
        }
        System.out.println();
        if (grammaticalAnalyzer.move(expression)){
            System.out.println("success!");
            return true;
        }else {
            System.out.println("error!");
            return false;
        }
    }
}
