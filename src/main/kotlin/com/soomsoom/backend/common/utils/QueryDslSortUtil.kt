package com.soomsoom.backend.common.utils

import com.querydsl.core.types.Expression
import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.PathBuilder
import org.springframework.data.domain.Sort

object QueryDslSortUtil {
    fun toOrderSpecifiers(sort: Sort, entityClass: Class<*>): List<OrderSpecifier<*>> {
        val pathBuilder = PathBuilder(entityClass, entityClass.simpleName.replaceFirstChar { it.lowercase() })
        return sort.map { order ->
            val property = order.property
            val direction = if (order.isAscending) Order.ASC else Order.DESC
            val path = pathBuilder.get(property) as Expression<Comparable<*>>
            OrderSpecifier(direction, path)
        }.toList()
    }
}
