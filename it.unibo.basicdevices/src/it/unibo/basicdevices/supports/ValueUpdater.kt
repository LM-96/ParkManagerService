package it.unibo.basicdevices.supports

import java.io.Closeable

interface ValueUpdater<out T> : Closeable, AutoCloseable {
	
	fun getActual() : T
	fun start()
	fun suspend()
	
	override fun close()
}