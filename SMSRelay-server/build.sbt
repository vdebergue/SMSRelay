import com.tuplejump.sbt.yeoman.Yeoman

name := "SMSRelay-server"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache
)

play.Project.playScalaSettings

Yeoman.yeomanSettings