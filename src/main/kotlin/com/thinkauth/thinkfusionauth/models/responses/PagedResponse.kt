package com.thinkauth.thinkfusionauth.models.responses

class PagedResponse<T>(
    var item: T,
    var curentPage: Int,
    var totalItems: Long,
    var totalPages: Int
) {
}