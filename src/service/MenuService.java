package service;

import dao.MenuDAO;
import model.MenuItem;

import java.util.List;

public class MenuService {
    private final MenuDAO menuDAO = new MenuDAO();

    public List<MenuItem> getAllMenuItems() {
        return menuDAO.getAllMenuItems();
    }
}
