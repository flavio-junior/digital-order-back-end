package br.com.digital.order.repository

import br.com.digital.order.entities.item.Item
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ItemRepository : JpaRepository<Item, Long> {

    @Query(value = "SELECT i FROM Item i WHERE i.company.id = :companyId AND i.name = :name")
    fun checkNameItemAlreadyExists(
        @Param("companyId") companyId: Long? = null,
        @Param("name") name: String
    ): Item?

    @Query(
        value =
            """
            SELECT i FROM Item i
                WHERE i.company.id = :companyId
            AND (:name IS NULL OR LOWER(i.name) LIKE LOWER(CONCAT('%', :name, '%')))
            """
    )
    fun findAllItems(
        @Param("companyId") companyId: Long? = null,
        @Param("name") name: String?, pageable: Pageable
    ): Page<Item>?

    @Query(value = "SELECT i FROM Item i WHERE i.company.id = :companyId AND i.id = :idItem")
    fun findItemById(
        @Param("companyId") companyId: Long? = null,
        @Param("idItem") itemId: Long
    ): Item?

    @Query(
        value = "SELECT i FROM Item i WHERE i.company.id = :companyId AND LOWER(i.name) LIKE LOWER(CONCAT('%', :name, '%'))"
    )
    fun findItemByName(
        @Param("companyId") companyId: Long? = null,
        @Param("name") name: String?
    ): List<Item>

    @Modifying
    @Query(value = "UPDATE Item i SET i.price =:price WHERE i.company.id = :companyId AND i.id =:idItem")
    fun updateItemPrice(
        @Param("companyId") companyId: Long? = null,
        @Param("idItem") idItem: Long,
        @Param("price") price: Double
    )

    @Modifying
    @Query(value = "DELETE FROM Item i WHERE i.id = :itemId AND i.company.id = :companyId")
    fun deleteItemById(
        @Param("companyId") companyId: Long? = null,
        @Param("itemId") itemId: Long
    ): Int
}
