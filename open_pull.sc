#!/usr/local/bin/amm
// This script opens pull requests from the changes.txt file
import $file.config
import config.Ext._
import scala.language.postfixOps
import sys.process._
import scala.io.Source
import $ivy.`org.scalaj::scalaj-http:2.4.2`
import scalaj.http.Http

val base = "https://api.github.com"
val target = "XTreamr"
val token = "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"
val title = "[CLAs Experience][eCLAs][CMS][BE] Cambiar CLA_USER y CLA_PASSWORD por el mismo nombre que el que se usa en CMS"
val body = """
Issue original: https://jira.tid.es/browse/CLA-761

> Cambiar las referencias a CLA_USER y CLA_PASSWORD por CLA_GENERATOR_USER o CLA_IMPORTER_USER. La idea es tener los mismos nombres en todos los repos para evitar las confusiones.
"""

var pullUrls = List[String]()

println("Getting repository data...")
val branches = Http(s"$base/orgs/$target/repos?sort=full_name&per_page=100")
  .auth(token)
  .json
  .arr
  .map { it => 
    it("name").str -> it("default_branch").str
  }
  .toMap

val urlFixer = raw"https://api.github.com/repos/XTreamr/cla-cms/pulls/([0-9]+)".r

Source.fromFile("changes.txt").getLines().foreach { folder =>
  println(s"Opening pull request at $folder...")
  val repo = folder
  val mainBranch = branches(repo)
  val data = Http(s"$base/repos/$target/$repo/pulls")
    .postJson(
      "title" -> title,
      "body" -> body,
      "head" -> s"pblanco-dekalabs:${config.branch}",
      "base" -> mainBranch
    )
    .auth(token)
    .json
  try {
    val urlFixer(id) = data("url").str
    val url = s"https://github.com/XTreamr/$repo/pull/$id"
    pullUrls ::= s"$folder -> $url"
    println(s"Added $folder at $url")
  } catch {
    case e: Any =>
      println(e)
      throw new Error(s"Failed to recover data: $data")
  }
}

scala.util.Using(new java.io.FileWriter(new java.io.File("pull_requests.txt"))) { w =>
  w.write(pullUrls.mkString("\n").toArray)
}
