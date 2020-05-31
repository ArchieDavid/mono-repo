package hotels

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveParameters
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.put
import io.ktor.routing.route


fun Route.hotelRouter(hotelService: HotelService) {
    route("/hotels") {
        post {
            with(call) {
                val parameters = receiveParameters()
                val name = requireNotNull(parameters["name"])
                val age = parameters["age"]?.toInt()

                val hotelId = hotelService.create(name, age)
                respond(HttpStatusCode.Created, hotelId)
            }
        }

        get("/{id}"){
            with(call){
                val id = requireNotNull(parameters["id"]).toInt()
                val hotel = hotelService.findById(id)

                if(hotel == null){
                    respond(HttpStatusCode.NotFound)
                }else{
                    respond(hotel)
                }
            }
        }

        put("/{id}"){
            with(call) {
                val id = requireNotNull(parameters["id"]).toInt()

                val parameters = receiveParameters()
                val name = requireNotNull(parameters["name"])
                val age = parameters["age"]?.toInt()

                hotelService.update(id, name, age)
            }
        }

        delete("/{id}"){
            with(call){
                val id = requireNotNull(parameters["id"]).toInt()
                 hotelService.deleteById(id)
            }
        }

        get {
            call.respond(hotelService.all())
        }
    }
}
