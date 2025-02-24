package br.com.digital.order.service

import br.com.digital.order.entities.day.Day
import br.com.digital.order.exceptions.ObjectDuplicateException
import br.com.digital.order.exceptions.ResourceNotFoundException
import br.com.digital.order.repository.DayRepository
import br.com.digital.order.utils.common.DayOfWeek
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DayService {

    @Autowired
    private lateinit var dayRepository: DayRepository

    fun getDay(
        dayId: Long,
        feeId: Long
    ): Day {
        val feeSaved: Day? = dayRepository.findDayById(dayId = dayId, feeId = feeId)
        if (feeSaved != null) {
            return feeSaved
        } else {
            throw ResourceNotFoundException(message = DAY_NOT_FOUND)
        }
    }

    fun saveDay(
        fee: br.com.digital.order.entities.fee.Fee,
        day: DayOfWeek
    ): Day {
        if (checkNameDayAlreadyExists(feeId = fee.id, day = day)) {
            throw ObjectDuplicateException(message = DUPLICATE_DAY)
        } else {
            val dayCreated = Day(day = day, fee = fee)
            return dayRepository.save(dayCreated)
        }
    }

    private fun checkNameDayAlreadyExists(
        feeId: Long,
        day: DayOfWeek
    ): Boolean {
        val dayResult = dayRepository.checkDayAlreadyExists(feeId = feeId, day = day)
        return dayResult != null
    }

    @Transactional
    fun deleteDay(
        feeId: Long,
        dayId: Long
    ) {
        getDay(dayId = dayId, feeId = feeId)
        dayRepository.deleteDayById(dayId = dayId, feeId = feeId)
    }

    companion object {
        const val DUPLICATE_DAY = "The ay already exists!"
        const val DAY_NOT_FOUND = "Day not found!"
    }
}
