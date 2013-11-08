import com.tuplejump.sbt.yeoman.Yeoman

name := "SMSRelay-server"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  "com.typesafe.play" %% "play-slick" % "0.5.0.8" 
)

play.Project.playScalaSettings

Yeoman.yeomanSettings

playAssetsDirectories <+= baseDirectory / "ui/dist"