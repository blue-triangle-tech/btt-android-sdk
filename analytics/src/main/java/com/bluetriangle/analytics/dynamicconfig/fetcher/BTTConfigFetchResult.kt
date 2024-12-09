package com.bluetriangle.analytics.dynamicconfig.fetcher

import com.bluetriangle.analytics.dynamicconfig.model.BTTRemoteConfiguration
import com.bluetriangle.analytics.dynamicconfig.reporter.BTTConfigFetchError

internal sealed class BTTConfigFetchResult {
    class Success(val config: BTTRemoteConfiguration):BTTConfigFetchResult()
    class Failure(val error: BTTConfigFetchError):BTTConfigFetchResult()
}