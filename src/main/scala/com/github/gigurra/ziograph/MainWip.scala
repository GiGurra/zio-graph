package com.github.gigurra.ziograph

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.numeric.{Greater, Less, Positive}
import io.github.iltotore.iron.compileTime.stringValue
import zio.json.*
import zio.logging.{LogFormat, console, consoleJson}
import zio.{ExitCode, IO, Runtime, Scope, Task, UIO, ZIO, ZIOAppArgs, ZIOAppDefault, ZLayer}

import java.util.concurrent.TimeUnit
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.compiletime.constValue

object MainWip extends ZIOAppDefault:

  override val bootstrap: ZLayer[ZIOAppArgs, Any, Any] =
    Runtime.removeDefaultLoggers >>> console(LogFormat.colored)

  // Imagine this function living inside libX.jar
  type SomeAge = Positive DescribedAs "Age should be positive"
  def foo(
      x: Int :| (Greater[0] & SomeAge)
  ): Unit = ???

  // Imagine this living inside libY.jar
  final class Boogoo[V]
  inline given [V <: Int]: Constraint[Int, Boogoo[V]] with
    override inline def test(value: Int): Boolean = value + 1 > constValue[V]
    override inline def message: String           = "Should be greater than " + stringValue[V]
  def bar(
      x: Int :| (Boogoo[1] & Less[12])
  ): Unit = ???

  // Imagine this function living inside your own project
  def foobar(
      x: Int :| (Greater[0] & Less[12] & Boogoo[1] & SomeAge)
  ): Unit =
    foo(x)
    bar(x)

  foobar(2)
  // foobar(12) // not < 12, so does not compile

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
