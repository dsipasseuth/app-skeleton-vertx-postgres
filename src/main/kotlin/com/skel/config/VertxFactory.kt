package com.skel.config

import io.micronaut.context.annotation.Factory
import io.vertx.core.Vertx
import io.vertx.mutiny.core.Vertx as MutinyVertx
import jakarta.inject.Singleton

@Factory
class VertxFactory {
  @Singleton
  fun vertx(): Vertx {
    return Vertx.vertx()
  }

  @Singleton
  fun mutinyVertx(vertx: Vertx): MutinyVertx {
    return MutinyVertx(vertx)
  }
}
