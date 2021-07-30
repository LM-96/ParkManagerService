package it.unibo.kcproxy.proxies

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ReceiveChannel
import org.eclipse.californium.core.CoapClient
import org.eclipse.californium.core.CoapHandler
import org.eclipse.californium.core.CoapResponse
import java.io.BufferedWriter
import java.io.IOException
import java.io.OutputStreamWriter
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.jvm.Throws

@kotlinx.coroutines.ExperimentalCoroutinesApi
abstract class AbstractCoroutinedCoapProxy(coapUrl : String) : AbstractCoapProxy(coapUrl) {

    private val scope = CoroutineScope(EmptyCoroutineContext + CoroutineName("proxy[$coapUrl]"))
    private var channel : BroadcastChannel<String> = BroadcastChannel(10)
    private var queue = ArrayBlockingQueue<String>(10)

    protected abstract fun implStartProxy2()
    protected abstract fun implStopProxy2()

    protected fun newCoapReceiveChannel() : ReceiveChannel<String> {
        return channel.openSubscription()
    }

    protected fun getScope(): CoroutineScope {
        return scope
    }

    init {
        println("AbstractCoroutinedCoapProxy[$coapUrl] | Created coroutine scope $scope")
    }

    override fun implStartProxy() {
        channel = BroadcastChannel(10)
        startChannelWriter()

        implStartProxy2()
    }

    override fun implStopProxy() {
        stopChannelWriter()
        scope.cancel()

        implStopProxy2()
    }

    override fun onCoap(msg: String) {
        queue.put(msg)
    }

    private fun startChannelWriter() {
        scope.launch {
            while(true) {
                channel?.send(queue.take())
            }
        }
    }

    private fun stopChannelWriter() {
        runBlocking {
            var closing = scope.launch {
                channel?.close()
            }
            closing.join()
        }
    }
}