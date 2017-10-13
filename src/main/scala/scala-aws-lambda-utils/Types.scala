package io.github.petesta.awslambda

// import io.circe.generic.JsonCodec

// @JsonCodec trait ApiGatewayResponse
trait ApiGatewayResponse

final case class CirceParseError(error: String) extends ApiGatewayResponse

final case class GenericError(error: String) extends ApiGatewayResponse

final case class Request(body: String) extends ApiGatewayResponse

final case class Response(statusCode: Int, body: ApiGatewayResponse)

final case class Output(message: String) extends ApiGatewayResponse

// final case class CirceParseError(error: String)
//
// final case class GenericError(error: String)
//
// final case class Response[A <: ApiGatewayResponse](statusCode: Int, body: A)
