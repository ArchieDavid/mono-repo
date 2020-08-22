package com.example.notetaking.web

import com.example.notetaking.service.NoteTakingService
import com.example.notetaking.service.NoteTakingServiceImpl
import com.github.mustachejava.DefaultMustacheFactory
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.application.log
import io.ktor.features.StatusPages
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.mustache.Mustache
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.koin.dsl.module
import org.koin.experimental.builder.singleBy
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.inject


val noteTakingWebModule = module(createdAtStart = true) {
    singleBy<NoteTakingService, NoteTakingServiceImpl>()
}


fun main(args: Array<String>) {
    embeddedServer(Netty, commandLineEnvironment(args)).start()
}


@Suppress("unused") // Referenced in application.conf
fun Application.module() {
    install(Koin) {
        modules(noteTakingWebModule)
    }

    val noteTakingService by inject<NoteTakingService>()
    moduleWithDependencies(noteTakingService)
}

fun Application.moduleWithDependencies(noteTakingService: NoteTakingService){

    install(StatusPages) {
        when {
            isDev -> {
                this.exception<Throwable> { e ->
                    call.respondText(e.localizedMessage, ContentType.Text.Plain, HttpStatusCode.InternalServerError)
                    throw e
                }
            }

            isTest -> {
                this.exception<Throwable> { e ->
                    call.response.status(HttpStatusCode.InternalServerError)
                    throw e
                }
            }

            isProd -> {
                this.exception<Throwable> { e ->
                    call.respondText(e.localizedMessage, ContentType.Text.Plain, HttpStatusCode.InternalServerError)
                    throw e
                }
            }
        }
    }

    install(Mustache) {
        mustacheFactory = DefaultMustacheFactory("templates")
    }


    install(Routing) {
        if (isDev) trace {
            application.log.trace(it.buildText())
        }

        notes(noteTakingService)
        staticResources()
    }
}

data class UserSession(val name: String)


val Application.envKind get() = environment.config.property("ktor.environment").getString()
val Application.isDev get() = envKind == "dev"
val Application.isTest get() = envKind == "test"
val Application.isProd get() = envKind != "dev" && envKind != "test"

