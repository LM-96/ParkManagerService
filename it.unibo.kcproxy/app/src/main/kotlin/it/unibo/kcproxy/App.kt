/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package it.unibo.kcproxy

import it.unibo.kcproxy.configuration.ConfigLoader
import it.unibo.kcproxy.proxies.ProxyConfigurationBuilder
import it.unibo.kcproxy.proxies.ProxyContainer
import kotlinx.coroutines.runBlocking

fun main() {

    println("────────────────────────────────────────\n" +
            "─────────────▒░───▒▒▒▒▒──▒▒▒────────────\n" +
            "──────────────▒▒▒░▒▒▒▒▒▒▒▒──────────────\n" +
            "─────────────▒▒▒▒░▒▒▒▒▒▒▒▒▒▒────────────\n" +
            "──────────▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▓─────────\n" +
            "───────░▟█▛▀▓──────────────▝▝▀██▖───────\n" +
            "─▄▄▗──▟█▛▘────────────────────▝▗▄▄▖─▄▄▄▖\n" +
            "─▀▜██████████████████████████████████▗▘▘\n" +
            "──▄▄▜██████████████████████████████▛▜█▌─\n" +
            "─▐██──▀▀████████████████████████▛▀▘─▄█▄─\n" +
            "─▐███▖───██▛▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▓▄█▌──▗▟██▟─\n" +
            "─▗██████████▟▄▄▄▄▄▄▄▄▄▄▄▄▄▄▟███████████─\n" +
            "─██████████████████████████████████████─\n" +
            "─▜███████████▛▀▀▀▀▀▀▀▀▀▀▀▀▀██████▄░▄▗▄█─\n" +
            "──▝▀▀▀▀▀▀▀▀▀▘───────────────▀▀▀▀▀▀▀▀▀▓──\n" +
            "──▒──▒▒▒▒▒▒▒▒░▒▒▒▒░░▒▒▒▒▒▒▒▒▒▒▒▒▒▒░▒▒▒░─\n" +
            "────────────────────────────────────────\n" +
            "────────────────────────────────────────")
    println("______ _________________                             \n" +
            "___  //_/_  ____/__  __ \\________________  ______  __\n" +
            "__  ,<  _  /    __  /_/ /_  ___/  __ \\_  |/_/_  / / /\n" +
            "_  /| | / /___  _  ____/_  /   / /_/ /_>  < _  /_/ / \n" +
            "/_/ |_| \\____/  /_/     /_/    \\____//_/|_| _\\__, /  \n" +
            "                                            /____/   ")

    var configs = ConfigLoader.load().getEntries()
    println("KCProxy | Completed loading configuration")

    var proxies = ProxyConfigurationBuilder.buildProxies(configs)
    println("KCProxy | All proxies are builded")

    ProxyContainer.registerAll(proxies)
    println("KCProxy | All proxies registered")

    runBlocking {
        ProxyContainer.startAll()
        println("KCProxy | All proxies started")
    }
    println("KCProxy | Press a key or CTRL+C/D to stop")

    readLine()
    ProxyContainer.stopAll()
    println("KCProxy | Terminated")
}
