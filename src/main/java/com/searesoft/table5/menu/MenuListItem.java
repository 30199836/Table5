package com.searesoft.table5.menu;

import com.searesoft.lib.XMLParser;

import java.util.ArrayList;

import static com.searesoft.lib.StrUtils.elfHash;

/**
 * An entry in the MenuList, eg. a Pizza topping with prices for small, medium, large.  A single salad choice like lettuce, tomato etc.
 */
public class MenuListItem {
    private MenuList owner = null;
    private final ArrayList<Double> prices = new ArrayList<>();
    private String name = "";
    private int hash = 0;

    /**
     * Access to prices
     *
     * @return list of prices
     */
    public ArrayList<Double> prices() {
        return prices;
    }

    /**
     * Read access to name
     *
     * @return name the menu list item name
     */
    public String name() {
        return name;
    }

    /**
     * Write access to name
     *
     * @param name the menu list item name
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
     * @param owner the owner menu list
     * @param name  the menu list item name
     */
    MenuListItem(MenuList owner, String name) {
        setName(name);
        this.owner = owner;
    }

    /**
     * Parse this menu list item from an XML tag
     *
     * @param tag The XML tag containing the metadata for this menu list item
     */
    protected void parseXML(XMLParser.XMLTag tag) {
        //add all the prices
        for (int i = 0; i < tag.values().size(); i++) {
            if (tag.values().get(i).name().contains("price")) {
                prices.add(Double.parseDouble(tag.values().get(i).text()));
            }
        }
    }
}
