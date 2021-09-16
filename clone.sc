#!/usr/local/bin/amm
// This script clones, creates the branch and pushes it to
// the forked repositories.
import $file.config
import config.Ext._
import scala.language.postfixOps
import sys.process._

config.urls.foreach { r =>
  import r.{ url, folder }
  println(s"Cloning $url")
  s"git clone $url"!;
  s"git checkout -b ${config.branch}" at folder;
  s"git push -f -u origin ${config.branch}" at folder;
}

println("Done")
