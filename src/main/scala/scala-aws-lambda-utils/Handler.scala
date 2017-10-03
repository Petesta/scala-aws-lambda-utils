package io.github.petesta.awslambda

import com.amazonaws.services.lambda.runtime.{ Context, RequestStreamHandler }
import io.circe.{ Decoder, Encoder }
import java.io.{ InputStream, OutputStream }
import scala.concurrent.{ Await, ExecutionContext, Future }
import scala.concurrent.duration.{ Duration, MILLISECONDS }

abstract class Handler[A, B](
  implicit decoder: Decoder[A],
  encoder: Encoder[Response[B]],
  parseErrDecoder: Encoder[Response[String]],
  errDecoder: Encoder[Response[Err]]
) extends RequestStreamHandler with Encoding {
  protected def handle(input: A): Response[B]
  private def handle(input: Response[String]) = input

  def handleRequest(is: InputStream, os: OutputStream, context: Context): Unit = {
    in(is) match {
      case Left(err) =>
        val request = Response(400, err.toString)
        out(handle(request), os)
        ()
      case Right(data) =>
        out(handle(data), os)
        ()
    }
  }
}

abstract class FutureHandler[A, B](time: Option[Duration] = None)(
  implicit decoder: Decoder[A],
  encoder: Encoder[Response[B]],
  parseErrDecoder: Encoder[Response[String]],
  errDecoder: Encoder[Response[Err]],
  ec: ExecutionContext
) extends RequestStreamHandler with Encoding {
  protected def handle(input: A): Future[Response[B]]
  private def handle(input: Response[String]) = input

  def handleRequest(is: InputStream, os: OutputStream, context: Context): Unit =
    in(is) match {
      case Left(err) =>
        val request = Response(400, err.toString)
        out(handle(request), os)
        ()
      case Right(data) =>
        val result = Await.result(
          handle(data),
          time.getOrElse(Duration(context.getRemainingTimeInMillis().toLong, MILLISECONDS))
        )
        out(result, os)
        ()
    }
}
