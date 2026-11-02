package com.skel.verticles

import com.skel.services.bowservice.BowServiceGrpcService
import com.skel.services.bowservice.BowServiceService
import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.grpc.reflection.ReflectionService
import io.vertx.grpc.server.GrpcServer
import jakarta.inject.Singleton

@Singleton
class GrpcServerVerticle(private val bowService: BowServiceService) : AbstractVerticle() {
  override fun start(promise: Promise<Void>) {
    val grpcServer =
        GrpcServer.server(vertx)
            .addService(ReflectionService.v1())
            .addService(BowServiceGrpcService.of(bowService))

    vertx
        .createHttpServer()
        .requestHandler(grpcServer)
        .listen(9000)
        .onFailure(promise::fail)
        .onSuccess {
          println("gRPC server started on port 9000")
          promise.complete()
        }
  }
}
