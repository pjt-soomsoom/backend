package com.soomsoom.backend.adapter.out.persistence.item.repository.jpa.entity

import com.soomsoom.backend.common.entity.BaseTimeEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "collections")
class CollectionJpaEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    var name: String,

    @Column(columnDefinition = "TEXT")
    var description: String?,

    var phrase: String?,

    var basePrice: Int,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    var itemIds: List<Long>,
) : BaseTimeEntity(){
}
