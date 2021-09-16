#!/usr/local/bin/amm
// Patch the file tree
import $file.config
import config.Ext._
import java.io.{ File }
import scala.util.{ Success, Failure }
import scala.util.{ Using }
import scala.collection.JavaConverters._

// Recursive walker
val walk = config.walker(
  ".git"
)

val claUserPassRegex = "CLA_(USER|PASS|PASSWORD)".r
val userOrNameVar = "((GENERATOR|IMPORTER|CONTENT_MANAGER|DEFAULT_ADMIN)_[A-Z_]+)".r

def prefix(implicit source: String): String =
  userOrNameVar replace "CLA_$1"

def patch(file: File, content: String): String = {
  val path = file.getAbsolutePath
  implicit val src = prefix(content)
  return if ((path has "generator") || (path has "ftpuploader")) {
    claUserPassRegex replace "CLA_GENERATOR_$1"
  } else if (path has "checker") {
    claUserPassRegex replace "CLA_CHECKER_$1"
  } else if (path has "importer") {
    claUserPassRegex replace "CLA_IMPORTER_$1"
  } else {
    src
  }
}

// Walk each folder
config.urls.foreach { r =>
  import r.{ url, folder }
  println(s"\n +- Patching $folder...")
  walk(folder.file) { file =>
    print(s" |-- $file...")
    file.read match {
      case Success(contents) => {
        // If OK patch the sources
        print(s"Patching ${contents.length} bytes...")
        patch(file, contents) writeTo file
        println(s"Done")
      }
      case Failure(e) => println(s"Skip $e")
    }
  }
}
