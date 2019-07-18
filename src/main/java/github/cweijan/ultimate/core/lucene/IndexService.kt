package github.cweijan.ultimate.core.lucene

import github.cweijan.ultimate.core.page.Pagination
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.index.Term
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.*
import org.apache.lucene.store.Directory
import org.apache.lucene.store.FSDirectory
import java.io.File
import java.util.*

/**
 * @author cweijan
 * @version 2019/7/16/016 15:33
 */
class IndexService(indexDirPath: String) {

    private val directory: Directory by lazy {
        return@lazy FSDirectory.open(File(indexDirPath).toPath())
    }

    private val writerLazy = lazy {
        return@lazy IndexWriter(directory, IndexWriterConfig(StandardAnalyzer()))
    }
    private val indexWriter: IndexWriter by writerLazy

    private val readerLazy = lazy {
        return@lazy DirectoryReader.open(directory)
    }
    private val indexReader by readerLazy
//    openIfChanged

    private val indexSearcher by lazy {
        return@lazy IndexSearcher(indexReader)
    }

    init {
        Runtime.getRuntime().addShutdownHook(Thread {
            if (writerLazy.isInitialized()) indexWriter.close()
            if (readerLazy.isInitialized()) indexReader.close()
        })
    }

    /**
     * 搜索
     */
    fun <T> search(fields: Array<String>, luceneQuery: LuceneQuery<T>): Pagination<T> {

        val pagination = Pagination<T>()
        pagination.data = ArrayList<T?>()
        pagination.pageSize = luceneQuery.pageSize ?: 100
        pagination.currentPage = luceneQuery.page ?: 1

        val queryParser = MultiFieldQueryParser(fields, StandardAnalyzer())
        queryParser.defaultOperator = QueryParser.AND_OPERATOR
        queryParser.fuzzyMinSim = 2f
        queryParser.allowLeadingWildcard = true

        val query = queryParser.parse(luceneQuery.getLuceneSearch())

        // TODO 分页目前获取的总数是不对的
        val results = TopScoreDocCollector.create(pagination.currentPage * pagination.pageSize, 0)

        //TODO 排序
        val sort = Sort(SortedNumericSortField("github.cweijan.bean.VideoItem_id", SortField.Type.LONG))
        val r = TopFieldCollector.create(sort, pagination.currentPage * pagination.pageSize, 0)

        indexSearcher.search(query, results)
        val scoreDocs = results.topDocs((pagination.currentPage - 1) * pagination.pageSize, pagination.pageSize).scoreDocs
        for (scoreDoc in scoreDocs) {
            val doc = indexSearcher.doc(scoreDoc.doc)
            pagination.data.add(LuceneHelper.documentToObject(doc, luceneQuery.componentClass))
        }
        pagination.count = results.totalHits

        return pagination
    }

    fun addDocument(document: Document) {
        indexWriter.addDocument(document)
        indexWriter.commit()
    }

    fun updateDocument(id: Int?, document: Document) {
        val term = Term("id", id!!.toString() + "")
        indexWriter.updateDocument(term, document)
        indexWriter.commit()
    }

    fun deleteDocument(id: Int?) {
        val term = Term("id", id!!.toString() + "")
        indexWriter.deleteDocuments(term)
        indexWriter.commit()
    }

    /**
     * 删除索引
     */
    fun deleteAllIndex() {
        indexWriter.deleteAll()
        indexWriter.commit()
    }

}
