package br.com.dashboard.company.repository

import br.com.dashboard.company.entities.food.Food
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface FoodRepository : JpaRepository<Food, Long> {

    @Query("SELECT f FROM Food f WHERE f.company.id = :companyId AND f.name = :foodName")
    fun checkNameFoodAlreadyExists(
        @Param("companyId") companyId: Long? = null,
        @Param("foodName") foodName: String
    ): Food?

    @Query(
        value = """
        SELECT f FROM Food f
            WHERE f.company.id = :companyId
        AND (:foodName IS NULL OR LOWER(f.name) LIKE LOWER(CONCAT('%', :foodName, '%')))
    """
    )
    fun findAllFoods(
        @Param("companyId") companyId: Long? = null,
        @Param("foodName") foodName: String?,
        pageable: Pageable
    ): Page<Food>?

    @Query(value = "SELECT f FROM Food f WHERE f.company.id = :companyId AND f.id = :foodId")
    fun findFoodById(
        @Param("companyId") companyId: Long? = null,
        @Param("foodId") foodId: Long
    ): Food?

    @Query(
        value = "SELECT f FROM Food f WHERE f.company.id = :companyId AND LOWER(f.name) LIKE LOWER(CONCAT('%', :foodName, '%'))"
    )
    fun findFoodByName(
        @Param("companyId") companyId: Long? = null,
        @Param("foodName") foodName: String
    ): List<Food>

    @Modifying
    @Query("UPDATE Food f SET f.price =:price WHERE f.company.id = :companyId AND f.id =:id")
    fun updatePriceFood(
        @Param("companyId") companyId: Long? = null,
        @Param("id") idFood: Long,
        @Param("price") price: Double
    )

    @Modifying
    @Query(value = "DELETE FROM Food f WHERE f.id = :foodId AND f.company.id = :companyId")
    fun deleteFoodById(
        @Param("companyId") companyId: Long? = null,
        @Param("foodId") foodId: Long
    ): Int
}
