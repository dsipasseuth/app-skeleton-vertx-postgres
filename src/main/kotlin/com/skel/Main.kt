package com.skel

import com.skel.verticles.GrpcServerVerticle
import io.micronaut.context.ApplicationContext
import io.vertx.core.Vertx
import org.slf4j.LoggerFactory

private val log = LoggerFactory.getLogger("com.skel.Main")

fun main(args: Array<String>) {
  val log = LoggerFactory.getLogger("com.skel.Main")
  log.info("Starting application context...")

  // 1. Start Micronaut's IoC container
  val context = ApplicationContext.run()

  // 2. Get the Vertx instance managed by Micronaut (or create one)
  val vertx = context.getBean(Vertx::class.java)

  // 3. Resolve the Verticle from Micronaut so constructor injection works
  val serverVerticle = context.getBean(GrpcServerVerticle::class.java)

  // 4. Deploy the fully injected verticle
  vertx
      .deployVerticle(serverVerticle)
      .onSuccess { log.info("Verticle deployed successfully with ID: $it") }
      .onFailure { err -> log.error("Failed to deploy verticle", err) }
}
