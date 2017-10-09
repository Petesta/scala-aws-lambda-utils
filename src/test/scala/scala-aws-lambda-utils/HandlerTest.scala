package io.github.petesta.awslambda

import AwsLambda.BaseHandler
import io.circe._
import io.circe.generic.auto._
// import io.circe.generic.JsonCodec
import io.circe.generic.semiauto._
// import io.circe.parser._
// import io.circe.syntax._
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.util.StringInputStream
import java.io.ByteArrayOutputStream
import org.scalatest.{ FunSuite, Matchers }
import org.scalatest.mockito.MockitoSugar

object AwsLambda {
  final case class Output(message: String)

  final case class Request(body: String)

  final case class ClientError(message: String) extends HandlerError

  final case class FutureError(message: String) extends HandlerError

  implicit val cencoder: Encoder[Response[ClientError]] = deriveEncoder[Response[ClientError]]

  class BaseHandler extends Handler[Request, HandlerError, Output] {
    def handle(request: Either[HandlerError, Request]): Either[Response[FutureError], Response[Output]] =
      if (true)
        request match {
          case Left(_) =>
            Left(Response(400, ClientError("")))
          case Right(_) =>
            Right(Response(200, Output("")))
        }
      else Left(Response(400, FutureError("")))
  }
}

class HandlerTest extends FunSuite with Matchers with MockitoSugar {
  test("should handle request successfully") {
    val json = """{ "body": "hello" }"""
    val is = new StringInputStream(json)
    val os = new ByteArrayOutputStream()

    new BaseHandler().handleRequest(is, os, mock[Context])

    os.toString should equal("""{"statusCode":200,"body":{"message":""}}""")
  }

  test("should handle request unsuccessfully") {
    val json = """{ "different_key": "hello" }"""
    val is = new StringInputStream(json)
    val os = new ByteArrayOutputStream()

    new BaseHandler().handleRequest(is, os, mock[Context])

    println(os.toString)
    os.toString should equal("""{"statusCode":400,"body":"DecodingFailure(Attempt to decode value on failed cursor, List(DownField(body)))"}""")
  }
}
