import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class Cat implements Runnable {

    //Статический контейнер всех созданных "кототредов"
    //Класс CopyOnWriteArrayList - тот же ArrayList, только потокобезопасный
    public static final List<Cat> cats = new CopyOnWriteArrayList<>();

    // имя и количество жизней (unmappable character (0x98) for encoding windows-1251)
    private String name;
    private volatile int life;
    // Личный поток
    private Thread thread;

    public Cat(String name, int life, String threadName) {
        this.name = name;                            // имя кота (unmappable character (0x98) for encoding windows-1251)
        this.life = life;                            // Количество жизней
        Cat.cats.add(this);                          // Добавляем себя в List<Cat> cats
        thread = new Thread(this, threadName); // Создаём поток этого кота и передаём ему ссылку на себя

        System.out.println(String.format("Кот %s cоздан. HP: %d", this.name, this.life));
    }

    // Атака. Принимает текущего кота и кота-противника. Метод синхронизирован
    public static synchronized void attack(Cat thisCat, Cat enemyCat) {

        // Дополнительная проверка жизни — во избежание конфликта (у кота может не быть жизней)
        if (thisCat.getLife() <= 0) {
            return;
        }

        // Отнимаем жизнь противника
        enemyCat.decrementLife();
        System.out.println(String.format("Кот %s атаковал кота %s. Жизни %<s: %d", thisCat.getName(), enemyCat.getName(), enemyCat.getLife()));

        // Если противник не имеет жизней
        if (enemyCat.getLife() <= 0) {
            Cat.cats.remove(enemyCat);

            System.out.println(String.format("Кот %s покидает бой.", enemyCat.getName()));
            System.out.println(String.format("Оставшиеся коты: %s", Cat.cats));
            System.out.println(String.format("%s завершает свою работу.", enemyCat.getThread().getName()));

            enemyCat.getThread().interrupt();
        }
    }

    @Override
    public void run() {
        System.out.println(String.format("Кот %s идет в бой.", name));

        while (Cat.cats.size() > 1) {
            // Атакуем произвольного кота из оставшихся, кроме себя
            Cat.attack(this, getRandomEnemyCat(this));
        }
    }

    // Возвращает произвольный объект Cat из cats, кроме самого себя
    private Cat getRandomEnemyCat(Cat deleteThisCat) {

        // Создаём лист-копию из основного листа cats
        List<Cat> copyCats = new ArrayList<>(Cat.cats);
        // Удаляем текущего кота, чтобы он не выпал в качестве противника
        copyCats.remove(deleteThisCat);
        // Возвращаем произвольного кота из оставшихся с помощью класса util.java.Random
        return copyCats.get(new Random().nextInt(copyCats.size()));
    }

    // Декремент жизней
    public synchronized void decrementLife() {
        life--;
    }

    // Нужен для корректного вывода
    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public int getLife() {
        return life;
    }

    public Thread getThread() {
        return thread;
    }
}
