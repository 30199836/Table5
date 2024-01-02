package com.searesoft.table5.menu;

import com.searesoft.lib.XMLParser;

import java.util.ArrayList;

import static com.searesoft.lib.StrUtils.elfHash;

/**
 * The menu item class that encapsulates each menu item branch
 */
public class MenuItem {
    private MenuCategory owner;
    private final ArrayList<MenuOption> menuOptions = new ArrayList<>();
    private String name;
    private int hash;
    private String description;

    public int optionIndex = -1;

    /**
     * Read access to optionIndex
     *
     * @return The currently selected option index
     */
    public int optionIndex() {
        return optionIndex;
    }

    /**
     * Write access to optionIndex
     *
     * @param optionIndex The currently selected option index
     */
    public void setOptionIndex(int optionIndex) {
        this.optionIndex = optionIndex;
    }

    /**
     * Set the option index from the option name
     *
     * @param name the option name
     */
    public void setOptionIndexFromName(String name) {
        int hash = elfHash(name);
        for (int i = 0; i < menuOptions.size(); i++) {
            if (menuOptions.get(i).hash() == hash && menuOptions.get(i).name().equalsIgnoreCase(name)) {
                optionIndex = i;
            }
        }
        this.optionIndex = -1;
    }

    /**
     * Get the option index from the option name
     *
     * @param name the option name
     * @return the option index or -1
     */
    public int optionIndexFromName(String name) {
        int hash = elfHash(name);
        for (int i = 0; i < menuOptions.size(); i++) {
            if (menuOptions.get(i).hash() == hash && menuOptions.get(i).name().equalsIgnoreCase(name)) {
                return i;
            }
        }
        return -1;
    }
    /**
     * Get the MenuOption from the option name
     *
     * @param name the option name
     * @return the MenuOption or null
     */
    public MenuOption optionFromName(String name) {
        int hash = elfHash(name);
        for (int i = 0; i < menuOptions.size(); i++) {
            if (menuOptions.get(i).hash() == hash && menuOptions.get(i).name().equalsIgnoreCase(name)) {
                return options().get(i);
            }
        }
        return null;
    }

    /**
     * Access to options
     *
     * @return list of MenuOption
     */
    public ArrayList<MenuOption> options() {
        return menuOptions;
    }

    /**
     * Read access to owner category
     *
     * @return The owner MenuCategory
     */
    public MenuCategory owner() {
        return owner;
    }

    /**
     * Read Access to name
     *
     * @return menu item name
     */
    public String name() {
        return name;
    }

    /**
     * Write access to name
     *
     * @param name the menu item name
     */
    public void setName(String name) {
        this.name = name;
        hash = elfHash(name);
    }

    /**
     * Read access to description
     *
     * @return the menu item description
     */
    public String description() {
        return description;
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
     * @param owner The owner category
     * @param name  The menu item name
     */
    MenuItem(MenuCategory owner, String name) {
        setName(name);
        this.owner = owner;
    }

    /**
     * Copy constructor
     *
     * @param owner    the owner category
     * @param menuItem the menu item to duplicate
     */
    public MenuItem(MenuCategory owner, MenuItem menuItem) {
        setName(menuItem.name);
        this.owner = owner;
        description = menuItem.description;
        for (MenuOption option : menuItem.menuOptions) {
            menuOptions.add(new MenuOption(this, option));
        }
    }

    /**
     * Find an option by name
     *
     * @param name The name of the option
     * @return The MenuOption or null
     */
    public MenuOption menuOptionFromName(String name) {
        int hash = elfHash(name);
        for (MenuOption option : menuOptions) {
            if (option.hash() == hash && option.name().equalsIgnoreCase(name)) {
                return option;
            }
        }
        return null;
    }

    /**
     * Parse this menu item from an XML tag
     *
     * @param tag The XML tag containing the metadata for this menu item
     */
    protected void parseXML(XMLParser.XMLTag tag) {
        //set the description if it exists
        XMLParser.XMLTag tag2 = tag.childFromName("description");
        if (tag2 != null) description = tag2.text();
        //find the first option
        tag2 = tag.childFromName("option");
        MenuOption option;

        if (tag2 == null) {
            //if there's no options add a default
            option = new MenuOption(this, "default");
            menuOptions.add(option);
            option.parseXML(tag);
        } else {
            //add all the options
            while (tag2 != null) {
                option = new MenuOption(this, tag2.valueFromName("name").text());
                menuOptions.add(option);
                option.parseXML(tag2);
                tag2 = tag2.nextSiblingFromName("option");
            }
        }
    }
}
