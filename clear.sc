// This script clears the entire source tree
import $file.config
import config.Ext._
import java.io.File

// Delete all directories
config.urls.foreach { r =>
  import r.{ folder }
  println(s"Deleting $folder...")
  folder.dir.deleteRecursively()
}
