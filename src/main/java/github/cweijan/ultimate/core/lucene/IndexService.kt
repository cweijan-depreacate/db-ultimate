package github.cweijan.ultimate.core.lucene

import github.cweijan.ultimate.core.page.Pagination
import github.cweijan.ultimate.util.Log
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.index.Term
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.Query
import org.apache.lucene.search.Sort
import org.apache.lucene.search.TopFieldCollector
import org.apache.lucene.store.Directory
import org.apache.lucene.store.FSDirectory
import java.io.File
import kotlin.collections.forEach as forEach1


/**
 * @author cweijan
 * @version 2019/7/16/016 15:33
 */
class IndexService(private val indexDirPath: String) {

    val directoryLazy = lazy {
        return@lazy HashMap<Class<*>, Directory>()
    }
    private val directoryMap: MutableMap<Class<*>, Directory> by directoryLazy

    val writerLazy = lazy {
        return@lazy HashMap<Class<*>, IndexWriter>()
    }
    private val writerMap: MutableMap<Class<*>, IndexWriter> by writerLazy

    val readerLazy = lazy {
        return@lazy HashMap<Class<*>, DirectoryReader>()
    }
    private val readerMap: MutableMap<Class<*>, DirectoryReader> by readerLazy

    private val searchMap: MutableMap<Class<*>, IndexSearcher> by lazy {
        return@lazy HashMap<Class<*>, IndexSearcher>()
    }

    fun getDiretory(clazz: Class<*>): Directory {
        if (!directoryMap.containsKey(clazz))
            directoryMap[clazz] = FSDirectory.open(File(indexDirPath+File.separator + clazz.name).toPath())
        return directoryMap[clazz]!!
    }

    fun getIndexWriter(clazz: Class<*>): IndexWriter {
        if (!writerMap.containsKey(clazz))
            writerMap[clazz] = IndexWriter(getDiretory(clazz), IndexWriterConfig(StandardAnalyzer()))
        return writerMap[clazz]!!
    }

    fun getIndexReader(clazz: Class<*>): DirectoryReader {
        if (!readerMap.containsKey(clazz))
            readerMap[clazz] = DirectoryReader.open(getDiretory(clazz))
        return readerMap[clazz]!!
    }

    fun getIndexSearcher(clazz: Class<*>): IndexSearcher {
        if (!searchMap.containsKey(clazz))
            searchMap[clazz] = IndexSearcher(getIndexReader(clazz))
        DirectoryReader.open(getDiretory(clazz))
        return searchMap[clazz]!!
    }

    init {
        Runtime.getRuntime().addShutdownHook(Thread {
            if (readerLazy.isInitialized()) {
                readerMap.forEach1 { (_, reader) -> reader.close() }
            }
            if (writerLazy.isInitialized()) {
                writerMap.forEach { _, writer -> writer.close() }
            }
            if (directoryLazy.isInitialized()) {
                directoryMap.forEach1 { (_, directory) -> directory.close() }
            }
        })
    }

    fun searchByQuery(query: Query, n: Int,clazz: Class<*>): ArrayList<Document>? {

        val scoreDocs = getIndexSearcher(clazz).search(query, n).scoreDocs
        if (scoreDocs != null && scoreDocs.isNotEmpty()) {
            val docs = ArrayList<Document>();
            for (scoreDoc in scoreDocs) {
                docs.add(getIndexSearcher(clazz).doc(scoreDoc.doc))
            }
            return docs
        }
        return null
    }

    /**
     * 搜索
     */
    fun <T> search(fields: Array<String>, luceneQuery: LuceneQuery<T>): Pagination<T> {

        val indexSearcher = getIndexSearcher(luceneQuery.componentClass)
        val pagination = Pagination<T>()
        pagination.list = ArrayList<T?>()
        pagination.pageSize = luceneQuery.pageSize ?: 100
        pagination.currentPage = luceneQuery.page ?: 1
        pagination.startPage = luceneQuery.page ?: 1

        //fields是当未指定field时的默认搜索field
        val queryParser = MultiFieldQueryParser(fields, StandardAnalyzer())
        queryParser.defaultOperator = QueryParser.AND_OPERATOR
        queryParser.fuzzyMinSim = 2f
        queryParser.allowLeadingWildcard = true

        val luceneSearch = luceneQuery.getLuceneSearch()
        Log.getLogger().debug("lucene query : $luceneSearch")
        val query = queryParser.parse(luceneSearch)

        val sort = Sort()
        if (luceneQuery.sortLazy.isInitialized()) {
            sort.setSort(*luceneQuery.sortFieldList.toTypedArray())
        }
        val collector = TopFieldCollector.create(sort, pagination.currentPage * pagination.pageSize, 0)

        indexSearcher.search(query, collector)
        val scoreDocs = collector.topDocs((pagination.currentPage - 1) * pagination.pageSize, pagination.pageSize).scoreDocs
        for (scoreDoc in scoreDocs) {
            val doc = indexSearcher.doc(scoreDoc.doc)
            pagination.list.add(LuceneHelper.documentToObject(doc, luceneQuery.componentClass))
        }
        pagination.count = collector.totalHits

        return pagination
    }

    fun addDocument(document: Document, javaClass: Class<*>) {
        getIndexWriter(javaClass).addDocument(document)
    }

    fun commit(clazz: Class<*>) {
        getIndexWriter(clazz).commit()
        val reader = DirectoryReader.openIfChanged(getIndexReader(clazz))
        if (reader!=null) {
            readerMap[clazz]=reader
            searchMap[clazz]=IndexSearcher(reader)
        }
    }

    fun updateDocument(term: Term, document: Document,clazz: Class<*>) {
        getIndexWriter(clazz).updateDocument(term, document)
        getIndexWriter(clazz).commit()
    }

    fun deleteByQuery(query: Query,clazz: Class<*>){
        getIndexWriter(clazz).deleteDocuments(query)
    }

    /**
     * 删除索引
     */
    fun deleteAllIndex(clazz: Class<*>) {
        getIndexWriter(clazz).deleteAll()
        commit(clazz)
    }

}
