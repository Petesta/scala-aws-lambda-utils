package io.github.petesta.awslambda

import com.amazonaws.services.lambda.runtime.{ Context, RequestStreamHandler }
import io.circe.{ Decoder, Encoder }
import java.io.{ InputStream, OutputStream }
// import scala.concurrent.{ Await, ExecutionContext, Future }
// import scala.concurrent.duration.{ Duration, MILLISECONDS }

abstract class Handler[A, B](
  implicit decoder: Decoder[A],
  encoder: Encoder[Response[B]],
  handlerErrDecoder: Encoder[Response[HandlerError]]
  // outputStreamErrDecoder: Encoder[Response[OutputStreamError]]
) extends RequestStreamHandler with Encoding {
  protected def handle(input: Either[HandlerError, A]): Either[Response[HandlerError], Response[B]]

  def handleRequest(is: InputStream, os: OutputStream, context: Context): Unit = {
    out(handle(in(is)), os)
    ()
  }
}

// abstract class FutureHandler[A, B](time: Option[Duration] = None)(
//   implicit decoder: Decoder[A],
//   encoder: Encoder[Response[B]],
//   handlerErrDecoder: Encoder[Response[HandlerError]],
//   outputStreamErrDecoder: Encoder[Response[OutputStreamError]],
//   ec: ExecutionContext
// ) extends RequestStreamHandler with Encoding {
//   protected def handle(input: Either[HandlerError, A]): Future[Response[B]]
//
//   def handleRequest(is: InputStream, os: OutputStream, context: Context): Unit =
//     in(is) match {
//       case l @ Left(_) =>
//         val result = Await.result(
//           handle(l),
//           time.getOrElse(Duration(context.getRemainingTimeInMillis().toLong, MILLISECONDS))
//         )
//         out(result, os)
//         ()
//       case d @ Right(_) =>
//         val result = Await.result(
//           handle(d),
//           time.getOrElse(Duration(context.getRemainingTimeInMillis().toLong, MILLISECONDS))
//         )
//         out(result, os)
//         ()
//     }
// }
