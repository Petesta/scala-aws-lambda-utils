package io.github.petesta.awslambda

import com.amazonaws.services.lambda.runtime.{ Context, RequestStreamHandler }
import io.circe.{ Decoder, Encoder }
import java.io.{ InputStream, OutputStream }
import scala.concurrent.{ Await, ExecutionContext, Future }
import scala.concurrent.duration.{ Duration, MILLISECONDS }

abstract class Handler[A, C <: HandlerError, B](
  implicit decoder: Decoder[A],
  bEncoder: Encoder[Response[B]],
  cEncoder: Encoder[Response[C]],
  circeParseErrEncoder: Encoder[Response[CirceParseError]],
  outputStreamErrDecoder: Encoder[Response[OutputStreamError]]
) extends RequestStreamHandler with Encoding {
  protected def handle(input: Either[HandlerError, A]): Either[Response[C], Response[B]]

  def handleRequest(is: InputStream, os: OutputStream, context: Context): Unit = {
    val validJson = in(is)
    val response = handle(validJson)
    out(validJson, response, os)
    ()
  }
}

abstract class FutureHandler[A, C <: HandlerError, B](time: Option[Duration] = None)(
  implicit decoder: Decoder[A],
  bEncoder: Encoder[Response[B]],
  cEncoder: Encoder[Response[C]],
  circeParseErrEncoder: Encoder[Response[CirceParseError]],
  outputStreamErrDecoder: Encoder[Response[OutputStreamError]],
  ec: ExecutionContext
) extends RequestStreamHandler with Encoding {
  protected def handle(input: Either[HandlerError, A]): Future[Either[Response[C], Response[B]]]

  def handleRequest(is: InputStream, os: OutputStream, context: Context): Unit = {
    val validJson = in(is)
    val response = handle(validJson)
    val result = Await.result(
      response,
      time.getOrElse(Duration(context.getRemainingTimeInMillis().toLong, MILLISECONDS))
    )
    out(validJson, result, os)
    ()
  }
}
