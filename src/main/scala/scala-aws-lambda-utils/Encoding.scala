package io.github.petesta.awslambda

import io.circe.{ Decoder, Encoder }
import io.circe.parser._
import io.circe.syntax._
import java.io.{ InputStream, OutputStream }
import scala.io.Source

private[awslambda] trait Encoding {
  def in[A](is: InputStream)(implicit decoder: Decoder[A]): Either[CirceParseError, A] = {
    val rawJson = Source.fromInputStream(is).mkString
    val decodedJson = decode[A](rawJson)
    is.close()
    decodedJson.left.map(l => CirceParseError(l.toString))
  }

  def out[A, C <: HandlerError, B](
    validJson: Either[CirceParseError, A],
    response: Either[Response[C], Response[B]],
    os: OutputStream
  )(
    implicit bEncoder: Encoder[Response[B]],
    cEncoder: Encoder[Response[C]],
    circeParseErrEncoder: Encoder[Response[CirceParseError]],
    outputStreamErrEncoder: Encoder[Response[OutputStreamError]]
  ): Either[Response[HandlerError], Response[B]] =
    try {
      validJson match {
        case l @ Left(invalidJson) =>
          val invalidJsonResponse = Response(400, invalidJson)
          os.write(invalidJsonResponse.asJson.noSpaces.getBytes("UTF-8"))
          val modifyLeft = l.left.map(_ => invalidJsonResponse)
          modifyLeft.right.map(_ => response.right.get)
        case Right(_) =>
          response match {
            case l @ Left(error) =>
              os.write(error.asJson.noSpaces.getBytes("UTF-8"))
              l
            case r @ Right(success) =>
              os.write(success.asJson.noSpaces.getBytes("UTF-8"))
              r
          }
      }
    } catch {
      case e: Exception =>
        val error = Response(500, OutputStreamError(e.toString))
        os.write(error.asJson.noSpaces.getBytes("UTF-8"))
        Left(error)
    } finally {
      os.close()
    }
}
