package io.github.petesta.awslambda

import com.amazonaws.services.lambda.runtime.{Context, RequestStreamHandler}
import io.circe.{Decoder, Encoder}
import java.io.{InputStream, OutputStream}
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration.{Duration, MILLISECONDS}

abstract class Handler[A, B](implicit decoder: Decoder[A], encoder: Encoder[B]) extends RequestStreamHandler {
  import Encoding._

  protected def handler(input: A, context: Context): B

  def handleRequest(is: InputStream, os: OutputStream, context: Context): Unit =
    input(is).right.flatMap(data => output(handler(data, context), os)) match {
      case Left(_) =>
        ()
      case Right(_) =>
        ()
    }
}

abstract class FutureHandler[A, B](time: Option[Duration] = None)(
  implicit decoder: Decoder[A],
  encoder: Encoder[B],
  ec: ExecutionContext
) extends Handler[A, B] {
  protected def handlerFuture(input: A, context: Context): Future[B]

  protected def handleRequest(input: A, context: Context): B =
    Await.result(
      handlerFuture(input, context),
      time.getOrElse(Duration(context.getRemainingTimeInMillis().toLong, MILLISECONDS))
    )
}
