package io.github.petesta.awslambda

sealed trait ApiGatewayResponse

final case class GenericError(error: String) extends ApiGatewayResponse

final case class Response[A](statusCode: Int, body: A)
