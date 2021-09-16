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

def patch(file: File)(implicit content: String): String = {
  val path = file.getAbsolutePath
  if (path has "generator") {
    return "CLA_(USER|PASS)".r replace "CLA_GENERATOR_$1"
  } else if (path has "checker") {
    return "CLA_(USER|PASS)".r replace "CLA_CHECKER_$1"
  }
  return content
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
        patch(file)(contents) writeTo file
        println(s"Done")
      }
      case Failure(e) => println(s"Skip $e")
    }
  }
}
