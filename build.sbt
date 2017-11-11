organization := "com.pricehero"

name := "price-hero"

version := "0.1.0-SNAPSHOT-1"

scalaVersion := "2.12.3"

resolvers ++= List(
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
)

val json4sVersion = "3.5.3"
val finagleVersion = "17.10.0"

libraryDependencies ++= Seq(
  "com.twitter"                 %% "finagle-http"         % finagleVersion,
  "com.google.code.findbugs"    %  "jsr305"               % "2.0.1",
  "log4j"                       %  "log4j"                % "1.2.17",
  "joda-time"                   %  "joda-time"            % "2.3",
  "org.joda"                    %  "joda-convert"         % "1.6",
  "org.json4s"                  %% "json4s-native"        % json4sVersion,
  "com.trueaccord.scalapb"      %% "compilerplugin"       % "0.6.6",
  "junit"                       %  "junit"                % "4.11"  % "test",
  "org.scalatest"               %% "scalatest"            % "3.0.1" % "test"
)

enablePlugins(ProtobufPlugin)

