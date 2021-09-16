#!/usr/local/bin/amm
// This script clones, creates the branch and pushes it to
// the forked repositories.
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
