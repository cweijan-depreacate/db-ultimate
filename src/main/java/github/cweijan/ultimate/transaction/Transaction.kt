package github.cweijan.ultimate.transaction

import github.cweijan.ultimate.db.config.DbConfig
import github.cweijan.ultimate.util.Log
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.TransactionStatus

import java.sql.Connection
import java.sql.SQLException
import kotlin.math.log

class Transaction(var dbConfig: DbConfig) : PlatformTransactionManager {

    override fun getTransaction(definition: TransactionDefinition?): TransactionStatus {
        Log.info("gettransd")
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun begin() {
        Log.info("begin")
        dbConfig.openConnection().autoCommit = false
    }

    override fun commit(status: TransactionStatus) {
        Log.info("commit")
        dbConfig.openConnection().commit()
    }

    override fun rollback(status: TransactionStatus) {
        Log.info("rollback")
        dbConfig.openConnection().rollback()//事务提交
    }

}
