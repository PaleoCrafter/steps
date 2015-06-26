name := "steps"
organization := "de.mineformers"
version := "0.0.1"
scalaVersion := "2.11.7"

autoCompilerPlugins := true
scalacOptions += "-Xplugin-require:scalaxy-streams"
scalacOptions += "-optimise"
scalacOptions += "-Yclosure-elim"
scalacOptions += "-Yinline"
scalacOptions += "-Ybackend:GenBCode"
scalacOptions += "-Xexperimental"

initialCommands in console := "import steps._; import steps.impl._; import S7._;"

addCompilerPlugin("com.nativelibs4java" %% "scalaxy-streams" % "0.3.4")