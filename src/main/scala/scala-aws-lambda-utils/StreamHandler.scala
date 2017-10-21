package io.github.petesta.awslambda

import com.amazonaws.services.lambda.runtime.{ Context, RequestStreamHandler }
import io.circe.{ Decoder, Encoder }
import java.io.{ InputStream, OutputStream }
import scala.concurrent.{ Await, ExecutionContext, Future }
import scala.concurrent.duration.{ Duration, MILLISECONDS }

abstract class StreamHandler[A, B](
  implicit decoder: Decoder[A],
  encoderA: Encoder[A],
  encoderB: Encoder[B],
  encoderReponse: Encoder[Response[B]],
  encoderGeneric: Encoder[Response[GenericError]]
) extends RequestStreamHandler with Encoding {
  protected def handle(input: A): Response[B]

  def handleRequest(is: InputStream, os: OutputStream, context: Context): Unit =
    try {
      in(is) match {
        case Left(err) =>
          val response = Response(400, GenericError(err.toString))
          out(response, os)
        case Right(json) =>
          out(handle(json), os)
      }
    } catch {
      case e: Exception =>
        val response = Response(500, GenericError(e.toString))
        out(response, os)
    }
}

abstract class FutureStreamHandler[A, B](time: Option[Duration] = None)(
  implicit decoder: Decoder[A],
  encoderA: Encoder[A],
  encoderB: Encoder[B],
  encoderResponse: Encoder[Response[B]],
  encoderGeneric: Encoder[Response[GenericError]],
  ec: ExecutionContext
) extends RequestStreamHandler with Encoding {
  protected def handle(input: A): Future[Response[B]]

  def handleRequest(is: InputStream, os: OutputStream, context: Context): Unit =
    try {
      in(is) match {
        case Left(err) =>
          val response = Response(400, GenericError(err.toString))
          out(response, os)
        case Right(json) =>
          val result = Await.result(
            handle(json),
            time.getOrElse(Duration(context.getRemainingTimeInMillis().toLong, MILLISECONDS))
          )
          out(result, os)
      }
    } catch {
      case e: Exception =>
        val response = Response(500, GenericError(e.toString))
        out(response, os)
    }
}
