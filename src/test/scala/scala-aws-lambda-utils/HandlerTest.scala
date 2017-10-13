package io.github.petesta.awslambda

import AwsLambda.BaseHandler
// import AwsLambda.{ BaseHandler, FutureBaseHandler }
import io.circe.generic.auto._
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.util.StringInputStream
import java.io.ByteArrayOutputStream
import org.scalatest.{ FunSuite, Matchers }
import org.scalatest.mockito.MockitoSugar
// import scala.concurrent.Future
// import scala.concurrent.ExecutionContext.Implicits._
// import scala.concurrent.duration.Duration

object AwsLambda {
  class BaseHandler extends Handler[Request, Output] {
    def handle(input: Request): Output =
      Output("")
  }

  // class BaseHandler extends Handler[Request, ClientError, Output] {
  //   def handle(request: Either[HandlerError, Request]): Either[Response[ClientError], Response[Output]] =
  //     request match {
  //       case Left(_) =>
  //         Left(Response(400, ClientError("")))
  //       case Right(_) =>
  //         Right(Response(200, Output("")))
  //     }
  // }

  // class FutureBaseHandler(
  //   time: Option[Duration] = None
  // ) extends FutureHandler[Request, ClientError, Output](time) {
  //   def handle(request: Either[HandlerError, Request]): Future[Either[Response[ClientError], Response[Output]]] =
  //     request match {
  //       case Left(_) =>
  //         Future.successful(Left(Response(400, ClientError(""))))
  //       case Right(_) =>
  //         Future.successful(Right(Response(200, Output(""))))
  //     }
  // }
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

// class FutureHandlerTest extends FunSuite with Matchers with MockitoSugar {
//   test("should handle request successfully") {
//     val json = """{ "body": "hello" }"""
//     val is = new StringInputStream(json)
//     val os = new ByteArrayOutputStream()
//
//     new FutureBaseHandler().handleRequest(is, os, mock[Context])
//
//     os.toString should equal("""{"statusCode":200,"body":{"message":""}}""")
//   }
//
//   test("should handle request unsuccessfully") {
//     val json = """{ "different_key": "hello" }"""
//     val is = new StringInputStream(json)
//     val os = new ByteArrayOutputStream()
//
//     new FutureBaseHandler().handleRequest(is, os, mock[Context])
//
//     os.toString should equal("""{"statusCode":400,"body":{"error":"DecodingFailure(Attempt to decode value on failed cursor, List(DownField(body)))"}}""")
//   }
// }
