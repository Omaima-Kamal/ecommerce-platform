package com.oneteamplus.commerce.repository;

import com.oneteamplus.commerce.model.Cart;
import com.oneteamplus.commerce.model.CartItem;
import com.oneteamplus.commerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @Query("SELECT ci From CartItem ci Where ci.cart=:cart And ci.product=:product And ci.size=:size And ci.userId=:userId")
    public CartItem isCartItemExist(@Param("cart")Cart cart, @Param("product")Product product, @Param("size")String size,
        @Param("userId")Long userId);

    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart.id = (SELECT c.id FROM Cart c WHERE c.user.id = :userId)")
    int deleteByCartUserId(@Param("userId") Long userId);
}

