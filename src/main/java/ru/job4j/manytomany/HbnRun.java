package ru.job4j.manytomany;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

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

            Book bookOne = new Book("BookOne");
            Book bookTwo = new Book("BookTwo");
            Book bookThree = new Book("BookThree");

            Author authorOne = new Author("AuthorOne");
            authorOne.addBook(bookOne);
            authorOne.addBook(bookTwo);
            authorOne.addBook(bookThree);
            Author authorTwo = new Author("AuthorTwo");
            authorTwo.addBook(bookOne);
            authorTwo.addBook(bookThree);

            session.persist(authorOne);
            session.persist(authorTwo);

            session.remove(session.get(Author.class, 1));

            session.getTransaction().commit();
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }
}
