package com.searesoft.table5.menu;

import com.searesoft.lib.XMLParser;

import java.util.ArrayList;

import static com.searesoft.lib.StrUtils.elfHash;

/**
 * The option class that encapsulates each option branch
 */
public class MenuOption {
    private MenuItem owner;
    private final ArrayList<MenuChoice> choices = new ArrayList<>();

    private int hash = 0;
    private String name = "";
    private double price = 0;

    /**
     * Access to choices
     *
     * @return List of MenuChoice
     */
    public ArrayList<MenuChoice> choices() {
        return choices;
    }

    /**
     * Read access to name
     *
     * @return the choice name
     */
    public String name() {
        return name;
    }

    /**
     * Write access to name
     *
     * @param name the choice name
     */
    public void setName(String name) {
        this.name = name;
        hash = elfHash(name);
    }

    /**
     * Read access to price
     *
     * @return the option price
     */
    public double price() {
        return price;
    }

    /**
     * Read acess to the name hash
     *
     * @return The name hash
     */
    public int hash() {
        return hash;
    }

    /**
     * Read access to the owner menu item
     *
     * @return teh owner MenuItem
     */
    public MenuItem owner() {
        return owner;
    }

    /**
     * Constructor
     *
     * @param owner The owner menu item
     * @param name The option name
     */
    MenuOption(MenuItem owner, String name) {
        setName(name);
        this.owner = owner;
    }

    /**
     * Copy constructor
     *
     * @param owner The owner menu item
     * @param menuOption The menu option to duplicate
     */
    MenuOption(MenuItem owner, MenuOption menuOption) {
        setName(menuOption.name);
        this.owner = owner;
        price = menuOption.price;
        for (MenuChoice choice : menuOption.choices) {
            choices.add(new MenuChoice(this, choice));
        }
    }

    /**
     * Parse this option from an XML tag
     *
     * @param tag  The XML tag containing the metadata for this menu option
     */
    protected void parseXML(XMLParser.XMLTag tag) {
        //get the price from a tag
        XMLParser.XMLTag tag2 = tag.childFromName("price");
        if (tag2 == null) {
            //try from the value
            XMLParser.XMLValue value = tag.valueFromName("price");
            if (value != null) {
                price = Double.parseDouble(value.text());
            }
        } else {
            price = Double.parseDouble(tag2.text());
        }

        //get the first choice
        tag2 = tag.childFromName("choice");
        MenuChoice choice;

        if (tag2 != null) {
            //add all the choices
            while (tag2 != null) {
                choice = new MenuChoice(this, tag2.valueFromName("name").text());
                choices.add(choice);
                choice.parseXML(tag2);
                tag2 = tag2.nextSiblingFromName("choice");
            }
        }
    }
}
