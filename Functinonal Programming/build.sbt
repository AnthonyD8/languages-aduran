ThisBuild / scalaVersion := "2.12.17"

lazy val root = (project in file("."))
  .settings(
    name := "HelloWorldScalaApp",
    version := "0.1",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor-typed" % "[2.6,3.0)",  // Allows any version from 2.6.x up to (but not including) 3.0
      "com.typesafe.akka" %% "akka-http" % "[10.2,11.0)",  // Allows any version from 10.2.x up to (but not including) 11.0
      "com.typesafe.akka" %% "akka-stream" % "[2.6,3.0)",  // Allows any version from 2.6.x up to (but not including) 3.0
      "com.typesafe.akka" %% "akka-http-spray-json" % "10.2.10",  // Akka HTTP JSON support
      "io.spray" %% "spray-json" % "1.3.6",  // Latest version of spray-json
      "com.typesafe" %% "ssl-config-core" % "0.6.1",  // Add this for SSL configuration
      "org.slf4j" % "slf4j-simple" % "1.7.36"  // SLF4J Logger
    )
  )
