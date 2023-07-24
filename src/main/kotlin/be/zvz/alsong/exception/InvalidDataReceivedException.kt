package be.zvz.alsong.exception

import okhttp3.Response

class InvalidDataReceivedException(cause: Response) : RuntimeException(
    "Received data is invalid. This may be an passing API error. Please leave an issue if continues. code: ${cause.code}, message: ${cause.message}",
) {
    init {
        cause.close()
    }
}
