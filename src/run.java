import java.util.List;

/**
 * @author Tsening Chu
 * @version 1.0
 * @date 2018/12/18
 */
public class run {
    public static void main(String[] args){
        List<Integer> token = LexicalAnalyzer.analyzer("D:/test.txt");
        for (Integer toke : token){
            System.out.print(toke+" ");
        }
    }
}