package io.github.petesta.awslambda

trait HandlerError

final case class CirceParseError(message: String) extends HandlerError

final case class OutputStreamError(message: String) extends HandlerError

final case class Response[+A](statusCode: Int, body: A)
