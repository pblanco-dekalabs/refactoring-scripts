#!/usr/local/bin/amm
// This script looks for changes and makes commits.
import $file.config
import config.Ext._
import scala.language.postfixOps
import sys.process._

config.urls.foreach { r =>
  import r.{ url, folder }
  print(s"Checking $folder...")
  val result = "git status" at folder;
  val shouldUpdate = !result.contains("nothing to commit")
  if (shouldUpdate) {
    println("Commit!")
    "git add ." at folder;
    "git commit -m \"Updated names for env vars. See https://jira.tid.es/browse/CLA-761.\"" at folder;
    // "git push" at folder;
  } else {
    println("Skip")
  }
}

println("Done")
