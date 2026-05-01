package com.fcs.be.modules.product.repository;

import com.fcs.be.common.enums.ProductStatus;
import com.fcs.be.modules.product.dto.request.ProductFilterRequest;
import com.fcs.be.modules.product.entity.Product;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecification {

    private ProductSpecification() {}

    public static Specification<Product> from(ProductFilterRequest filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.isFalse(root.get("isDeleted")));

            if (filter.keyword() != null && !filter.keyword().isBlank()) {
                String pattern = "%" + filter.keyword().toLowerCase() + "%";
                predicates.add(cb.or(
                    cb.like(cb.lower(root.get("name")), pattern),
                    cb.like(cb.lower(root.get("description")), pattern),
                    cb.like(cb.lower(root.get("sku")), pattern)
                ));
            }

            if (filter.brandId() != null) {
                predicates.add(cb.equal(root.get("brand").get("id"), filter.brandId()));
            }

            if (filter.categoryId() != null) {
                var categoryJoin = root.join("productCategories");
                predicates.add(cb.equal(categoryJoin.get("category").get("id"), filter.categoryId()));
            }

            if (filter.minPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("salePrice"), filter.minPrice()));
            }

            if (filter.maxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("salePrice"), filter.maxPrice()));
            }

            if (filter.minCondition() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("conditionPercent"), filter.minCondition()));
            }

            if (filter.maxCondition() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("conditionPercent"), filter.maxCondition()));
            }

            if (filter.status() != null) {
                predicates.add(cb.equal(root.get("status"), filter.status()));
            }

            if (query != null) {
                query.distinct(true);
                query.orderBy(cb.desc(root.get("createdAt")));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
