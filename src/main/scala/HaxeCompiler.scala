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
        if (errString.indexOf(" does not have static function main") >= 0) ""
        else {
          val regex1 = """(?s).*\.hx:(\d+): characters (\d+)-.*""".r
          val regex2 = """(?s).*\.hx:(\d+): """.r

          val (line, column) = errString match {
            case regex1(l, c) => (Some(l.toInt), Some(c.toInt))
            case regex2(l) => (Some(l.toInt), None)
            case _ => (None, None)
          }

          throw AssetCompilationException(Some(src), errString, line, column)
        }
      }
    } finally {
      dest.delete()
    }
  }
}
