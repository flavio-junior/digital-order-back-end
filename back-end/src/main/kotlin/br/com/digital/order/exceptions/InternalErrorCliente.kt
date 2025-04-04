package br.com.digital.order.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST)
class InternalErrorClient(message: String) : RuntimeException(message)
