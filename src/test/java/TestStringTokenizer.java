import java.util.StringTokenizer;

public class TestStringTokenizer {
    public static void main(String[] args) {
        String line = "Time,V1,V2,V3";//4
        StringTokenizer stringTokenizer = new StringTokenizer(line,",");
        System.out.println(stringTokenizer.countTokens());
    }
}
