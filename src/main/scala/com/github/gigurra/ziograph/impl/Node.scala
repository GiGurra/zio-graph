package com.github.gigurra.ziograph.impl

import zio.{Task, ZIO}

trait Node:
  def run(): Task[Unit]
  def expire(): Task[Unit] = ZIO.logError(s"Task expired: $this")
