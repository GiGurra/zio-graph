package com.github.gigurra.ziograph

import zio.json.*
import zio.logging.{LogFormat, console, consoleJson}
import zio.{ExitCode, IO, Runtime, Scope, Task, UIO, ZIO, ZIOAppArgs, ZIOAppDefault, ZLayer}

import java.util.concurrent.TimeUnit
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

object MainWip extends ZIOAppDefault:

  override val bootstrap: ZLayer[ZIOAppArgs, Any, Any] =
    Runtime.removeDefaultLoggers >>> console(LogFormat.colored)

  override def run: ZIO[Any, Any, Any] =
    (for {
      _           <- ZIO.logInfo(s"Starting experiment...")
      userService <- ZIO.service[UserService]
      _           <- userService.handleNewUser("J")
      _           <- userService.handleNewUser("D")
      _           <- userService.handleNewUser("G")
      _           <- ZIO.logInfo(s"Waiting for experiment to end...")
      _           <- ZIO.sleep(zio.Duration(10, TimeUnit.SECONDS))
    } yield ()).provide(
      ZLayer.fromFunction(UserService.apply _)
    )
