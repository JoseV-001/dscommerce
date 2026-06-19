package com.josev001.dscommerce.dto;

import com.josev001.dscommerce.entities.Order;
import com.josev001.dscommerce.entities.OrderItem;
import com.josev001.dscommerce.entities.OrderStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class OrderDTO {

    private Long id;
    private Instant moment;
    private OrderStatus orderStatus;

    private ClientDTO client;

    private PaymentDTO payment;

    private List<OrderItemDTO> items = new ArrayList<>();

    public OrderDTO(){

    }

    public OrderDTO(Long id, Instant moment, OrderStatus orderStatus, ClientDTO client, PaymentDTO payment) {
        this.id = id;
        this.moment = moment;
        this.orderStatus = orderStatus;
        this.client = client;
        this.payment = payment;
    }

    public OrderDTO(Order entity) {
        id = entity.getId();
        moment = entity.getMoment();
        orderStatus = entity.getStatus();
        client = new ClientDTO(entity.getClient());
        payment = (entity.getPayment() == null) ? null : new PaymentDTO(entity.getPayment());
        for (OrderItem item : entity.getItems()) {
            OrderItemDTO itemDTO = new OrderItemDTO(item);
            items.add(itemDTO);
        }
    }

    public Long getId() {
        return id;
    }

    public Instant getMoment() {
        return moment;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public ClientDTO getClient() {
        return client;
    }

    public PaymentDTO getPayment() {
        return payment;
    }

    public List<OrderItemDTO> getItems() {
        return items;
    }

    public Double getTotal() {
        double sum = 0.0;
        for (OrderItemDTO item : items) {
            sum = sum + item.getSubTotal();
        }
        return sum;
    }
}
