package io.github.petesta.awslambda

import AwsLambda.{ BaseHandler, FutureBaseHandler }
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.util.StringInputStream
import io.circe.generic.auto._
import java.io.ByteArrayOutputStream
import org.scalatest.{ FunSuite, Matchers }
import org.scalatest.mockito.MockitoSugar
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.duration.Duration

object AwsLambda {
  final case class Request(body: String)

  final case class Output(message: String)

  class BaseHandler extends Handler[Request, Output] {
    def handle(input: Request): Response[Output] =
      Response(200, Output(""))
  }

  class FutureBaseHandler(
    time: Option[Duration] = None
  ) extends FutureHandler[Request, Output](time) {
    def handle(input: Request): Future[Response[Output]] =
      Future.successful(Response(200, Output("")))
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

    os.toString should equal("""{"statusCode":400,"body":{"error":"DecodingFailure(Attempt to decode value on failed cursor, List(DownField(body)))"}}""")
  }
}

class FutureHandlerTest extends FunSuite with Matchers with MockitoSugar {
  test("should handle request successfully") {
    val json = """{ "body": "hello" }"""
    val is = new StringInputStream(json)
    val os = new ByteArrayOutputStream()

    new FutureBaseHandler().handleRequest(is, os, mock[Context])

    os.toString should equal("""{"statusCode":200,"body":{"message":""}}""")
  }

  test("should handle request unsuccessfully") {
    val json = """{ "different_key": "hello" }"""
    val is = new StringInputStream(json)
    val os = new ByteArrayOutputStream()

    new FutureBaseHandler().handleRequest(is, os, mock[Context])

    os.toString should equal("""{"statusCode":400,"body":{"error":"DecodingFailure(Attempt to decode value on failed cursor, List(DownField(body)))"}}""")
  }
}
