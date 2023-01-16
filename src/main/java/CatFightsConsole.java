import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CatFightsConsole {

    public static void main(String[] args) {

        System.out.println("Cat Fights Console");

        List<Cat> catThreads = new ArrayList<>();

        int life = 100;

        Collections.addAll(catThreads,
                new Cat("Tom", life, "Tread Tom"),
                new Cat("Cleocatra", life, "Tread Cleocatra"),
                new Cat("Dupli", life, "Tread Dupli"),
                new Cat("Toodles", life, "Tread Toodles"));

        for (Cat cat : catThreads) {
            cat.getThread().start();
        }

        for (Cat cat : catThreads) {
            try {
                cat.getThread().join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println(String.format("Кот-победитель: %s!!!", Cat.cats.get(0)));
    }
}
