package github.cweijan.ultimate.excel

import github.cweijan.ultimate.component.TableInfo
import github.cweijan.ultimate.json.Json
import github.cweijan.ultimate.util.Log
import org.apache.poi.hssf.usermodel.HSSFCell
import org.apache.poi.hssf.usermodel.HSSFRow
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.VerticalAlignment
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.*
import kotlin.collections.HashMap

object ExcelOperator {

    /**
     * 导出Excel
     * @param headers 标题
     * @param values 内容
     * @param exportPath excel导出路径
     * @return
     */
    fun outputExcel(headers: Array<String>, values: Array<ArrayList<Any?>>, exportPath: String): Boolean {

        val wb = HSSFWorkbook()
        val sheet = wb.createSheet("sheet1")
        var row: HSSFRow

        val style = wb.createCellStyle()
        style.setAlignment(HorizontalAlignment.CENTER)
        style.setVerticalAlignment(VerticalAlignment.CENTER)
        var cell: HSSFCell?
        values.forEachIndexed { i, rowData ->
            row = sheet.createRow(i + 1)
            rowData.forEachIndexed { j, data ->
                row.createCell(j).setCellValue(data as String)
            }
        }

        row = sheet.createRow(0)
        headers.forEachIndexed { i, header ->
            cell = row.createCell(i)
            cell!!.setCellValue(header)
            cell!!.setCellStyle(style)
            sheet.autoSizeColumn(i)
        }

        try {
            wb.write(File(exportPath))
        } catch (e: Exception) {
            Log.error(e.message, e)
            return false
        }
        return true
    }

    /**
     * 读入excel文件，解析后返回解析对象
     * @throws IOException
     */
    @Throws(IOException::class)
    fun <T> inputExcel(inputStream: InputStream, componentClass: Class<T>): List<T> {

        val workbook = HSSFWorkbook(inputStream)
        val list = ArrayList<T>()

        for (sheetNum in 0 until workbook.numberOfSheets) {
            val sheet = workbook.getSheetAt(sheetNum) ?: continue
            val headerRow = sheet.getRow(0) ?: return list
            IntRange(sheet.firstRowNum + 1, sheet.lastRowNum + 1).forEach { rowNum ->
                val tempDataMap = HashMap<String, Any>()
                val row = sheet.getRow(rowNum) ?: return@forEach
                IntRange(row.firstCellNum.toInt(), row.physicalNumberOfCells).forEach { cellNum ->
                    val header = getCellValue(headerRow.getCell(cellNum))
                    TableInfo.getComponent(componentClass).excelHeaderFieldMap[header]?.run {
                        getCellValue(row.getCell(cellNum)).let { if (it != "") tempDataMap[this.name] = it }
                    }
                }

                if (tempDataMap.keys.size > 0) {
                    val toObject = Json.jsonToObject(Json.objectToJson(tempDataMap)!!, componentClass)!!
                    list.add(toObject)
                }
            }
        }
        workbook.close()
        return list
    }

    fun getCellValue(cell: Cell?): String {
        var cellValue = ""
        if (cell == null) {
            return cellValue
        }
        //把数字当成String来读，避免出现1读成1.0的情况
        if (cell.cellType === Cell.CELL_TYPE_NUMERIC) {
            cell.cellType = Cell.CELL_TYPE_STRING
        }
        //判断数据的类型
        return when (cell.cellType) {
            Cell.CELL_TYPE_NUMERIC -> cell.numericCellValue.toString()
            Cell.CELL_TYPE_STRING -> cell.stringCellValue.toString()
            Cell.CELL_TYPE_BOOLEAN -> cell.booleanCellValue.toString()
            Cell.CELL_TYPE_FORMULA -> cell.cellFormula.toString()
            Cell.CELL_TYPE_BLANK -> ""
            Cell.CELL_TYPE_ERROR -> "非法字符"
            else -> "未知类型"
        }
    }

}