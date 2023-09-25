package ru.netology.nework.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
class BadContentTypeException : RuntimeException()
