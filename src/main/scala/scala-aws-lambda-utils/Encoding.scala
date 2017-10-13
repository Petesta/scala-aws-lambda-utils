package io.github.petesta.awslambda

import io.circe.{ Decoder, Encoder, Error }
import io.circe.parser._
import io.circe.syntax._
import java.io.{ InputStream, OutputStream }
import scala.io.Source

private[awslambda] trait Encoding {
  def in[A](is: InputStream)(implicit decoder: Decoder[A]): Either[Error, A] = {
    val rawJson = Source.fromInputStream(is).mkString
    val decodedJson = decode[A](rawJson)
    is.close()
    decodedJson
  }

  def error(err: Error, os: OutputStream)(implicit encoder: Encoder[Response]): Unit =
    try {
      val response = Response(400, CirceParseError(err.toString))
      os.write(response.asJson.noSpaces.getBytes("UTF-8"))
    } catch {
      case e: Exception =>
        val response = Response(500, GenericError(e.toString))
        os.write(response.asJson.noSpaces.getBytes("UTF-8"))
    }

  def out[A <: ApiGatewayResponse](input: A, os: OutputStream)(
    implicit encoder: Encoder[A],
    responseEncoder: Encoder[Response]
  ): Unit =
    try {
      os.write(input.asJson.noSpaces.getBytes("UTF-8"))
    } catch {
      case e: Exception =>
        val response = Response(500, GenericError(e.toString))
        os.write(response.asJson.noSpaces.getBytes("UTF-8"))
    }
}
