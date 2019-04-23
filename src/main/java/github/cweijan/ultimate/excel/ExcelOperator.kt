package github.cweijan.ultimate.excel

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
     * 读入excel文件，解析后返回
     * @param file
     * @throws IOException
     */
    @Throws(IOException::class)
    fun inputExcel(inputStream: InputStream): List<Array<String?>> {
        //检查文件
        //获得Workbook工作薄对象
        val workbook = HSSFWorkbook(inputStream)
        //创建返回对象，把每行中的值作为一个数组，所有行作为一个集合返回
        val list = ArrayList<Array<String?>>()
        if (workbook != null) {
            for (sheetNum in 0 until workbook.numberOfSheets) {
                //获得当前sheet工作表
                val sheet = workbook.getSheetAt(sheetNum) ?: continue
//获得当前sheet的开始行
                val firstRowNum = sheet.firstRowNum
                //获得当前sheet的结束行
                val lastRowNum = sheet.lastRowNum
                //循环除了第一行的所有行
                for (rowNum in firstRowNum + 1..lastRowNum) {
                    //获得当前行
                    val row = sheet.getRow(rowNum) ?: continue
//获得当前行的开始列
                    val firstCellNum = row.firstCellNum
                    //获得当前行的列数
                    val lastCellNum = row.physicalNumberOfCells
                    val cells = arrayOfNulls<String?>(lastCellNum)
                    //循环当前行
                    for (cellNum in firstCellNum until lastCellNum) {
                        val cell = row.getCell(cellNum)
                        cells[cellNum] = getCellValue(cell)
                    }
                    list.add(cells)
                }
            }
            workbook.close()
        }
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