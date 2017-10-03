package io.github.petesta.awslambda

import io.circe.{ Decoder, Encoder, Error }
import io.circe.parser._
import io.circe.syntax._
import java.io.{ InputStream, OutputStream }
import scala.io.Source

final case class Response[A](statusCode: Int, body: A)

final case class Err(error: String)

private[awslambda] trait Encoding {
  def in[A](is: InputStream)(implicit decoder: Decoder[A]): Either[Error, A] = {
    val rawJson = Source.fromInputStream(is).mkString
    val decodedJson = decode[A](rawJson)
    is.close()
    decodedJson
  }

  def out[A](response: Response[A], os: OutputStream)(
    implicit encoder: Encoder[Response[A]],
    errDecoder: Encoder[Response[Err]]
  ): Either[Response[Err], Response[A]] =
    try {
      os.write(response.asJson.noSpaces.getBytes("UTF-8"))
      Right(response)
    } catch {
      case e: Exception =>
        val error = Response(500, Err(e.toString))
        os.write(error.asJson.noSpaces.getBytes("UTF-8"))
        Left(error)
    } finally {
      os.close()
    }
}
