package com.github.hexx

import java.io.File
import scala.io.Source
import scala.sys.process._
import scala.util.control.Exception._
import play.core.jscompile.JavascriptCompiler
import play.PlayExceptions.AssetCompilationException

object HaxeCompiler {
  def compile(file: File, options: Seq[String]) = {
    val js = executeNativeCompiler(file, options)
    (js, minify(js, file), Seq(file))
  }

  def minify(js: String, file: File) = {
    catching(classOf[AssetCompilationException]).opt(JavascriptCompiler.minify(js, Some(file.getName)))
  }

  def executeNativeCompiler(src: File, options: Seq[String]): String = {
    val dir = new File(src.getParentFile.getAbsolutePath)
    val dest = File.createTempFile(src.getName, ".js")
    try {
      val process = Process(Seq("haxe", "-cp", dir.getAbsolutePath, "-js", dest.getAbsolutePath) ++ options ++ Seq("-main", src.getName))
      val out = new StringBuilder
      val err = new StringBuilder
      val logger = ProcessLogger(s => out.append(s + "\n"), s => err.append(s + "\n"))
      val exit = process ! logger
      if (exit == 0) Source.fromFile(dest).mkString
      else {
        val errString = err.mkString

        // .hx files without main function is used to be imported from other files.
        // Returns empty string in this case.
        if (errString.contains(" does not have static function main")) ""
        else {
          // Following regex assumes that a path name
          // 1. starts with a non-space character
          // 2. doesn't contain : (colon)
          val regex = """(\S[^:]*\.hx):(\d+): (?:characters (\d+))?""".r
          val (file, line, column) = regex.findFirstMatchIn(errString).map { (m) =>
            (
              Option(m.group(1)).map(new File(_)),
              Option(m.group(2)).map(_.toInt),
              Option(m.group(3)).map(_.toInt)
            )
          }.getOrElse((Some(src), None, None))

          throw AssetCompilationException(file, errString, line, column)
        }
      }
    } finally {
      dest.delete()
    }
  }
}
