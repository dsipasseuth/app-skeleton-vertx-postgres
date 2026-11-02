package com.skel.repositories

import com.skel.domain.Bow
import io.smallrye.mutiny.Multi
import io.vertx.mutiny.sqlclient.Pool
import io.vertx.mutiny.sqlclient.Row
import io.vertx.mutiny.sqlclient.Tuple
import jakarta.inject.Singleton

@Singleton
class BowRepository(private val pool: Pool) {

  fun streamAll(): Multi<Bow> {
    return pool
        .withConnection { conn ->
          conn.prepare("SELECT id, bow_type, draw_weight, draw_length FROM bows").map {
              preparedQuery ->
            // Create a cursor stream with a fetch batch size of 50
            preparedQuery.createStream(50).toMulti()
          }
        }
        .toMulti()
        .flatMap { it.map(::mapRowToDomain) }
  }

  fun streamByBowType(bowType: String): Multi<Bow> {
    return pool
        .withConnection { conn ->
          conn
              .prepare(
                  "SELECT id, bow_type, draw_weight, draw_length FROM bows WHERE bow_type = $1")
              .map { preparedQuery -> preparedQuery.createStream(50, Tuple.of(bowType)).toMulti() }
        }
        .toMulti()
        .flatMap { it.map(::mapRowToDomain) }
  }

  private fun mapRowToDomain(row: Row): Bow {
    return Bow(
        id = row.getLong("id") ?: 0L,
        bowType = row.getString("bow_type") ?: "",
        drawWeight = row.getInteger("draw_weight") ?: 0,
        drawLength = row.getInteger("draw_length") ?: 0)
  }
}
