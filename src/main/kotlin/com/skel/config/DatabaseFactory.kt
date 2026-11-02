package com.skel.config

import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Property
import io.vertx.mutiny.core.Vertx
import io.vertx.mutiny.sqlclient.Pool
import io.vertx.pgclient.PgConnectOptions
import io.vertx.sqlclient.PoolOptions
import jakarta.inject.Singleton

@Factory
class DatabaseFactory {

  @Singleton
  fun sqlPool(
      vertx: Vertx,
      @Property(name = "datasources.default.url") url: String,
      @Property(name = "datasources.default.username") dbUser: String,
      @Property(name = "datasources.default.password") dbPass: String
  ): Pool {
    val connectOptions = PgConnectOptions.fromUri(url).setUser(dbUser).setPassword(dbPass)

    val poolOptions = PoolOptions().setMaxSize(5)

    // Creates a non-blocking PostgreSQL-backed pool using the generic Mutiny Pool API
    return Pool.pool(vertx, connectOptions, poolOptions)
  }
}
