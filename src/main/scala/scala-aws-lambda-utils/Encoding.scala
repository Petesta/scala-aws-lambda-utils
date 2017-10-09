package io.github.petesta.awslambda

import io.circe.{ Decoder, Encoder }
import io.circe.parser._
import io.circe.syntax._
import java.io.{ InputStream, OutputStream }
import scala.io.Source

trait HandlerError

final case class CirceParseError(message: String) extends HandlerError

final case class Response[A](statusCode: Int, body: A)

final case class OutputStreamError(message: String) extends HandlerError

private[awslambda] trait Encoding {
  def in[A](is: InputStream)(implicit decoder: Decoder[A]): Either[HandlerError, A] = {
    val rawJson = Source.fromInputStream(is).mkString
    val decodedJson = decode[A](rawJson)
    is.close()
    decodedJson.left.map(l => CirceParseError(l.toString))
  }

  def out[A, B <: HandlerError](response: Either[Response[B], Response[A]], os: OutputStream)(
    implicit encoder: Encoder[Response[A]],
    doutputStreamErrDecoder: Encoder[Response[B]]
    // sdoutputStreamErrDecoder: Encoder[Response[OutputStreamError]]
  ): Either[Response[B], Response[A]] =
    try {
      response match {
        case l @ Left(error) =>
          os.write(error.asJson.noSpaces.getBytes("UTF-8"))
          l
        case r @ Right(success) =>
          os.write(success.asJson.noSpaces.getBytes("UTF-8"))
          r
      }
    // } catch {
      // case e: Exception =>
      //   val error = Response(500, OutputStreamError(e.toString))
      //   os.write(error.asJson.noSpaces.getBytes("UTF-8"))
      //   Left(error)
    } finally {
      os.close()
    }
}
