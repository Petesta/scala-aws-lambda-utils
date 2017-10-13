package io.github.petesta.awslambda

trait ApiGatewayResponse

final case class CirceParseError(error: String) extends ApiGatewayResponse

final case class GenericError(error: String) extends ApiGatewayResponse

final case class Response[A](statusCode: Int, body: A)
