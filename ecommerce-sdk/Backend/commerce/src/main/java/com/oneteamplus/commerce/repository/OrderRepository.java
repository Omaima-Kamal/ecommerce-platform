package com.oneteamplus.commerce.repository;

import com.oneteamplus.commerce.model.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = {"user", "shippingAddress", "orderItems", "orderItems.product"})
    @Query("""
        SELECT o
        FROM Order o
        WHERE o.user.id = :userId
          AND (
              o.orderStatus = 'PLACED'
              OR o.orderStatus = 'CONFIRMED'
              OR o.orderStatus = 'SHIPPED'
              OR o.orderStatus = 'DELIVERED'
          )
        """)
    List<Order> getUsersOrders(@Param("userId") Long userId);

    @EntityGraph(attributePaths = {"user", "shippingAddress", "orderItems", "orderItems.product", "paymentDetails"})
    Order findByOrderId(String orderId);

    @EntityGraph(attributePaths = {"user", "shippingAddress", "orderItems", "orderItems.product"})
    @Override
    Optional<Order> findById(Long id);
}
