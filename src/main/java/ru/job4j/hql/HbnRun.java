package ru.job4j.hql;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Query;

public class HbnRun {
    public static void main(String[] args) {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure()
                .build();
        try {
            SessionFactory sf = new MetadataSources(registry)
                    .buildMetadata()
                    .buildSessionFactory();
            Session session = sf.openSession();
            session.beginTransaction();
            Candidate candidate1 = new Candidate("Max", 2, 200_000);
            Candidate candidate2 = new Candidate("Joe", 5, 300_000);
            Candidate candidate3 = new Candidate("Boris", 1, 100_000);
            session.save(candidate1);
            session.save(candidate2);
            session.save(candidate3);

            select(session);
            selectById(1, session);
            selectByName("Boris", session);

            update(1, "NewMaxName", 5, session);
            selectById(1, session);

            delete(2, session);
            select(session);

            insert(1, session);
            select(session);

            session.getTransaction().commit();
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }

    public static void select(Session session) {
        Query query = session.createQuery("from Candidate ");
        for (Object st : query.list()) {
            System.out.println(st);
        }
    }

    public static void selectByName(String name, Session session) {
        Query query = session.createQuery("from Candidate c where c.name=:selectName");
        query.setParameter("selectName", name);
        System.out.println(query.uniqueResult());
    }

    public static void selectById(int id, Session session) {
        Query query = session.createQuery("from Candidate c where c.id=:fId");
        query.setParameter("fId", id);
        System.out.println(query.uniqueResult());
    }

    public static void update(int id, String name, int exp, Session session) {
        session.createQuery("update Candidate c set c.name=:newName, c.experience=:newExperience where c.id=:fId")
                .setParameter("newName", name)
                .setParameter("newExperience", exp)
                .setParameter("fId", id)
                .executeUpdate();
    }

    public static void delete(int id, Session session) {
        session.createQuery("delete from Candidate where id=:fId")
                .setParameter("fId", id)
                .executeUpdate();
    }

    public static void insert(int id, Session session) {
        session.createQuery("insert into Candidate (name,experience,salary) "
                        + "select c.name , c.experience+1, c.salary+20000 from Candidate c where c.id=:fId")
                .setParameter("fId", id)
                .executeUpdate();
    }
}
