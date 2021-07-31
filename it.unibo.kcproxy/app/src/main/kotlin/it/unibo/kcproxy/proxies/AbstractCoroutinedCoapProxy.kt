package it.unibo.kcproxy.proxies

import com.diogonunes.jcolor.AnsiFormat
import com.diogonunes.jcolor.Attribute
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
    private var channel : BroadcastChannel<String> = BroadcastChannel(20)

    private val violet = AnsiFormat(Attribute.TEXT_COLOR(150, 90, 150))

    protected abstract fun implStartProxy2()
    protected abstract fun implStopProxy2()

    protected fun newCoapReceiveChannel() : ReceiveChannel<String> {
        return channel.openSubscription()
    }

    protected fun getScope(): CoroutineScope {
        return scope
    }

    init {
        println(violet.format("AbstractCoroutinedCoapProxy[$coapUrl] | Created coroutine scope $scope"))
    }

    override fun implStartProxy() {
        channel = BroadcastChannel(10)
        implStartProxy2()
    }

    override fun implStopProxy() {
        scope.cancel()

        implStopProxy2()
    }

    override fun onCoap(msg: String) {
        scope.launch { channel.send(msg) }
    }
}