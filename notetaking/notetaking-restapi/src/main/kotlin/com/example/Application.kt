package com.example


import com.example.notetaking.dataaccess.NoteTakingDataAccessService
import com.example.notetaking.dataaccess.NoteTakingDataAccessServiceImpl
import com.example.notetaking.repository.NotesRepository
import com.example.notetaking.repository.NotesRepositoryImpl
import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.application.log
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.response.respondText
import io.ktor.routing.Routing
import org.koin.dsl.module
import org.koin.experimental.builder.singleBy
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.inject


val noteTakingAppModule = module(createdAtStart = true) {
    singleBy<NoteTakingDataAccessService, NoteTakingDataAccessServiceImpl>()
    singleBy<NotesRepository, NotesRepositoryImpl>()
}

fun main(args: Array<String>) {
    io.ktor.server.cio.EngineMain.main(args)
}

val NoteTakingContentV1 = ContentType("application", "vnd.notetakingapi.v1+json")

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    install(Koin) {
        modules(noteTakingAppModule)
    }

    val noteTakingDataAccessService by inject<NoteTakingDataAccessService>()

    moduleWithDependencies(noteTakingDataAccessService)
}


fun Application.moduleWithDependencies(noteTakingDataAccessService: NoteTakingDataAccessService) {
    install(Routing) {
        trace { application.log.trace(it.buildText()) }
        noteTakingApi(noteTakingDataAccessService)
    }


    install(StatusPages) {
        this.exception<Throwable> { e ->
            call.respondText(e.localizedMessage, ContentType.Text.Plain, HttpStatusCode.InternalServerError)
            throw e
        }
    }

    install(ContentNegotiation) {

        jackson(NoteTakingContentV1) {
            enable(SerializationFeature.INDENT_OUTPUT)
        }

        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }
}
