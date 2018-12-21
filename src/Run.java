import java.util.List;

/**
 * @author Tsening Chu
 * @version 1.0
 * @date 2018/12/18
 */
public class Run {
    public static void main(String[] args){
        List<Integer> token = LexicalAnalyzer.analyzer("D:/test.txt");
        Boolean grammaticalCheck = GrammaticalAnalyzer.analyzer(token);
    }
}
