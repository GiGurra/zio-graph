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

  def bar2(
      x: Int :| Less[13]
  ): Unit = ???

  def foo1(
      x: Int :| Less[1]
  ): Unit = ???

  def foo2(
      x: Int :| Greater[-2]
  ): Unit = ???

  // Imagine this function living inside your own project
  def foobar(
      z: Int :| (Greater[0] & Less[12] & Boogoo[1] & SomeAge)
  ): Unit =
    foo(z)
    bar(z)
    foo2(z.refine)
    foo2(z.refineFurther[Greater[-2]])

    // foobar(12) // not < 12, so does not compile
  extension [Src](value: Src)
    inline def refineZIO[C](using inline constraint: Constraint[Src, C]): IO[String, Src :| C] =
      value.refineValidation[C].toZIO

  extension [Src, Cstr](value: Src :| Cstr)
    inline def refineFurtherZIO[C](using inline constraint: Constraint[Src, C]): IO[String, Src :| (Cstr & C)] =
      (value: Src).refineZIO[C].map(_.asInstanceOf[Src :| (Cstr & C)])
    inline def refineFurtherEither[C](using inline constraint: Constraint[Src, C]): Either[String, Src :| (Cstr & C)] =
      (value: Src).refineEither[C].map(_.asInstanceOf[Src :| (Cstr & C)])
    inline def refineFurtherOption[C](using inline constraint: Constraint[Src, C]): Option[Src :| (Cstr & C)] =
      (value: Src).refineOption[C].map(_.asInstanceOf[Src :| (Cstr & C)])
    inline def refineFurther[C](using inline constraint: Constraint[Src, C]): Src :| (Cstr & C) =
      (value: Src).refine[C].asInstanceOf[Src :| (Cstr & C)]

  override def run: ZIO[Any, Throwable, Any] =
    (for {
      _           <- ZIO.logInfo(s"Starting experiment...")
      userService <- ZIO.service[UserService]
      _           <- userService.handleNewUser("J")
      _           <- userService.handleNewUser("D")
      _           <- userService.handleNewUser("G")
      x1          <- 0.refineZIO[Less[1]].mapError(new RuntimeException(_))
      x2          <- x1.refineFurtherZIO[Less[2]].mapError(new RuntimeException(_))
      x3          <- x2.refineFurtherZIO[Less[3]].mapError(new RuntimeException(_))
      _           <- ZIO.logInfo(s"Waiting for experiment to end...")
      _           <- ZIO.sleep(zio.Duration(3, TimeUnit.SECONDS))
    } yield ()).provide(
      ZLayer.fromFunction(UserService.apply _)
    )
