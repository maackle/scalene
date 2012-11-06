
organization := "scalene"

name := "scalene"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.9.2"

libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-simple" % "1.6.6",
  "org.clapper" %% "grizzled-slf4j" % "0.6.9",
  "org.scalatest" %% "scalatest" % "2.0.M4" % "test",
  "org.jbox2d" % "jbox2d" % "2.1.2.2",
  "org.jbox2d" % "jbox2d-library" % "2.1.2.2",
  "org.jbox2d" % "jbox2d-serialization" % "1.0.0",
  "com.googlecode.soundlibs" % "jorbis" % "0.0.17-1",
  "org.urish.openal" % "java-openal" % "1.0.0",
  "org.lwjgl.lwjgl" % "lwjgl" % "2.8.4",
  "org.lwjgl.lwjgl" % "lwjgl_util" % "2.8.4",
  "org.lwjgl.lwjgl" % "lwjgl-platform" % "2.8.4" classifier "natives-osx" classifier "natives-windows" classifier "natives-linux"
//  "net.java.jinput" % "jinput" % "2.0.5",
//  "net.java.jinput" % "jinput-platform" % "2.0.5" classifier "natives-osx" classifier "natives-windows" classifier "natives-linux"
//  "com.googlecode.soundlibs" % "jorbis" % "0.0.17-1"
//  "slick" % "slick" % "248"
)


// resolvers += "Scage @ Google Code" at "http://scage.googlecode.com/svn/maven-repository"

// resolvers += "LWJGL" at "http://adterrasperaspera.com/lwjgl"

resolvers += "Slick" at "http://slick.cokeandcode.com/mavenrepo"

resolvers ++= Seq(
	"Codehaus Snapshots" at "http://nexus.codehaus.org/snapshots/",
//	"Logback" at "scp://pixie.qos.ch/var/mvnrepo/",
	"OSS Sonatype" at "http://oss.sonatype.org/content/repositories/github-releases",
	"Scala Tools 2" at "http://scala-tools.org/repo-releases"
)
