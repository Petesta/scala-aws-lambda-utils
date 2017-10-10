scala-aws-lambda-utils
======================

## Intro
`scala-aws-lambda-utils` provides abstractions for serializing/deserializing, with [Circe], input/output with [AWS Lambda].

## How to use
```scala
libraryDependencies += "io.github.petesta" %% "scala-aws-lambda-utils" % "0.0.1"
```

## Examples
```scala
import io.circe._
import io.circe.generic.auto._
import io.circe.generic.semiauto._
import io.github.petesta.awslambda._
import scala.concurrent.Future

final case class Request(data: String)
final case class Person(name: String)
final case class ClientError(error: String) extends HandlerError

implicit val cencoder: Encoder[Response[ClientError]] = deriveEncoder[Response[ClientError]]

// NOTE:
//   input => { "data": "" }
//   output => { "statusCode": INTEGER, "body": PERSON_OBJECT }
class RequestHandler extends Handler[Request, ClientError, Person] {
  def handler(request: Either[HandlerError, Request]): Either[Response[ClientError], Response[Output]] =
    request match {
      case Left(_) =>
        Left(Response(400, ClientError("custom error message")))
      case Right(_) =>
        Right(Response(200, Person("Pete")))
    }
}

// NOTE:
//   input => { "data": "" }
//   output => { "statusCode": INTEGER, "body": PERSON_OBJECT }
class FutureBaseHandler(
  time: Option[Duration] = None
) extends FutureHandler[Request, ClientError, Person](time) {
  def handle(request: Either[HandlerError, Request]): Future[Either[Response[ClientError], Response[Output]]] =
	request match {
	  case Left(_) =>
		Future.successful(Left(Response(400, ClientError("custom error message"))))
	  case Right(_) =>
		Future.successful(Right(Response(200, Person("Pete"))))
	}
}
```

#### NOTE:
* The response must contain `statusCode` and `body` if you use [API Gateway]. Otherwise, you'll run into an `Internal Server Error`.
* Invoking `serverless` from the command line will return the response appropriately even without `statusCode` and `body`.

[API Gateway]: https://aws.amazon.com/api-gateway/
[AWS Lambda]: https://aws.amazon.com/lambda/
[Circe]: https://circe.github.io/circe/
