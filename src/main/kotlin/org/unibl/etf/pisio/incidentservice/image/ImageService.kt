package org.unibl.etf.pisio.incidentservice.image

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.unibl.etf.pisio.incidentservice.incident.ImageResponse

@Service
class ImageService(@Qualifier("imageProcessorClient") private val imageProcessorClient: WebClient) {
    suspend fun uploadImage(image: FilePart) = imageProcessorClient.post()
        .contentType(MediaType.MULTIPART_FORM_DATA)
        .body(BodyInserters.fromMultipartData("image", image))
        .retrieve()
        .awaitBody<ImageResponse>()
}