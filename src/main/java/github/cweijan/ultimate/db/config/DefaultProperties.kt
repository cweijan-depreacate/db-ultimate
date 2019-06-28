package github.cweijan.ultimate.db.config

import github.cweijan.ultimate.db.init.generator.TableAutoMode

object DefaultProperties {

    internal val DEFAULT_TABLE_MODE=TableAutoMode.none
    internal const val MAXIUM_POOL_SIZE = 20
    internal const val MINIUM_IDEL_SIZE = 5
    internal const val SHOW_SQL = true
    internal const val ENABLE =true
    internal const val DEVELOP =false

}
