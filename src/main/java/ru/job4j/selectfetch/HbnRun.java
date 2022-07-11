package ru.job4j.selectfetch;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class HbnRun {
    public static void main(String[] args) {
        Object rzl = null;
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure()
                .build();
        try {
            SessionFactory sf = new MetadataSources(registry)
                    .buildMetadata()
                    .buildSessionFactory();
            Session session = sf.openSession();
            session.beginTransaction();
            Candidate candidate = new Candidate("Boris", 4, 200_000);
            VacanciesDB vacanciesDB = new VacanciesDB("new database");
            vacanciesDB.addVacancies(new Vacancy("vacancy one"));
            vacanciesDB.addVacancies(new Vacancy("vacancy two"));
            vacanciesDB.addVacancies(new Vacancy("vacancy three"));
            session.save(vacanciesDB);
            candidate.setVacanciesDB(vacanciesDB);
            session.save(candidate);
            rzl = selectById(candidate.getId(), session);
            session.getTransaction().commit();
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            StandardServiceRegistryBuilder.destroy(registry);
        }
        System.out.println(rzl);
    }

    public static Object selectById(int id, Session session) {
        return session.createQuery("select distinct c from Candidate c "
                        + "join fetch c.vacanciesDB vdb "
                        + "join fetch vdb.vacancies"
                        + " where c.id=:ID")
                .setParameter("ID", id).uniqueResult();
    }
}
