 
package app.getnuri.data

import app.getnuri.model.ImageValidationError as ModelImageValidationError

class InsufficientInformationException(errorMessage: String? = null) : Exception(errorMessage)

class ImageValidationException(val imageValidationError: ImageValidationError? = null) : Exception(imageValidationError.toString())

class ImageDescriptionFailedGenerationException() : Exception()
class NoInternetException : Exception("No internet connection")

enum class ImageValidationError {
    NOT_PERSON,
    NOT_ENOUGH_DETAIL,
    POLICY_VIOLATION,
    OTHER,
}

fun ModelImageValidationError.toImageValidationError(): app.getnuri.data.ImageValidationError {
    return when (this) {
        ModelImageValidationError.NOT_PERSON -> app.getnuri.data.ImageValidationError.NOT_PERSON
        ModelImageValidationError.NOT_ENOUGH_DETAIL -> app.getnuri.data.ImageValidationError.NOT_ENOUGH_DETAIL
        ModelImageValidationError.POLICY_VIOLATION -> app.getnuri.data.ImageValidationError.POLICY_VIOLATION
        ModelImageValidationError.OTHER -> app.getnuri.data.ImageValidationError.OTHER
    }
}
