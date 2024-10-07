import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._
import spray.json._

import scala.io.StdIn

object HelloWorldServer {

  // Case class to represent the JSON structure
  case class StringList(strings: List[String])

  // Define the JSON format using Spray JSON
  implicit val stringListFormat = jsonFormat1(StringList)

  def main(args: Array[String]): Unit = {
    // Create an ActorSystem
    implicit val system = ActorSystem(Behaviors.empty, "helloWorldSystem")
    implicit val executionContext = system.executionContext

    // Define the route
    val route =
      concat(  // Using concat to properly group multiple routes
        path("greet" / Segment) { person =>
          get {
            complete(s"Hello, $person!")
          }
        },
        pathSingleSlash {
          get {
            complete(s"Hello!")
          }
        },
        path("sortStrings") {
          post {
            entity(as[StringList]) { stringList =>
              // Functional programming style: use immutable transformation
              val sortedStrings = stringList.strings.sorted

              // Return the sorted list as JSON
              complete(sortedStrings.toJson.prettyPrint)
            }
          }
        }
      )

    // Start the server
    val bindingFuture = Http().newServerAt("localhost", 8080).bind(route)

    println("Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // Keep the server running until user presses return
    bindingFuture
      .flatMap(_.unbind()) // Unbind from the port
      .onComplete(_ => system.terminate()) // Terminate the system when done
  }
}