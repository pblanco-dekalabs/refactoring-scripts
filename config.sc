/*
  This file is a library type file, do not execute it.
*/
// Configuration for this operation
import scala.language.postfixOps
import sys.process._
import scala.util.{ Try }
import java.io.{
  File,
  FileWriter,
  FileReader,
  BufferedWriter,
  BufferedReader
}
import java.nio.file.{ Files, Path }
import java.nio.charset.{ StandardCharsets }
import scala.util.matching._

val github = "http://github.com/"
val team = "pblanco-dekalabs"
val repos = List(
  "cla-cms",
  "cla-frontend",
  "cla-devops",
  "cla-importer",
  "cla-tcdndispatcher",
  "cla-ftpuploader",
  "cla-train",
  "cla-predict",
  "clarec",
  "cla-videogenerator",
  "cla-videochecker",
  "cla-health",
  "cla-proxy",
  "cla-apirabbit",
  "cla-videogenerator-to-api",
)
val urls = repos.map(r => new {
  val folder = r
  val url = github + team + "/" + r
})
val branch = "feature/cla-761"

// General extensions
object Ext {
  implicit class StrExtCfg(s: String) {
    def file = new File(s)
    def dir = new scala.reflect.io.Directory(s.file)
    def at(folder: String) = Process(s, folder.file).!!
    def writeTo(file: File) = {
      Files.write(file.toPath, s.getBytes(StandardCharsets.UTF_8))
    }
    def has(o: String) = s contains o
  }
  implicit class FileExtCfg(file: File) {
    def writer = new BufferedWriter(new FileWriter(file))
    def reader = new BufferedReader(new FileReader(file))
    def read = Try { Files.readString(file.toPath) }
  }
  implicit class RegexExtCfg(r: Regex) {
    def replace(target: String)(implicit content: String) =
      r.replaceAllIn(content, target)
  }
}

/** A recursive walker functor. 
  */
class Walker(val ignore: Set[String]) {
  def apply(file: File)(fn: File => Unit): Unit = {
    if (ignore.contains(file.getName)) {
      return
    }
    if (file.isDirectory) {
      for (f <- file.listFiles) {
        this(f)(fn)
      }
    } else {
      fn(file)
    }
  }
}

/** Creates a walker for the given ignore set, and starts
  * recursively walking the file tree.
  * @param ignore
  * @param file
  * @param fn
  */
def walker(ignore: String*) = new Walker(Set(ignore:_*))
