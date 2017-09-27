package io.github.petesta.awslambda

import io.circe.{Decoder, Encoder, Error}
import io.circe.parser._
import io.circe.syntax._
import java.io.{InputStream, OutputStream}

private[awslambda] trait Encoding {
  def input[A](is: InputStream)(implicit decoder: Decoder[A]): Either[Error, A] = {
    val t = decode[A](scala.io.Source.fromInputStream(is).mkString)
    is.close()
    t
  }

  def output[A](value: A, os: OutputStream)(implicit encoder: Encoder[A]): Either[Exception, A] =
    try {
      os.write(value.asJson.noSpaces.getBytes("UTF-8"))
      Right(value)
    } catch {
      case e: Exception =>
        Left(e)
    } finally {
      os.close()
    }
}
