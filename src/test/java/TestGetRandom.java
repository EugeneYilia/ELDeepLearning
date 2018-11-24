import java.util.Random;

public class TestGetRandom {
    public static void main(String[] args) {
        for(int i =0;i<100;i++) {
            System.out.println(new Random().nextDouble());
        }
    }
}
