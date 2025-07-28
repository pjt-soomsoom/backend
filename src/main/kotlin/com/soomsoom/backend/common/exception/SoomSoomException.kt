package com.soomsoom.backend.common.exception

class SoomSoomException(
    val errorCode: ErrorCode,
) : RuntimeException()
