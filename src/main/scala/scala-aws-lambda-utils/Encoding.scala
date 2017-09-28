package io.github.petesta.awslambda

import io.circe.{ Decoder, Encoder, Error }
import io.circe.parser._
import io.circe.syntax._
import java.io.{ InputStream, OutputStream }

final case class Response[A](statusCode: Int, body: A)

private[awslambda] trait Encoding {
  def in[A](is: InputStream)(implicit decoder: Decoder[A]): Either[Error, A] = {
    val t = decode[A](scala.io.Source.fromInputStream(is).mkString)
    is.close()
    t
  }

  def out[A, B](response: Response[A], error: Response[B], os: OutputStream)(
    implicit encoder: Encoder[Response[A]],
    errorEncoder: Encoder[Response[B]]
  ): Either[Response[B], Response[A]] =
    try {
      os.write(response.asJson.noSpaces.getBytes("UTF-8"))
      Right(response)
    } catch {
      case e: Exception =>
        os.write(error.asJson.noSpaces.getBytes("UTF-8"))
        Left(error)
    } finally {
      os.close()
    }
}
