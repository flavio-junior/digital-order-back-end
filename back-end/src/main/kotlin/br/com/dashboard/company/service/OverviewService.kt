package br.com.dashboard.company.service

import br.com.dashboard.company.entities.`object`.Object
import br.com.dashboard.company.entities.`object`.Overview
import br.com.dashboard.company.exceptions.InternalErrorClient
import br.com.dashboard.company.exceptions.ResourceNotFoundException
import br.com.dashboard.company.repository.OverviewRepository
import br.com.dashboard.company.utils.common.ObjectStatus
import br.com.dashboard.company.utils.others.ConstantsUtils.ZERO_QUANTITY_ERROR
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Service
class OverviewService {

    @Autowired
    private lateinit var overviewRepository: OverviewRepository

    fun getOverview(
        objectId: Long,
        overviewId: Long
    ): Overview {
        val overviewSaved: Overview? = overviewRepository.findOverviewById(objectId = objectId, overviewId = overviewId)
        if (overviewSaved != null) {
            return overviewSaved
        } else {
            throw ResourceNotFoundException(message = OVERVIEW_NOT_FOUND)
        }
    }

    @Transactional
    fun saveOverview(
        status: ObjectStatus? = null,
        quantity: Int,
        objectResult: Object? = null
    ): Overview {
        if(quantity == 0) {
            throw InternalErrorClient(message = ZERO_QUANTITY_ERROR)
        }
        val overview = Overview(
            createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
            status = status,
            quantity = quantity,
            objectResult = objectResult
        )
        return overviewRepository.save(overview)
    }

    @Transactional
    fun deleteOverview(
        objectId: Long,
        overviewId: Long
    ) {
        overviewRepository.deleteOverviewById(objectId = objectId, overviewId = overviewId)
    }

    companion object {
        private const val OVERVIEW_NOT_FOUND = "Overview not found"
    }
}
