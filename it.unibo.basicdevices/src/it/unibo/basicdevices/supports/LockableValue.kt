package it.unibo.basicdevices.supports

import java.util.concurrent.locks.ReentrantLock
import java.util.concurrent.locks.Lock

class LockableValue<T>(v : T) {
	
	private var value = v
	private val lock = ReentrantLock()
	
	
	fun safeGet() : T {
		lock.lock()
		try {
			return value
		} finally {lock.unlock()}
	}
	
	fun get() : T {
		return value;
	}
	
	fun safeSet(value : T) {
		lock.lock()
		try {
			this.value = value			
		} finally {lock.unlock()}
	}
}