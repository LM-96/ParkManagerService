package it.unibo.basicfan

object FanFactory {
	
	@JvmStatic
	fun create(id: String, type : FanType, address : String?) : Fan? {
		return when(type) {
			FanType.VIRTUAL -> if(address != null) WsFan(id, address) else null
		}
	}
}