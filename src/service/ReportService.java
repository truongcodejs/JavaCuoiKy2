package service;

import model.Order;
import util.FileLogger;
import java.util.List;

public class ReportService {
    public void exportReport(List<Order> orders) {
        for (Order order : orders) {
            FileLogger.log("Order ID: " + order.getId() + ", User ID: " + order.getUserId() + ", Time: " + order.getOrderTime());
        }
    }
}