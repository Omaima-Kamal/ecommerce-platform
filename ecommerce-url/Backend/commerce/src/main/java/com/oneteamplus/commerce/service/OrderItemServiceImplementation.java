package com.oneteamplus.commerce.service;

import com.oneteamplus.commerce.model.OrderItem;
import com.oneteamplus.commerce.repository.OrderItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OrderItemServiceImplementation implements OrderItemService {

    private static final Logger log = LoggerFactory.getLogger(OrderItemServiceImplementation.class);

    private final OrderItemRepository orderItemRepository;

    public OrderItemServiceImplementation(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    public OrderItem createOrderItem(OrderItem orderItem) {
        OrderItem saved = orderItemRepository.save(orderItem);
        log.info("Created order item id={} productId={} size={} quantity={} price={}",
                saved.getId(),
                saved.getProduct() != null ? saved.getProduct().getId() : null,
                saved.getSize(),
                saved.getQuantity(),
                saved.getPrice());
        return saved;
    }
}