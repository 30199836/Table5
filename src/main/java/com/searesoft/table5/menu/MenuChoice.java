package com.searesoft.table5.menu;

import com.searesoft.lib.XMLParser;

import java.util.ArrayList;

import static com.searesoft.lib.StrUtils.elfHash;

/**
 * The choice class that encapsulates each menu choice
 */
public class MenuChoice {
    private MenuOption owner = null;
    private MenuList menuList = null;
    private String name = "";
    private int hash = 0;
    private int requiredCount = 0;
    private int allowedCount = 0;

    //  private boolean required = false;
    private ArrayList<Integer> selectedIndices = new ArrayList<>();

    /**
     * Access to selectedIndices
     *
     * @return The currently selected indices for this choice
     */
    public ArrayList<Integer> selectedIndices() {
        return selectedIndices;
    }

    /**
     * Read access to menuList
     *
     * @return The MenuList associated with this choice
     */
    public MenuList menuList() {
        return menuList;
    }

    /**
     * Read access to owner
     *
     * @return the owner MenuOption
     */
    public MenuOption owner() {
        return owner;
    }

    /**
     * Read acces to name
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
     * read access to the name hash
     *
     * @return The name hash
     */
    public int hash() {
        return hash;
    }

    /**
     * Read access to requiredCount
     *
     * @return The number of selections this choice requires
     */
    public int requiredCount() {
        return requiredCount;
    }

    /**
     * Read access to allowedCount
     *
     * @return The number of selections that are allowed for this choice
     */
    public int allowedCount() {
        return allowedCount;
    }

    /**
     * Constructor
     *
     * @param owner the owner menu option
     * @param name  the choice name
     */
    MenuChoice(MenuOption owner, String name) {
        setName(name);
        this.owner = owner;
    }

    /**
     * Copy constructor
     *
     * @param owner      the owner menu option
     * @param menuChoice the menu choice to duplicate
     */
    MenuChoice(MenuOption owner, MenuChoice menuChoice) {
        setName(menuChoice.name);
        this.owner = owner;
        menuList = menuChoice.menuList;
        requiredCount = menuChoice.requiredCount;
        allowedCount = menuChoice.allowedCount;
        // selectedIndices.addAll(menuChoice.selectedIndices);
    }

    /**
     * Parse this choice from an XML tag
     *
     * @param tag The XML tag containing the metadata for this menu choice
     */
    protected void parseXML(XMLParser.XMLTag tag) {
        //set the required count if it exists
        XMLParser.XMLValue value = tag.valueFromName("requiredCount");
        if (value != null) requiredCount = Integer.parseInt(value.text());
        //set the allowed count if it exists
        value = tag.valueFromName("allowedCount");
        if (value != null) allowedCount = Integer.parseInt(value.text());
        //set the menu list from the owner Menu
        value = tag.valueFromName("menuList");
        if (value != null) menuList = owner().owner().owner().owner().menuListFromName(value.text());
    }
}
