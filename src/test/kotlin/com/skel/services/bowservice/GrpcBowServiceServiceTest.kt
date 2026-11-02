package com.skel.services.bowservice

import com.skel.domain.Bow
import com.skel.repositories.BowRepository
import com.skel.services.bowservice.Bow as ProtoBow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.smallrye.mutiny.Multi

class GrpcBowServiceServiceTest :
    StringSpec({
      val bowRepository = mockk<BowRepository>()
      val service = GrpcBowServiceService(bowRepository)

      "should stream all bows when BowType is default request" {
        val mockBows =
            listOf(
                Bow(id = 1L, bowType = "RECURVE", drawWeight = 45, drawLength = 28),
                Bow(id = 2L, bowType = "COMPOUND", drawWeight = 60, drawLength = 30))

        every { bowRepository.streamAll() } returns Multi.createFrom().iterable(mockBows)

        val request = listBowRequest {}

        val future = service.listBow(request)

        future.succeeded() shouldBe true
        val readStream = future.result()

        val collectedBows = mutableListOf<ProtoBow>()
        readStream.handler { protoBow -> collectedBows.add(protoBow) }

        collectedBows.size shouldBe 2
        collectedBows[0].drawWeight shouldBe 45
        collectedBows[1].drawWeight shouldBe 60
      }

      "should stream filtered bows when a specific BowType is provided" {
        val mockBows = listOf(Bow(id = 3L, bowType = "LONGBOW", drawWeight = 50, drawLength = 28))

        every { bowRepository.streamByBowType("LONGBOW") } returns
            Multi.createFrom().iterable(mockBows)

        val request = listBowRequest { bowType = BowType.LONGBOW }

        val future = service.listBow(request)

        future.succeeded() shouldBe true
        val readStream = future.result()

        val collectedBows = mutableListOf<ProtoBow>()
        readStream.handler { protoBow -> collectedBows.add(protoBow) }

        collectedBows.size shouldBe 1
        collectedBows[0].bowType shouldBe BowType.LONGBOW
      }
    })
