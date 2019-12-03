package github.cweijan.ultimate.core.excel

import github.cweijan.ultimate.core.component.TableInfo
import github.cweijan.ultimate.util.Json
import github.cweijan.ultimate.util.Log
import github.cweijan.ultimate.util.ReflectUtils
import org.apache.poi.hssf.usermodel.HSSFCell
import org.apache.poi.hssf.usermodel.HSSFRow
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.VerticalAlignment
import org.springframework.beans.BeanUtils
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.*
import kotlin.collections.HashMap


@SuppressWarnings("all")
object ExcelOperator {

    private val numberPattern = Regex("^(-?\\d+)(\\.\\d+)?$")
    private val integerPattern = Regex("^[-\\+]?[\\d]*$")

    /**
     * 导出Excel
     * @param headers 标题
     * @param values 内容
     * @param exportPath excel导出路径
     * @return
     */
    @JvmStatic
    fun outputExcel(headers: List<String>, values: Array<ArrayList<Any?>>, exportPath: String): Boolean {

        checkPoiEnable()

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
                val valueCell = row.createCell(j)

                val contextstyle = wb.createCellStyle()
                contextstyle.setAlignment(HorizontalAlignment.CENTER)
                contextstyle.setVerticalAlignment(VerticalAlignment.CENTER)
                val cellValue = "$data"
                if (cellValue.matches(numberPattern) && !cellValue.contains("%")) {
                    val df = wb.createDataFormat()
                    if (cellValue.matches(integerPattern)) {
                        contextstyle.dataFormat = df.getFormat("#,#0")//数据格式只显示整数
                    } else {
                        contextstyle.dataFormat = df.getFormat("#,##0.00")//保留两位小数点
                    }
                    valueCell.setCellStyle(contextstyle)
                    valueCell.setCellValue(cellValue.toDouble())
                } else {
                    valueCell.setCellStyle(contextstyle)
                    valueCell.setCellValue(cellValue)
                }
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
     * apache poi没有打包进依赖,需要手动添加
     */
    private fun checkPoiEnable() {
        try {
            Class.forName("org.apache.poi.hssf.usermodel.HSSFWorkbook")
        } catch (e: ClassNotFoundException) {
            Log.error("You must manually add apache poi to dependency\n" +
                    "compile group: 'org.apache.poi', name: 'poi', version: '3.17'")
            throw e;
        }
    }

    /**
     * 读入excel文件，解析后返回解析对象
     * @throws IOException
     */
    @Throws(IOException::class)
    @JvmStatic
    @JvmOverloads
    fun <T> inputExcel(inputStream: InputStream, componentClass: Class<T>, skipRow: Int = 0): List<T> {
        checkPoiEnable()
        val workbook = HSSFWorkbook(inputStream)
        val list = ArrayList<T>()

        for (sheetNum in 0 until workbook.numberOfSheets) {
            val sheet = workbook.getSheetAt(sheetNum) ?: continue
            val headerRow = sheet.getRow(skipRow) ?: return list
            IntRange(sheet.firstRowNum + 1 + skipRow, sheet.lastRowNum + 1).forEach { rowNum ->
                //row
                val instance: T = BeanUtils.instantiateClass(componentClass)
                val row = sheet.getRow(rowNum) ?: return@forEach
                IntRange(row.firstCellNum.toInt(), row.lastCellNum.toInt()).forEach { cellNum ->
                    val header = getCellValue(headerRow.getCell(cellNum))
                    TableInfo.getComponent(componentClass).excelHeaderFieldMap[header]?.run {
                        getCellValue(row.getCell(cellNum)).let {
                            if (it != "") {
                                val field = ReflectUtils.getField(componentClass, this.name)
                                if (field != null) {
                                    ReflectUtils.setFieldValue(instance, field, ReflectUtils.convert(it, field.type))
                                }
                            }
                        }
                    }
                }
                list.add(instance)
            }
        }
        workbook.close()
        return list
    }

    private fun getCellValue(cell: Cell?): String {

        cell ?: return ""

        //判断数据的类型
        return when (cell.cellTypeEnum) {
            CellType.NUMERIC -> cell.numericCellValue.toLong().toString()
            CellType.STRING -> cell.stringCellValue.toString()
            CellType.BOOLEAN -> cell.booleanCellValue.toString()
            CellType.FORMULA -> cell.cellFormula.toString()
            CellType.BLANK -> ""
            CellType.ERROR -> "非法字符"
            else -> "未知类型"
        }
    }

}