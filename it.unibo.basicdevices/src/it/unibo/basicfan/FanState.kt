package it.unibo.basicfan

enum class FanState {
	ON, OFF;
	
	companion object{
		fun fromBoolean(value : Boolean) : FanState {
			if (value==true)
				return ON
			else
				return OFF
		}
	}
	
}