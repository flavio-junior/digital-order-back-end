package br.com.digital.order.exceptions

import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.UNAUTHORIZED)
class OperationUnauthorizedException(exception: String? = "") : AuthenticationException(exception)
