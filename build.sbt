name := "Quick Scala FTP"

version := "0.1"

retrieveManaged := true

libraryDependencies ++= Seq(
   "org.slf4j" % "slf4j-api" % "1.7.5",
   "org.slf4j" % "slf4j-simple" % "1.7.5",
   "org.apache.mina" % "mina-core" % "2.0.7",
   "org.apache.ftpserver" % "ftpserver-core" % "1.0.6",
   "org.apache.ftpserver" % "ftplet-api" % "1.0.6"
)
