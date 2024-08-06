package org.work.com;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        String orderListPath = "./src/main/resources/orderList.json";
        String newOrderPath = "./src/main/resources/newOrder.json";

        // Processing all the past orders and storing them in a map
        Map<String, List<Order>> ordersMap = getAllProcessedOrders(orderListPath);

        // Read a new order from newOrder.json
        Order newOrder = readOrderFromFile(newOrderPath);

        // Check if the new order can be added
        boolean canOrderBeAdded = isValidOrder(ordersMap, newOrder);

        if (canOrderBeAdded) {
            // If the order can be added, add it to the map
            addOrderToMap(ordersMap, newOrder);
            System.out.println("Order was added successfully.");
        } else {
            System.out.println("Order could not be added since order already exists.");
        }

        // Now try adding the same order again and it will fail
        canOrderBeAdded = isValidOrder(ordersMap, newOrder);

        if (canOrderBeAdded) {
            addOrderToMap(ordersMap, newOrder);
            System.out.println("Order was added successfully.");
        } else {
            System.out.println("Order could not be added since order already exists.");
        }

    }

    private static Map<String, List<Order>> getAllProcessedOrders(String filePath) {
        Map<String, List<Order>> ordersMap = new HashMap<>();

        try {
            // Read the JSON file content
            String content = new String(Files.readAllBytes(Paths.get(filePath)));

            // Parse JSON content
            JSONArray jsonArray = new JSONArray(content);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String outletId = jsonObject.getString("outletId");
                Date orderDate = new Date(); // Assign current timestamp
                JSONArray itemsArray = jsonObject.getJSONArray("item");

                List<Item> items = new ArrayList<>();
                for (int j = 0; j < itemsArray.length(); j++) {
                    JSONObject itemObject = itemsArray.getJSONObject(j);
                    String upcCode = itemObject.getString("UPC_Code");
                    int qty = itemObject.getInt("qty");
                    items.add(new Item(upcCode, qty));
                }

                Order order = new Order(outletId, orderDate, items);
                // Add the order to the map
                ordersMap.computeIfAbsent(outletId, k -> new ArrayList<>()).add(order);
            }

            // Sort the orders for each outlet by orderDate in descending order
            for (List<Order> orders : ordersMap.values()) {
                orders.sort(Comparator.comparing(Order::getOrderDate));
            }

        } catch (IOException e) {
            System.out.println("Main.getAllProcessedOrders(): Error occurred getting order list " + e.getMessage());
        }

        return ordersMap;
    }

    private static Order readOrderFromFile(String filePath) {
        Order order = null;

        try {
            // Read the JSON file content
            String content = new String(Files.readAllBytes(Paths.get(filePath)));

            // Parse JSON content
            JSONObject jsonObject = new JSONObject(content);

            String outletId = jsonObject.getString("outletId");
            Date orderDate = new Date(); // Assign current timestamp
            JSONArray itemsArray = jsonObject.getJSONArray("item");

            List<Item> items = new ArrayList<>();
            for (int j = 0; j < itemsArray.length(); j++) {
                JSONObject itemObject = itemsArray.getJSONObject(j);
                String upcCode = itemObject.getString("UPC_Code");
                int qty = itemObject.getInt("qty");
                items.add(new Item(upcCode, qty));
            }

            order = new Order(outletId, orderDate, items);

        } catch (IOException e) {
            System.out.println("Main.readOrderFromFile(): Error occurred reading order file " + e.getMessage());
        }

        return order;
    }

    private static boolean isValidOrder(Map<String, List<Order>> ordersMap, Order newOrder) {
        String outletId = newOrder.getOutletId();
        Date newOrderDate = newOrder.getOrderDate();

        // If the outletID does not exist return true
        if (!ordersMap.containsKey(outletId)) {
            return true;
        }

        List<Order> outletOrders = ordersMap.get(outletId);
        Order lastOrder = outletOrders.getLast();
        Date lastOrderDate = lastOrder.getOrderDate();

        long timeDifference = newOrderDate.getTime() - lastOrderDate.getTime();
        long timeDifferenceInMinutes = timeDifference / (1000 * 60);

        // Keeping the cut-off as 24 hours
        if (timeDifferenceInMinutes > 24 * 60) {
            return true;
        }

        List<Item> newOrderItems = newOrder.getItems();
        List<Item> lastOrderItems = lastOrder.getItems();

        if (newOrderItems.size() != lastOrderItems.size()) {
            return true;
        }

        for (int i = 0; i < newOrderItems.size(); i++) {
            Item newItem = newOrderItems.get(i);
            Item lastItem = lastOrderItems.get(i);

            if (!newItem.getUpcCode().equals(lastItem.getUpcCode()) || newItem.getQty() != lastItem.getQty()) {
                return true;
            }
        }

        return false;
    }

    private static void addOrderToMap(Map<String, List<Order>> ordersMap, Order newOrder) {
        ordersMap.computeIfAbsent(newOrder.getOutletId(), k -> new ArrayList<>()).add(newOrder);
    }

}
