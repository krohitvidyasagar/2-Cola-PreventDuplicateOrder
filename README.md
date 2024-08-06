# Order Prevention

## Special Note
This implementation is **not a scalable solution**. In a typical production environment, the server would generate a
unique `transaction_id` before the API to place an order is called. This `transaction_id` would be provided to the
client, and the client would include it in their order request. The server would then reject any subsequent orders with
the same `transaction_id` to prevent duplicate entries.


## Overview

This Java program processes and validates orders based on predefined criteria. It reads existing orders from a JSON file, validates a new order, and manages adding the new order to an internal map if it meets the criteria.

## Components

1. **Main Class**: Contains the entry point of the program and handles the following tasks:
    - Reading and processing existing orders from `orderList.json`.
    - Reading a new order from `newOrder.json`.
    - Validating whether the new order can be added to the existing orders.
    - Adding the new order to the internal map if valid and printing appropriate messages.

2. **Order Processing Logic**:
    - **Reading Existing Orders**: The `getAllProcessedOrders` method reads and parses the existing orders from a JSON file, sorts them by date, and stores them in a map grouped by outlet IDs.
    - **Reading New Order**: The `readOrderFromFile` method reads a new order from a JSON file and creates an `Order` object.
    - **Validating New Order**: The `isValidOrder` method determines if the new order can be added to the map by:
        - Checking if the outlet ID exists in the map.
        - Comparing the timestamp of the new order with the last order's timestamp for the same outlet.
        - Verifying if the new order's items are identical to the last order's items within a 15-minute window.
    - **Adding Order to Map**: The `addOrderToMap` method adds the new order to the map and prints a success message if valid.

## JSON File Formats

- **`orderList.json`**: Contains a list of existing orders. Each order has fields for `outletId`, `orderDate`, and `item`.
- **`newOrder.json`**: Contains a new order to be validated and potentially added. Includes fields for `outletId`, `totalOrderValue`, and an array of `item`.

## How to Run

1. Ensure the JSON files `orderList.json` and `newOrder.json` are placed in the `./src/main/resources/` directory.
2. Compile and run the `Main` class. The program will:
    - Load existing orders from `orderList.json`.
    - Read a new order from `newOrder.json`.
    - Validate and attempt to add the new order.
    - Print messages indicating whether the order was successfully added or not.

## Dependencies

- **JSON Library**: The program uses the `org.json` library for JSON parsing.
