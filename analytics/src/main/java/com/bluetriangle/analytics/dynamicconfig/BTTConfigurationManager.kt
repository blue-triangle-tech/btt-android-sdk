package com.bluetriangle.analytics.dynamicconfig

import com.bluetriangle.analytics.dynamicconfig.repository.IBTTConfigurationRepository

internal class BTTConfigurationManager(
    val configurationRepository: IBTTConfigurationRepository,
    val configurationUpdatesListener: ConfigurationUpdatesListener) {

    init {
    }

}