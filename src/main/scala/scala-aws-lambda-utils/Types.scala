package io.github.petesta.awslambda

trait HandlerError

final case class CirceParseError(error: String) extends HandlerError

final case class OutputStreamError(error: String) extends HandlerError

final case class Response[+A](statusCode: Int, body: A)
