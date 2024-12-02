package ngod.dev.aicoding.data.entity

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import lombok.NoArgsConstructor
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime
import java.util.UUID

@Entity
data class Account(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id:Long?=null,
    @Column
    var uid:UUID?=null,
    @Column(unique = true, nullable = false)
    var userId:String="",
    @Column(nullable = false)
    var password:String="",
    @Column()
    var nickname:String="",
    @Column()
    var profileUrl:String?=null,
    @CreationTimestamp
    @Column
    var createdAt:LocalDateTime = LocalDateTime.now(),
    @Column
    var cash:Long=0,
    @Column
    var exp:Long=0,
)
