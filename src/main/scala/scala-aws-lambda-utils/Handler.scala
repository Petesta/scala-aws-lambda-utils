package io.github.petesta.awslambda

import com.amazonaws.services.lambda.runtime.{ Context, RequestStreamHandler }
import io.circe.{ Decoder, Encoder }
import java.io.{ InputStream, OutputStream }
import scala.concurrent.{ Await, ExecutionContext, Future }
import scala.concurrent.duration.{ Duration, MILLISECONDS }

abstract class Handler[A, B, C](error: Response[C])(
  implicit decoder: Decoder[A],
  encoder: Encoder[Response[B]],
  errorEncoder: Encoder[Response[C]]
) extends RequestStreamHandler with Encoding {
  protected def handler(input: A, context: Context): Response[B]

  def handleRequest(is: InputStream, os: OutputStream, context: Context): Unit =
    in(is).right.flatMap(data => out(handler(data, context), error, os)) match {
      case Left(_) =>
        ()
      case Right(_) =>
        ()
    }
}

abstract class FutureHandler[A, B, C](error: Response[C], time: Option[Duration] = None)(
  implicit decoder: Decoder[A],
  encoder: Encoder[Response[B]],
  errorEncoder: Encoder[Response[C]],
  ec: ExecutionContext
) extends RequestStreamHandler with Encoding {
  protected def handler(input: A, context: Context): Future[Response[B]]

  def handleRequest(is: InputStream, os: OutputStream, context: Context): Unit =
    in(is).right.flatMap { json =>
      val result = Await.result(
        handler(json, context),
        time.getOrElse(Duration(context.getRemainingTimeInMillis().toLong, MILLISECONDS))
      )
      out(result, error, os)
    } match {
      case Left(_) =>
        ()
      case Right(_) =>
        ()
    }
}
