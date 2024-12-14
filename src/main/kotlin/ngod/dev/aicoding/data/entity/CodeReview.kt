package ngod.dev.aicoding.data.entity

import jakarta.persistence.*

@Entity
data class CodeReview (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @Column
    var language:String,
    @Column(columnDefinition = "TEXT")
    var code:String,
    @Column(columnDefinition = "TEXT")
    var review:String,
)