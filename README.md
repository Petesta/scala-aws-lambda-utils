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
import io.circe.generic.auto._
import io.github.petesta.awslambda._
import scala.concurrent.Future

final case class Request(body: String)

final case class Output(message: String)

// NOTE:
//   input => { "body": "" }
//   output => { "statusCode": INTEGER, "body": PERSON_OBJECT }
class BaseHandler extends Handler[Request, Output] {
  def handle(input: Request): Response[Output] =
    Response(200, Output(""))
}

// NOTE:
//   input => { "data": "" }
//   output => { "statusCode": INTEGER, "body": PERSON_OBJECT }
class FutureBaseHandler(
  time: Option[Duration] = None
) extends FutureHandler[Request, Output](time) {
  def handle(input: Request): Future[Response[Output]] =
    Future.successful(Response(200, Output("")))
}
```

#### NOTE:
* The response must contain `statusCode` and `body` if you use [API Gateway]. Otherwise, you'll run into an `Internal Server Error`.
* Invoking `serverless` from the command line will return the response appropriately even without `statusCode` and `body`.

[API Gateway]: https://aws.amazon.com/api-gateway/
[AWS Lambda]: https://aws.amazon.com/lambda/
[Circe]: https://circe.github.io/circe/
