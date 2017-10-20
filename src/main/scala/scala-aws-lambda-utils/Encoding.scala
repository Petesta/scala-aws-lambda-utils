package io.github.petesta.awslambda

import io.circe.{ Decoder, Encoder, Error }
import io.circe.parser._
import io.circe.syntax._
import java.io.{ InputStream, OutputStream }
import scala.io.Source

final case class Response[A](statusCode: Int, body: A)

private[awslambda] trait Encoding {
  def in[A](is: InputStream)(implicit decoder: Decoder[A]): Either[Error, A] = {
    val rawJson = Source.fromInputStream(is).mkString
    val decodedJson = decode[A](rawJson)
    is.close()
    decodedJson
  }

  def error(err: Error, os: OutputStream)(implicit encoder: Encoder[Response[String]]): Unit =
    try {
      val response = Response(400, err.toString)
      os.write(response.asJson.noSpaces.getBytes("UTF-8"))
    } catch {
      case e: Exception =>
        val response = Response(500, e.toString)
        os.write(response.asJson.noSpaces.getBytes("UTF-8"))
    }

  def out[A](input: A, os: OutputStream)(
    implicit encoder: Encoder[A],
    encoderGenericError: Encoder[Response[String]]
  ): Unit =
    try {
      os.write(input.asJson.noSpaces.getBytes("UTF-8"))
    } catch {
      case e: Exception =>
        val response = Response(500, e.toString)
        os.write(response.asJson.noSpaces.getBytes("UTF-8"))
    }
}
