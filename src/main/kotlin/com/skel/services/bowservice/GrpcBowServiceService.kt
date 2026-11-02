package com.skel.services.bowservice

import com.skel.domain.Bow
import com.skel.repositories.BowRepository
import com.skel.services.bowservice.Bow as ProtoBow
import io.smallrye.mutiny.vertx.MultiHelper
import io.vertx.core.Future
import io.vertx.core.streams.ReadStream
import jakarta.inject.Singleton

@Singleton
class GrpcBowServiceService(private val bowRepository: BowRepository) : BowServiceService() {

  override fun listBow(request: ListBowRequest): Future<ReadStream<ProtoBow>> {
    val useFilter = request.bowType != BowType.UNKNOWN

    // 1. Get the Mutiny Multi stream from your repository
    val multiStream =
        if (useFilter) {
          bowRepository.streamByBowType(request.bowType.name)
        } else {
          bowRepository.streamAll()
        }

    // 2. Map Domain Bow to Protobuf ProtoBow within the Mutiny stream
    val mappedMulti = multiStream.map { bow -> toProtoBow(bow) }

    // 3. Convert the Mutiny Multi into a core Vert.x ReadStream<ProtoBow>
    // Mutiny provides a direct bridge via publisher conversion to a core ReadStream
    val coreReadStream = MultiHelper.toReadStream(mappedMulti)

    // 4. Wrap the ReadStream in a completed Vert.x Future
    return Future.succeededFuture(coreReadStream)
  }

  private fun toProtoBow(bow: Bow) = bow {
    bowType = BowType.valueOf(bow.bowType)
    drawWeight = bow.drawWeight
    drawLength = bow.drawLength
  }
}
