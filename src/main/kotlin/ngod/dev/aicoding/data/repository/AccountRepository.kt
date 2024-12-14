package ngod.dev.aicoding.data.repository

import ngod.dev.aicoding.data.entity.Account
import ngod.dev.aicoding.data.projectrion.AccountProjection
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.Optional
import java.util.UUID

@Repository
interface AccountRepository :JpaRepository<Account,Long>{
    fun findAllBy():List<AccountProjection>
    fun findAccountByUid(uid:UUID):Optional<AccountProjection>
    fun findByUid(uid:UUID):Optional<Account>
    fun findAccountById(id:Long):Optional<AccountProjection>
    fun findAllById(id:Long):AccountProjection
    fun existsByUserId(userId:String):Boolean
    fun findByUserId(userId:String):Optional<Account>
    @Modifying
    @Transactional
    @Query("UPDATE Account a SET a.exp = a.exp + :exp WHERE a.id = :id")
    fun updateExpById(id: Long, exp: Long): Int
}