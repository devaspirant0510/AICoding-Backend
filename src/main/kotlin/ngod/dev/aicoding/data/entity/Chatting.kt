package ngod.dev.aicoding.data.entity

import jakarta.persistence.*
import ngod.dev.aicoding.data.entity.enum.MessageType

@Entity
data class Chatting (
    @Id
    var id:Long,
    @Column
    var message:String,
    @Enumerated(EnumType.STRING)
    var messageType:MessageType,
    @ManyToOne
    var account: Account,
    @ManyToOne
    var content:BaseContent
)