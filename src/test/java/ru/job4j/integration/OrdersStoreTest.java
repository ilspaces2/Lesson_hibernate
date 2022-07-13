package ru.job4j.integration;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class OrdersStoreTest {
    private BasicDataSource pool = new BasicDataSource();

    @Before
    public void setUp() throws SQLException {
        pool.setDriverClassName("org.hsqldb.jdbcDriver");
        pool.setUrl("jdbc:hsqldb:mem:tests;sql.syntax_pgs=true");
        pool.setUsername("sa");
        pool.setPassword("");
        pool.setMaxTotal(2);
        StringBuilder builder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream("./db/scripts/update_001.sql")))
        ) {
            br.lines().forEach(line -> builder.append(line).append(System.lineSeparator()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        pool.getConnection().prepareStatement(builder.toString()).executeUpdate();
    }

    @After
    public void dropTable() throws SQLException {
        pool.getConnection().prepareStatement("drop table orders").executeUpdate();
    }

    @Test
    public void whenSaveOrderAndFindAllOneRowWithDescription() {
        OrdersStore store = new OrdersStore(pool);
        store.save(Order.of("name1", "description1"));
        List<Order> all = (List<Order>) store.findAll();
        assertThat(all.size(), is(1));
        assertThat(all.get(0).getDescription(), is("description1"));
        assertThat(all.get(0).getId(), is(1));
    }

    @Test
    public void whenSaveOrderAndFindById() {
        OrdersStore store = new OrdersStore(pool);
        Order orderOne = Order.of("name1", "description1");
        Order orderTwo = Order.of("name2", "description2");
        store.save(orderOne);
        store.save(orderTwo);
        Order orderDb = store.findById(orderTwo.getId());
        assertThat(orderTwo.getName(), is(orderDb.getName()));
        assertThat(orderTwo.getDescription(), is(orderDb.getDescription()));
    }

    @Test
    public void whenSaveOrderAndFindByName() {
        OrdersStore store = new OrdersStore(pool);
        Order orderOne = Order.of("name1", "description1");
        Order orderTwo = Order.of("name2", "description2");
        store.save(orderOne);
        store.save(orderTwo);
        Order orderDb = store.findByName(orderTwo.getName());
        assertThat(orderTwo.getName(), is(orderDb.getName()));
        assertThat(orderTwo.getDescription(), is(orderDb.getDescription()));
    }

    @Test
    public void whenSaveOrderAndUpdate() {
        OrdersStore store = new OrdersStore(pool);
        Order order = Order.of("name1", "description1");
        String newName = "New name";
        store.save(order);
        order.setName(newName);
        store.update(order);
        Order orderDb = store.findById(order.getId());
        assertThat(orderDb.getName(), is(newName));
    }
}