package org.work.com;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class Order {
    private String outletId;
    private Date orderDate;
    private List<Item> items;
}
