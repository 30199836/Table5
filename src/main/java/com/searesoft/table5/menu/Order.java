package com.searesoft.table5.menu;

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
        }
    }

}
