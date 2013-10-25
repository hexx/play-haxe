package com.github.hexx

import sbt._
import sbt.Keys._

object PlayHaxePlugin extends Plugin {
  val haxeEntryPoints = SettingKey[PathFinder]("play-haxe-entry-points")
  val haxeOptions = SettingKey[Seq[String]]("play-haxe-options")

  val HaxeCompiler = play.Project.AssetsCompiler("haxe",
    (_ ** "*.hx"),
    haxeEntryPoints,
    { (name, min) => name.replace(".hx", if (min) ".min.js" else ".js") },
    { (haxeFile, options) => com.github.hexx.HaxeCompiler.compile(haxeFile, options) },
    haxeOptions
  )

  override val settings = Seq(
    haxeEntryPoints <<= (sourceDirectory in Compile)(base => base / "assets" ** "*.hx"),
    haxeOptions := Seq.empty[String],
    resourceGenerators in Compile <+= HaxeCompiler
  )
}
