package com.searesoft.table5.menu;

import static com.searesoft.lib.StrUtils.elfHash;

import com.searesoft.lib.XMLParser;

import java.util.ArrayList;

/**
 * A reusable list that's used for things like Salad options, pizza toppings etc. in the menu choices
 */
public class MenuList {
    private Menu owner = null;
    private String name = "";
    private int hash = 0;
    private final ArrayList<MenuListItem> items = new ArrayList<>();

    /**
     * Read access to items
     *
     * @return List of MenuListItem
     */
    public ArrayList<MenuListItem> items() {
        return items;
    }

    /**
     * Read access to the owner menu
     *
     * @return the owner Menu
     */
    public Menu owner() {
        return owner;
    }

    /**
     * Read access to name
     *
     * @return the menu list name
     */
    public String name() {
        return name;
    }

    /**
     * Write access to name
     *
     * @param name  The menu list name
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
     * Constructor
     *
     * @param owner the owner Menu
     * @param name the menu list name
     */
    MenuList(Menu owner, String name) {
        setName(name);
        this.owner = owner;
    }

    /**
     * Find a menu list item by name
     *
     * @param name The menu list item name
     *
     * @return MenuListItem or null
     */
    public MenuListItem itemFromName(String name) {
        int i = indexOf(name);
        return i > -1 ? items.get(i) : null;
    }

    /**
     * Get the index of a menu list item by name
     *
     * @param name The menu list item name
     *
     * @return the item's index or -1
     */
    public int indexOf(String name) {
        int hash = elfHash(name);
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).hash() == hash && items.get(i).name().equalsIgnoreCase(name)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Parse this menu list from an XML tag
     *
     * @param tag  The XML tag containing the metadata for this menu list
     */
    void parseXML(XMLParser.XMLTag tag) {
        //add all the menu list items
        for (int j = 0; j < tag.children().size(); j++) {
            XMLParser.XMLTag tag2 = tag.children().get(j);
            MenuListItem item = new MenuListItem(this, tag2.text());
            items.add(item);
            item.parseXML(tag2);
        }
    }
}


