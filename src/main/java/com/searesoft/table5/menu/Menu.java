package com.searesoft.table5.menu;

import com.searesoft.lib.*;
import com.searesoft.table5.App;

import javax.swing.plaf.MenuBarUI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static com.searesoft.lib.StrUtils.elfHash;

/**
 * The menu class that encapsulates the whole menu tree
 */
public class Menu {
    public ArrayList<MenuList> menuLists = new ArrayList<>();
    public ArrayList<MenuCategory> menuCategories = new ArrayList<>();

    /**
     * Constructor
     */
    public Menu() {
        try {
            //parse the XML menu definition file
            XMLParser xml = new XMLParser();
            xml.parse(new String(App.class.getResourceAsStream("/menu.xml").readAllBytes(), StandardCharsets.UTF_8));

            //add all the menu lists
            XMLParser.XMLTag tag = xml.root().childFromName("menu").childFromName("lists");
            for (int i = 0; i < tag.children().size(); i++) {
                XMLParser.XMLTag tag2 = tag.children().get(i);
                MenuList menuList = new MenuList(this,tag2.valueFromName("name").text());
                menuLists.add(menuList);
                menuList.parseXML(tag2);
            }
            //add all the menu category tree branches
            tag = xml.root().childFromName("menu").childFromName("categories");
            for (int i = 0; i < tag.children().size(); i++) {
                XMLParser.XMLTag tag2 = tag.children().get(i);
                MenuCategory menuCategory = new MenuCategory(this,tag2.valueFromName("name").text());
                menuCategories.add(menuCategory);
                menuCategory.parseXML(tag2);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Find a MenuList by name
     * @param name the name of the menu list
     * @return the MenuList or null
     */
    public MenuList menuListFromName(String name) {
        int hash = elfHash(name);
        for (MenuList list : menuLists) {
            if (list.hash() == hash && list.name().equalsIgnoreCase(name)) {
                return list;
            }
        }
        return null;
    }

    /**
     * Find a category by name
     * @param name The name of the category
     * @return The MenuCategory or null
     */
    public MenuCategory menuCategoryFromName(String name) {
        int hash = elfHash(name);
        for (MenuCategory cat : menuCategories) {
            if (cat.hash() == hash && cat.name().equalsIgnoreCase(name)) {
                return cat;
            }
        }
        return null;
    }
    /**
     * Find a menu item by name
     * @param name The name of the menu item
     * @return The MenuItem or null
     */
    public MenuItem menuItemFromName(String name) {
        int hash = elfHash(name);
        for (MenuCategory cat : menuCategories) {
            for (MenuItem item: cat.menuItems()) {
                if (item.hash() == hash && item.name().equalsIgnoreCase(name)) {
                    return item;
                }
            }
        }
        return null;
    }
}
