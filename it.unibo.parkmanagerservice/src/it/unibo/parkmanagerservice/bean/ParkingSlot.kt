package it.unibo.parkmanagerservice.bean

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "PARKINGSLOT")
data class ParkingSlot(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    val slotnum : Long,

    @Column(name = "SLOTSTATE", nullable = false)
    @Enumerated(value = EnumType.STRING)
    var slotstate : ParkingSlotState,

    @Column(name="TOKEN", nullable = true)
    var token : String?,

    @OneToOne(cascade = [CascadeType.DETACH], fetch = FetchType.EAGER)
    @JoinColumn(name = "USERID", referencedColumnName = "ID")
    var user : User?

)
