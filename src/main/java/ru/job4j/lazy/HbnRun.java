package ru.job4j.lazy;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Если попытаться получить доступ к вложенному объекту (в нашем случае коллекция)
 * за пределами сессии то получим LazyInitializationException.
 * Есть несколько решений этой проблемы:
 * <p>
 * 1. Добавить стратегию загрузки @OneToMany(mappedBy = "car", fetch = FetchType.EAGER)
 * Но так делать не стоит, потому что у нас может быть многоуровневые связи,
 * что увеличит время работы запроса.
 * <p>
 * 2. Использовать в запросе join fetch. В данном случае с помощью join fetch
 * мы изменим стратегию загрузки связанных сущностей прямо в запросе к БД
 * list = session.createQuery(
 * "select distinct c from Car c join fetch c.models"
 * ).list();
 * <p>
 * 3. Обрабатывать полученные данные в области видимости сессии
 */

public class HbnRun {
    public static void main(String[] args) {
        List<Car> listInSession = new ArrayList<>();
        List<Car> listOutSession = new ArrayList<>();
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure()
                .build();
        try {
            SessionFactory sf = new MetadataSources(registry)
                    .buildMetadata()
                    .buildSessionFactory();
            Session session = sf.openSession();
            session.beginTransaction();
            saveCar(session, "Opel", "Astra", "Corsa", "Omega");
            saveCar(session, "Vaz", "2101", "2109");
            saveCar(session, "BMW", "M5", "X3");
            listInSession = session.createQuery("from Car").list();
            System.out.println("Обработка внутри сессии");
            for (Car cars : listInSession) {
                for (Model model : cars.getModels()) {
                    System.out.println(model);
                }
            }
            listOutSession = session.createQuery(
                    "select distinct c from Car c join fetch c.models"
            ).list();
            session.getTransaction().commit();
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            StandardServiceRegistryBuilder.destroy(registry);
        }
        System.out.println("Обработка вне области видимости сессии при помощи join fetch");
        for (Car cars : listOutSession) {
            for (Model model : cars.getModels()) {
                System.out.println(model);
            }
        }
    }

    public static void saveCar(Session session, String carName, String... modelName) {
        Car car = new Car(carName);
        for (String s : modelName) {
            car.getModels().add(new Model(s, car));
        }
        session.save(car);
    }
}
