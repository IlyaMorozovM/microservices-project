import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should create song metadata when valid request is sent"

    request {
        method POST()
        url '/songs'
        headers {
            contentType(applicationJson())
        }
        body(
                id: 1,
                name: "We are the champions",
                artist: "Queen",
                album: "News of the world",
                duration: "02:59",
                year: "1977"
        )
    }

    response {
        status OK()
        headers {
            contentType(applicationJson())
        }
        body(
                id: 1
        )
    }
}