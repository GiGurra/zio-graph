package com.github.gigurra.ziograph.impl

import zio.Task
import zio.json.JsonEncoder
import zio.json.JsonDecoder
import com.github.gigurra.ziograph.impl.GraphScheduler.NodeHandler

class GraphScheduler( /*graphRepo*/ ):

  def setHandler(
      topicName: String,
      handler: NodeHandler[_]
  ): Task[Unit] =
    ???

object GraphScheduler:

  sealed trait ExecutionResult
  object ExecutionResult:
    case object Done    extends ExecutionResult
    case object NotDone extends ExecutionResult

  case class ScheduledNode[A <: Node: JsonEncoder: JsonDecoder]()

  trait NodeHandler[N <: ScheduledNode[_]] {
    def handle(node: N): Task[ExecutionResult]
  }
