package io.github.petesta.awslambda

import AwsLambda.BaseHandler
import io.circe.generic.auto._
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.util.StringInputStream
import java.io.ByteArrayOutputStream
import org.scalatest.{ FunSuite, Matchers }
import org.scalatest.mockito.MockitoSugar

object AwsLambda {
  final case class Output(message: String)

  final case class Request(body: String)

  class BaseHandler extends Handler[Request, Output] {
    def handle(request: Request): Response[Output] =
      Response(200, Output(request.body))
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
