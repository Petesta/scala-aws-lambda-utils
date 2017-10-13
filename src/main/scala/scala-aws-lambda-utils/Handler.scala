package io.github.petesta.awslambda

import com.amazonaws.services.lambda.runtime.{ Context, RequestStreamHandler }
import io.circe.{ Decoder, Encoder }
import java.io.{ InputStream, OutputStream }
import scala.concurrent.{ Await, ExecutionContext, Future }
import scala.concurrent.duration.{ Duration, MILLISECONDS }

abstract class Handler[A <: ApiGatewayResponse, B <: ApiGatewayResponse](
  implicit decoder: Decoder[A],
  encoderA: Encoder[A],
  encoderB: Encoder[B],
  encoderReponse: Encoder[Response]
) extends RequestStreamHandler with Encoding {
  protected def handle(input: A): B

  def handleRequest(is: InputStream, os: OutputStream, context: Context): Unit = {
    in(is) match {
      case Left(err) =>
        error(err, os)
      case Right(json) =>
        val handledJson = handle(json)
        out(handledJson, os)
    }
    ()
  }
}

abstract class FutureHandler[A <: ApiGatewayResponse, B <: ApiGatewayResponse](
  time: Option[Duration] = None
)(
  implicit decoder: Decoder[A],
  encoderA: Encoder[A],
  encoderB: Encoder[B],
  encoderResponse: Encoder[Response],
  ec: ExecutionContext
) extends RequestStreamHandler with Encoding {
  protected def handle(input: A): Future[B]

  def handleRequest(is: InputStream, os: OutputStream, context: Context): Unit = {
    val validJson = in(is)
    validJson match {
      case Left(err) =>
        error(err, os)
      case Right(json) =>
        val result = Await.result(
          handle(json),
          time.getOrElse(Duration(context.getRemainingTimeInMillis().toLong, MILLISECONDS))
        )
        out(result, os)
    }
  }
}
