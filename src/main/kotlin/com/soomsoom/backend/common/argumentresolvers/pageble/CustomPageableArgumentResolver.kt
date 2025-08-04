package com.soomsoom.backend.common.argumentresolvers.pageble

import org.springframework.core.MethodParameter
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import java.awt.print.Pageable

@Component
class CustomPageableArgumentResolver : HandlerMethodArgumentResolver{

    companion object {
        private const val DEFAULT_PAGE_SIZE = 12
        private const val DEFAULT_SORT_FIELD = "createdAt"
        private const val DEFAULT_SORT_DIRECTION = "DESC"

        private const val PARAM_PAGE = "page"
        private const val PARAM_SIZE = "size"
        private const val PARAM_SORT = "sort"
    }
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return Pageable::class.java == parameter.parameterType
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): Any? {
        val page = getPage(webRequest).let { if (it < 0) 0 else it }
        val size = getSize(webRequest)
        val sort = getSort(webRequest)
        return CustomPageRequest.of(page, size, sort)
    }

    /**
     * 'page' 쿼리 파라미터를 읽어 0부터 시작하는 페이지 번호로 변환합니다.
     * 예: /api?page=1 -> 0, /api?page=2 -> 1
     * 파라미터가 없거나 숫자가 아니면 0을 반환합니다.
     */
    private fun getPage(webRequest: NativeWebRequest): Int {
        return webRequest.getParameter(PARAM_PAGE)?.toIntOrNull()?.minus(1) ?: 0
    }

    /**
     * 'size' 쿼리 파라미터를 읽습니다.
     * 파라미터가 없거나 숫자가 아니면 기본값(DEFAULT_PAGE_SIZE)을 사용합니다.
     */
    private fun getSize(webRequest: NativeWebRequest): Int {
        return webRequest.getParameter(PARAM_SIZE)?.toIntOrNull() ?: DEFAULT_PAGE_SIZE
    }

    /**
     * 'sort' 쿼리 파라미터를 읽어 Sort 객체를 생성합니다.
     * 예: /api?sort=createdAt,desc&sort=name,asc
     * 여러 개의 sort 파라미터를 모두 처리할 수 있습니다.
     */
    private fun getSort(webRequest: NativeWebRequest): Sort {
        val sortParams = webRequest.getParameterValues(PARAM_SORT)
            ?: return Sort.by(Sort.Direction.DESC, DEFAULT_SORT_FIELD) // 파라미터가 없으면 기본 정렬

        val orders = sortParams.mapNotNull { param ->
            if (param.isBlank()) return@mapNotNull null // 비어있는 파라미터는 무시

            val parts = param.split(",")
            val field = parts[0]
            // 정렬 방향이 없으면 DESC를 기본값으로 사용
            val direction = parts.getOrNull(1)?.let { Sort.Direction.fromString(it) } ?: Sort.Direction.DESC

            Sort.Order(direction, field)
        }

        return if (orders.isEmpty()) Sort.by(Sort.Direction.DESC, DEFAULT_SORT_FIELD) else Sort.by(orders)
    }
}
