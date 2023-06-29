package api

import io.kotest.core.spec.style.FunSpec
import org.testcontainers.containers.GenericContainer

class AktivitetRoutesTest(private val forebyggingsplanContainer: GenericContainer<*>): FunSpec({
    test("fullfør aktivitet returnerer 401 ved manglende autentisering") {

    }

    test("fullfør aktivitet returnerer 401 hvis autoriseringen feiler") {

    }
})