package io.github.petesta.awslambda

import AwsLambda.BaseHandler
import io.circe._
import io.circe.generic.auto._
import io.circe.generic.JsonCodec
// import io.circe.generic.semiauto._
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

  @JsonCodec final case class ClientError(message: String) extends HandlerError

  // implicit val handlerErrDecoder: Encoder[Response[ClientError]] = deriveEncoder[Response[ClientError]]

  class BaseHandler extends Handler[Request, Output] {
    // implicit val handlerErrDecoder: Encoder[Response[ClientError]] = deriveEncoder[Response[ClientError]]
    def handle(request: Request): Either[Response[HandlerError], Response[Output]] =
      if (true) Right(Response(200, Output(request.body)))
      else Left(Response(400, ClientError("")))
  }
}

class HandlerTest extends FunSuite with Matchers with MockitoSugar {
  test("should handle request successfully") {
    val json = """{ "body": "hello" }"""
    val is = new StringInputStream(json)
    val os = new ByteArrayOutputStream()

    new BaseHandler().handleRequest(is, os, mock[Context])

    os.toString should equal("""{"statusCode":200,"body":{"message":"hello"}}""")
  }

  test("should handle request unsuccessfully") {
    val json = """{ "different_key": "hello" }"""
    val is = new StringInputStream(json)
    val os = new ByteArrayOutputStream()

    new BaseHandler().handleRequest(is, os, mock[Context])

    os.toString should equal("""{"statusCode":400,"body":"DecodingFailure(Attempt to decode value on failed cursor, List(DownField(body)))"}""")
  }
}
