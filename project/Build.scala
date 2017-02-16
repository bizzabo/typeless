import sbt._
import Keys._

object MyBuild extends Build{
  val repoKind = SettingKey[String]("repo-kind", "Maven repository kind (\"snapshots\" or \"releases\")")
  val projectName = "x-shapeless"
  lazy val aRootProject = Project(id = projectName, base = file("."),
    settings = Seq(
      version := "0.1.1",
      name := projectName,
      scalaVersion := "2.11.8",
      description := "It provides some extra shapeless functionallity",
      libraryDependencies ++=   Seq(
        "com.chuusai" %% "shapeless" % "2.3.2",
        "org.scalatest" %% "scalatest" % "2.2.6" % "test"
      ),
      resolvers ++= Seq(Resolver.sonatypeRepo("releases"),Resolver.sonatypeRepo("snapshots")),
      scalacOptions ++= Seq(
//        "-Xlog-implicits",
        "-feature",
        "-language:higherKinds",
        "-language:implicitConversions",
        "-unchecked"      ),
      testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oFD"),
      organizationName := "x.ai",
      organization := "ai.x",
      scalacOptions in (Compile, doc) <++= (version,sourceDirectory in Compile,name).map((v,src,n) => Seq(
        "-doc-title", n,
        "-doc-version", v,
        "-doc-footer", projectName+" is developed by Miguel Iglesias.",
        "-sourcepath", src.getPath, // needed for scaladoc to strip the location of the linked source path
        "-doc-source-url", "https://github.com/xdotai/"+projectName+"/blob/"+v+"/src/main€{FILE_PATH}.scala",
        "-implicits",
        "-diagrams", // requires graphviz
        "-groups"
      )),
      repoKind <<= (version)(v => if(v.trim.endsWith("SNAPSHOT")) "snapshots" else "releases"),
      publishTo <<= (repoKind){
        case "snapshots" => Some("snapshots" at "https://oss.sonatype.org/content/repositories/snapshots")
        case "releases" =>  Some("releases"  at "https://oss.sonatype.org/service/local/staging/deploy/maven2")
      },
      publishMavenStyle := true,
      publishArtifact in Test := false,
      pomIncludeRepository := { _ => false },
      makePomConfiguration ~= { _.copy(configurations = Some(Seq(Compile, Runtime, Optional))) },
      licenses += ("Apache 2.0", url("http://github.com/xdotai/"+projectName+"/blob/master/LICENSE.txt")),
      homepage := Some(url("http://github.com/xdotai/"+projectName)),
      startYear := Some(2016),
      pomExtra :=
        <developers>
          <developer>
            <id>caente</id>
            <name>Miguel Iglesias</name>
            <timezone>-5</timezone>
            <url>https://github.com/caente/</url>
          </developer>
        </developers>
          <scm>
            <url>git@github.com:xdotai/{projectName}.git</url>
            <connection>scm:git:git@github.com:xdotai/{projectName}.git</connection>
          </scm>
    )
  )
}
