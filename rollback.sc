#!/usr/local/bin/amm
// This script resets all changes in case of error.
import $file.config
import config.Ext._
import scala.language.postfixOps
import sys.process._

config.urls.foreach { r =>
  import r.{ url, folder }
  println(s"Resetting $url...")
  s"git reset --hard" at folder;
}

println("Done")
