package com.github.gigurra.ziograph

import zio.ZIOAppDefault
import zio.json.*
import zio.{ExitCode, IO, Runtime, Scope, Task, UIO, ZIO, ZIOAppArgs, ZIOAppDefault, ZLayer}
import zio.logging.{LogFormat, console, consoleJson}
import io.github.iltotore.iron.{zio as _, *}
import io.github.iltotore.iron.zio.*
import io.github.iltotore.iron.constraint.numeric.{Greater, Less, Positive}
import io.github.iltotore.iron.compileTime.stringValue

import java.util.concurrent.TimeUnit
import scala.compiletime.constValue

import zio.prelude.Validation

object MainWip extends ZIOAppDefault:

  override val bootstrap: ZLayer[ZIOAppArgs, Any, Any] =
    Runtime.removeDefaultLoggers >>> console(LogFormat.colored)
  override def run: ZIO[Any, Throwable, Any] =
    (for {
      _           <- ZIO.logInfo(s"Starting experiment...")
      userService <- ZIO.service[UserService]
      _           <- userService.handleNewUser("J")
      _           <- userService.handleNewUser("D")
      _           <- userService.handleNewUser("G")
      _           <- ZIO.logInfo(s"Waiting for experiment to end...")
      _           <- ZIO.sleep(zio.Duration(3, TimeUnit.SECONDS))
    } yield ()).provide(
      ZLayer.fromFunction(UserService.apply _)
    )
