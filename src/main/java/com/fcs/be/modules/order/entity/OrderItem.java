package com.fcs.be.modules.order.entity;

import com.fcs.be.common.entity.SoftDeleteEntity;
import com.fcs.be.modules.product.entity.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "order_items")
public class OrderItem extends SoftDeleteEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "sku_snapshot", nullable = false, length = 120)
    private String skuSnapshot;

    @Column(name = "product_name_snapshot", nullable = false, length = 255)
    private String productNameSnapshot;

    @Column(name = "condition_snapshot", length = 50)
    private String conditionSnapshot;

    @Column(name = "price_at_purchase", nullable = false, precision = 19, scale = 4)
    private BigDecimal priceAtPurchase;
}
