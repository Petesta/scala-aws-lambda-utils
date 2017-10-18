package io.github.petesta.awslambda

final case class GenericError(error: String)

final case class Response[A](statusCode: Int, body: A)
