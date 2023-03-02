package com.github.gigurra.ziograph

import com.github.gigurra.ziograph.impl.{GraphTopic, Node}
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}
import zio.{UIO, ZIO}

case class UserService( /* userRepo: UserRepo */ ):

  import UserOp.*

  def handleNewUser(id: String): UIO[Unit] =
    topic1
      .ordered(on = id)
      .enqueue(
        CreateUser(id),
        ActivateUser(id, 1),
        SubscribeUser(id, 2, "3")
      )

  private def something: Int             = (math.random() * 100.0).toInt
  private val topic1: GraphTopic[UserOp] = new GraphTopic[UserOp]("banana1")

  private object UserOp {

    sealed trait UserOp extends Node:
      override def run(): UIO[Unit]

    case class CreateUser(id: String) extends UserOp:
      override def run(): UIO[Unit] = ZIO.logInfo(s"Running $this, something=$something")

    case class ActivateUser(id: String, a: Int) extends UserOp:
      override def run(): UIO[Unit] = ZIO.logInfo(s"Running $this, something=$something")

    case class SubscribeUser(id: String, a: Int, b: String) extends UserOp:
      override def run(): UIO[Unit] = ZIO.logInfo(s"Running $this, something=$something")

    object UserOp:
      given JsonDecoder[UserOp] = DeriveJsonDecoder.gen[UserOp]
      given JsonEncoder[UserOp] = DeriveJsonEncoder.gen[UserOp]
  }
