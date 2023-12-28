package com.searesoft.table5.menu;

import com.searesoft.lib.XMLParser;

import java.util.ArrayList;

import static com.searesoft.lib.StrUtils.elfHash;

/**
 * The category class that encapsulates each category branch
 */
public class MenuCategory {
    private Menu owner = null;
    private String name = "";
    private int hash = 0;
    private String description = "";
    private final ArrayList<MenuItem> menuItems = new ArrayList<>();

    /**
     * Access to the menuItems
     *
     * @return List of MenuItem
     */
    public ArrayList<MenuItem> menuItems() {
        return menuItems;
    }

    /**
     * Read access to owner menu
     *
     * @return the owner Menu
     */
    public Menu owner() {
        return owner;
    }

    /**
     * Read access to name
     *
     * @return the category name
     */
    public String name() {
        return name;
    }

    /**
     * Write access to name
     *
     * @param name The category name
     */
    public void setName(String name) {
        this.name = name;
        hash = elfHash(name);
    }

    /**
     * Read access to the name hash
     *
     * @return the name hash
     */
    public int hash() {
        return hash;
    }

    /**
     * Read access to description
     *
     * @return The category description
     */
    public String description() {
        return description;
    }

    /**
     * Constructor
     *
     * @param owner The Menu that owns this category
     * @param name  The category name
     */
    MenuCategory(Menu owner, String name) {
        setName(name);
        this.owner = owner;
    }

    /**
     * Find a menuItem by name
     *
     * @param name The name of the menu item
     * @return The Menu item or null
     */
    public MenuItem menuItemFromName(String name) {
        int hash = elfHash(name);
        for (MenuItem item : menuItems) {
            if (item.hash() == hash && item.name().equalsIgnoreCase(name)) {
                return item;
            }
        }
        return null;
    }

    /**
     * Parse this category from an XML tag
     *
     * @param tag The XML tag containing the metadata for this category
     */
    void parseXML(XMLParser.XMLTag tag) {
        //set the description if it exists
        XMLParser.XMLTag tag2 = tag.childFromName("description");
        if (tag2 != null) {
            description = tag2.text();
        }
        //add all the menu items
        tag2 = tag.childFromName("menuItem");
        while (tag2 != null) {
            MenuItem item = new MenuItem(this, tag2.valueFromName("name").text());
            menuItems.add(item);
            item.parseXML(tag2);
            tag2 = tag2.nextSiblingFromName("menuItem");
        }
    }
}
