package com.searesoft.table5.menu;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

/**
 * The order class that encapsulates an entire order
 */
public class Order {
    /**
     * A single choice for a particular order item
     */
    public class Choice {
        public String name;
        public ArrayList<String> items = new ArrayList<>();
        public ArrayList<Double> prices = new ArrayList<>();
        private Item item;

        public Item item() {
            return item;
        }

        public Choice(Item item) {
            this.item = item;
        }
    }

    /**
     * A single item in the order
     */
    public class Item {
        public String name;
        //the name of the selected option eg. single, meal, large, small
        public String option;
        //number of items
        public int count;
        public double price;

        //list of choices for this item
        public ArrayList<Choice> choices = new ArrayList<>();

        private Order order;

        public Order order() {
            return order;
        }

        public Item(Order order) {
            this.order = order;
        }
    }

    private double price = 0;

    public double price() {
        return price;
    }

    //list of ordered items
    public final ArrayList<Item> items = new ArrayList<>();


    /**
     * Calculate and set the total price of all the items
     */
    public void calculatePrice() {
        price = 0;
        for (Item item : items) {
            price += item.price;
//            for (Choice choice : item.choices) {
//                for (int i = 0; i < choice.prices.size(); i++) {
//                    price += choice.prices.get(i);
//                }
//            }
        }
    }

    //Simple persistent storage for the order ID so it increments even after restarting the app
    private int id = -1;
    private String idFilename = System.getProperty("user.dir") + "\\id.txt";

    /**
     * Increment and return the order ID and handle the persistent storage in a text file
     *
     * @return the next order ID
     */
    public int nextID() {
        if (id == -1) {
            try {
                id = Integer.parseInt(new String(Files.readAllBytes(Paths.get(idFilename)), StandardCharsets.UTF_8));
            } catch (Exception e) {
                //the file probably didn't exist, just start from zero
                id = 0;
            }
        }
        id++;
        try {
            Files.writeString(Paths.get(idFilename), String.valueOf(id), StandardOpenOption.CREATE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return id;
    }
}
