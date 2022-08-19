package com.bluetriangle.analytics.networkcapture

enum class RequestType {
    audio, example, font, image, message, model, multipart, video, css, csv, html, javascript, json, xml, zip, other;

    companion object {
        fun fromFilePath(filePath: String): RequestType {
            return when (filePath.substringAfterLast(".", "")) {
                "css" -> css
                "html" -> html
                "js" -> javascript
                "json" -> json
                "xml" -> xml
                "jpg", "jpeg", "png", "gif", "svg", "tif", "tiff" -> image
                else -> other
            }
        }
    }
}
