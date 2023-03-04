package com.github.gigurra.ziograph.impl

import zio.Task
import zio.json.JsonEncoder
import zio.json.JsonDecoder

class GraphScheduler( /*graphRepo*/ ):

  def registerHandler[A <: Node: JsonEncoder: JsonDecoder](
      topicName: String
  ): Task[GraphTopic[A]] =
    ???

object GraphScheduler:

  sealed trait ExecutionResult
  object ExecutionResult:
    case object Done    extends ExecutionResult
    case object NotDone extends ExecutionResult

  case class ScheduledNode[A <: Node: JsonEncoder: JsonDecoder]()

  trait TaskHandler[A <: Node: JsonEncoder: JsonDecoder] {
    def handle(task: ScheduledNode[A]): Task[ExecutionResult]
  }
