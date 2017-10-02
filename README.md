scala-aws-lambda-utils
======================

## Intro
`scala-aws-lambda-utils` provides abstractions for serializing/deserializing, with [Circe], input/output with [AWS Lambda].

## How to use
```scala
libraryDependencies += "io.github.petesta" %% "scala-aws-lambda-utils" % "0.1.1"
```

## Examples
```scala
import io.github.petesta.awslambda._
import scala.concurrent.Future

final case class Request(data: String)
final case class Person(name: String)
final case class Error(title: String, message: String)

val error = Response(500, Error("ERR: ", "Malformed input"))

// NOTE:
//   input => { "data": "" }
//   output => { "statusCode": INTEGER, "body": PERSON_OBJECT }
class RequestHandler(error) extends Handler[Request, Person, Error] {
  def handler(request: Request, context: Context): Response =
    Response(200, Person(request.data))
}

// NOTE:
//   input => { "data": "" }
//   output => { "statusCode": INTEGER, "body": PERSON_OBJECT }
class FutureRequestHandler(error) extends FutureHandler[Request, Person, Error] {
  def handler(request: Request, context: Context): Future[Response] =
    Future.successful(Response(200, Person(request.data)))
}
```

#### NOTE:
* The response must contain `statusCode` and `body` if you use API Gateway. Otherwise, you'll run into an `Internal Server Error`.
* Invoking `serverless` from the command line will return the response appropriately even without `statusCode` and `body`.

[AWS Lambda]: https://aws.amazon.com/lambda/
[Circe]: https://circe.github.io/circe/
