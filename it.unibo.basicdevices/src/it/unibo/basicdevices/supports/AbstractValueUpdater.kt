package it.unibo.basicdevices.supports

abstract class AbstractValueUpdater<T>(value: T) : ValueUpdater<T> {
	
	protected val value = LockableValue(value);
	
	override fun getActual(): T {
		return value.safeGet()
	}
}