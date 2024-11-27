package com.bluetriangle.analytics.dynamicconfig.fetcher

import java.io.IOException

class InvalidResponseCode(val responseCode: Int, message: String?) : IOException(message)