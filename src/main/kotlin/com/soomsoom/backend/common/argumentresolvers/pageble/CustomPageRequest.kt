package com.soomsoom.backend.common.argumentresolvers.pageble

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

class CustomPageRequest protected constructor(
    pageNumber: Int,
    pageSize: Int,
    sort: Sort,
) : PageRequest(pageNumber, pageSize, sort) {
    override fun getPageNumber(): Int {
        return super.getPageNumber() + 1
    }
    companion object {
        fun of(page: Int, size: Int, sort: Sort): CustomPageRequest {
            return CustomPageRequest(page, size, sort)
        }
    }
}
