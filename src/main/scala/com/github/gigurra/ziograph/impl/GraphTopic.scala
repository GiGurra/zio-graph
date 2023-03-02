package com.github.gigurra.ziograph.impl

import com.github.gigurra.ziograph.impl
import zio.json.*
import zio.logging.{LogFormat, console, consoleJson}
import zio.{ExitCode, IO, Runtime, Scope, Task, UIO, ZIO, ZIOAppArgs, ZIOAppDefault, ZLayer}

import java.util.concurrent.TimeUnit
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

class GraphTopic[A <: Node: JsonEncoder: JsonDecoder](val id: String):

  def ordered(on: String): impl.GraphTopic[A] =
    val self = this
    new impl.GraphTopic[A](id):
      override def enqueue(a: A*): UIO[Unit] =
        // TODO: Implement something
        self.enqueue(a: _*)

  /** Enqueues atomically. Either all are enqueued or none are
    */
  def enqueue(a: A*): UIO[Unit] =
    // For now just run immediately, TODO: Make transactional
    runNow(a.map(_.toJson))

  private def runNow(tasks: Seq[String]): UIO[Unit] =
    val decoder = summon[JsonDecoder[A]]
    ZIO
      .foreach(tasks)(task => ZIO.fromEither(decoder.decodeJson(task)))
      .flatMap(tasks =>
        ZIO.foreach(tasks)(t => t.run().tapErrorCause(ZIO.logCause(s"Task $t failed!", _)))
      )
      .catchAllCause(ZIO.logCause(s"Tasks failed!", _))
      .forkDaemon
      .unit
