// package io.github.petesta.awslambda
//
// import com.amazonaws.services.lambda.runtime.{ Context, RequestHandler }
// import io.circe.{ Decoder, Encoder }
// // import java.io.{ InputStream, OutputStream }
// // import scala.concurrent.{ Await, ExecutionContext, Future }
// // import scala.concurrent.duration.{ Duration, MILLISECONDS }
//
// final case class ApiGatewayResponse[A](statusCode: Int, body: A)
//
// final case class ParseError(error: String)
//
// abstract class RHandler[A: Decoder, B: Encoder](
//   // encoder: Encoder[io.github.petesta.awslambda.ApiGatewayResponse[B]]
//   // parseErrEncoder: Encoder[ParseError]
// ) extends RequestHandler[A, io.github.petesta.awslambda.ApiGatewayResponse[B]] with Encoding {
//   protected def handle(input: A): io.github.petesta.awslambda.ApiGatewayResponse[B]
//
//   // def handleRequest(input: Either[C, A], context: Context): String = {
//   //   ()
//   //   ""
//   def handleRequest(input: A, context: Context): io.github.petesta.awslambda.ApiGatewayResponse[B] = {
//     handle(input)
//     // val validJson = in(is)
//     // (validJson, handle(validJson)) match {
//     //   case (Left(err), _) =>
//     //     err
//     // }
//     // val validJson = in(is)
//     // out(validJson, handle(validJson), os)
//     // ()
//   }
// }
