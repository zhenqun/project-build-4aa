/**
 * Copyright &copy; 2012-2013 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.ido85.party.aaaa.mgmt.security.utils.excel;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.ido85.party.aaaa.mgmt.security.utils.Encodes;
import com.ido85.party.aaaa.mgmt.security.utils.Reflections;
import com.ido85.party.aaaa.mgmt.security.utils.StringUtils;

/**
 * 导出Excel文件（导出“XLSX”格式，支持大数据量导出 @see org.apache.poi.ss.SpreadsheetVersion）
 * 
 * @author ThinkGem
 * @version 2013-04-21
 */
public class ExportCompareExcel {

	private static Logger log = LoggerFactory.getLogger(ExportCompareExcel.class);

	/**
	 * 工作薄对象
	 */
	private SXSSFWorkbook wb;

	/**
	 * 工作表对象
	 */
	private Sheet sheet;

	/**
	 * 样式列表
	 */
	private Map<String, CellStyle> styles;

	/**
	 * 当前行号
	 */
	private int rownum;
	
	/**
	 * 工作表对象 多个
	 */
	private List<Sheet> sheets = new ArrayList<>();
	
	/***
	 * 工作表数量
	 */
	private int sheetNum;

	/**
	 * 注解列表（Object[]{ ExcelField, Field/Method }）
	 */
	List<Object[]> annotationList = Lists.newArrayList();
	
	/**
	 * 注解列表（Object[]{ ExcelField, Field/Method }） 动态表头哦
	 */
	List<Integer> columns = Lists.newArrayList();///所有表头的hashCode

	/**
	 * 构造函数
	 * 
	 * @param title
	 *            表格标题，传“空值”，表示无标题
	 * @param cls
	 *            实体对象，通过annotation.ExportField获取标题
	 */
	public ExportCompareExcel(String title, Class<?> cls) {
		this(title, cls, 1);
	}

	/**
	 * 构造函数
	 * 
	 * @param title
	 *            表格标题，传“空值”，表示无标题
	 * @param cls
	 *            实体对象，通过annotation.ExportField获取标题
	 * @param type
	 *            导出类型（1:导出数据；2：导出模板）
	 * @param groups
	 *            导入分组
	 */
	public ExportCompareExcel(String title, Class<?> cls, int type, int... groups) {
		// Get annotation field
		Field[] fs = cls.getDeclaredFields();
		for (Field f : fs) {
			ExcelField ef = f.getAnnotation(ExcelField.class);
			if (ef != null && (ef.type() == 0 || ef.type() == type)) {//导入 还是导出
				if (groups != null && groups.length > 0) {
					boolean inGroup = false;
					for (int g : groups) {
						if (inGroup) {
							break;
						}
						for (int efg : ef.groups()) {
							if (g == efg) {
								inGroup = true;
								annotationList.add(new Object[] { ef, f });
								break;
							}
						}
					}
				} else {
					annotationList.add(new Object[] { ef, f });
				}
			}
		}
		// Get annotation method
		Method[] ms = cls.getDeclaredMethods();
		for (Method m : ms) {
			ExcelField ef = m.getAnnotation(ExcelField.class);
			if (ef != null && (ef.type() == 0 || ef.type() == type)) {
				if (groups != null && groups.length > 0) {
					boolean inGroup = false;
					for (int g : groups) {
						if (inGroup) {
							break;
						}
						for (int efg : ef.groups()) {
							if (g == efg) {
								inGroup = true;
								annotationList.add(new Object[] { ef, m });
								break;
							}
						}
					}
				} else {
					annotationList.add(new Object[] { ef, m });
				}
			}
		}
		// Field sorting
		Collections.sort(annotationList, new Comparator<Object[]>() {
			public int compare(Object[] o1, Object[] o2) {
				return new Integer(((ExcelField) o1[0]).sort()).compareTo(new Integer(((ExcelField) o2[0]).sort()));
			};
		});
		// Initialize
		List<String> headerList = Lists.newArrayList();
		for (Object[] os : annotationList) {
			String t = ((ExcelField) os[0]).title();
			// 如果是导出，则去掉注释
			if (type == 1) {
				String[] ss = StringUtils.split(t, "**", 2);
				if (ss.length == 2) {
					t = ss[0];
				}
			}
			headerList.add(t);
		}
		initialize(title, headerList);
	}
	
	/**
	 * 构造函数
	 * 
	 * @param title
	 *            表格标题，传“空值”，表示无标题
	 * @param cls
	 *            实体对象，通过annotation.ExportField获取标题
	 * @param type
	 *            导出类型（1:导出数据；2：导出模板）
	 * @param groups
	 *            导入分组
	 */
	public ExportCompareExcel(String title, Class<?> cls, int type, List<String> header, int... groups) {
		// Get annotation field
		Field[] fs = cls.getDeclaredFields();
		for (Field f : fs) {
			ExcelField ef = f.getAnnotation(ExcelField.class);
			if (ef != null && (ef.type() == 0 || ef.type() == type)) {//导入 还是导出
				if (groups != null && groups.length > 0) {
					boolean inGroup = false;
					for (int g : groups) {
						if (inGroup) {
							break;
						}
						for (int efg : ef.groups()) {
							if (g == efg) {
								inGroup = true;
								annotationList.add(new Object[] { ef, f });
								break;
							}
						}
					}
				} else {
					annotationList.add(new Object[] { ef, f });
				}
			}
		}
		// Get annotation method
		Method[] ms = cls.getDeclaredMethods();
		for (Method m : ms) {
			ExcelField ef = m.getAnnotation(ExcelField.class);
			if (ef != null && (ef.type() == 0 || ef.type() == type)) {
				if (groups != null && groups.length > 0) {
					boolean inGroup = false;
					for (int g : groups) {
						if (inGroup) {
							break;
						}
						for (int efg : ef.groups()) {
							if (g == efg) {
								inGroup = true;
								annotationList.add(new Object[] { ef, m });
								break;
							}
						}
					}
				} else {
					annotationList.add(new Object[] { ef, m });
				}
			}
		}
		// Field sorting
		Collections.sort(annotationList, new Comparator<Object[]>() {
			public int compare(Object[] o1, Object[] o2) {
				return new Integer(((ExcelField) o1[0]).sort()).compareTo(new Integer(((ExcelField) o2[0]).sort()));
			};
		});
		// Initialize
		List<String> headerList = Lists.newArrayList();
//		for (Object[] os : annotationList) {
//			String t = ((ExcelField) os[0]).title();
//			// 如果是导出，则去掉注释
//			if (type == 1) {
//				String[] ss = StringUtils.split(t, "**", 2);
//				if (ss.length == 2) {
//					t = ss[0];
//				}
//			}
//			headerList.add(t);
//		}
		
		headerList.addAll(header);
		
		initialize(title, headerList);
	}

	
	/**
	 * 构造函数
	 * 
	 * @param title
	 *            表格标题，传“空值”，表示无标题
	 * @param cls
	 *            实体对象，通过annotation.ExportField获取标题
	 * @param type
	 *            导出类型（1:导出数据；2：导出模板）
	 * @param groups
	 *            导入分组
	 * @param      sheetNum  一共需要多少个 sheet     
	 */
	public ExportCompareExcel(String title, Class<?> cls, int type, String sheetNum, int... groups) {
		// Get annotation field
		Field[] fs = cls.getDeclaredFields();
		for (Field f : fs) {
			ExcelField ef = f.getAnnotation(ExcelField.class);
			if (ef != null && (ef.type() == 0 || ef.type() == type)) {//导入 还是导出
				if (groups != null && groups.length > 0) {
					boolean inGroup = false;
					for (int g : groups) {
						if (inGroup) {
							break;
						}
						for (int efg : ef.groups()) {
							if (g == efg) {
								inGroup = true;
								annotationList.add(new Object[] { ef, f });
								break;
							}
						}
					}
				} else {
					annotationList.add(new Object[] { ef, f });
				}
			}
		}
		// Get annotation method
		Method[] ms = cls.getDeclaredMethods();
		for (Method m : ms) {
			ExcelField ef = m.getAnnotation(ExcelField.class);
			if (ef != null && (ef.type() == 0 || ef.type() == type)) {
				if (groups != null && groups.length > 0) {
					boolean inGroup = false;
					for (int g : groups) {
						if (inGroup) {
							break;
						}
						for (int efg : ef.groups()) {
							if (g == efg) {
								inGroup = true;
								annotationList.add(new Object[] { ef, m });
								break;
							}
						}
					}
				} else {
					annotationList.add(new Object[] { ef, m });
				}
			}
		}
		// Field sorting
		Collections.sort(annotationList, new Comparator<Object[]>() {
			public int compare(Object[] o1, Object[] o2) {
				return new Integer(((ExcelField) o1[0]).sort()).compareTo(new Integer(((ExcelField) o2[0]).sort()));
			};
		});
		// Initialize
		List<String> headerList = Lists.newArrayList();
		for (Object[] os : annotationList) {
			String t = ((ExcelField) os[0]).title();
			// 如果是导出，则去掉注释
			if (type == 1) {
				String[] ss = StringUtils.split(t, "**", 2);
				if (ss.length == 2) {
					t = ss[0];
				}
			}
			headerList.add(t);
		}
		initialize(title, headerList,Integer.valueOf(sheetNum).intValue());
	}
	
	/**
	 * 构造函数
	 * 
	 * @param title
	 *            表格标题，传“空值”，表示无标题
	 * @param cls
	 *            实体对象，通过annotation.ExportField获取标题
	 * @param type
	 *            导出类型（1:导出数据；2：导出模板）
	 * @param groups
	 *            导入分组
	 */
	public ExportCompareExcel(List<String> headerList, List<Integer> keys) {
		for (Integer item: keys) {
			columns.add(item);
		}
		
		initialize("", headerList);
	}
	
	/**
	 * 构造函数
	 * 
	 * @param title
	 *            表格标题，传“空值”，表示无标题
	 * @param headers
	 *            表头数组
	 */
	public ExportCompareExcel(String title, String[] headers) {
		initialize(title, Lists.newArrayList(headers));
	}

	/**
	 * 构造函数
	 * 
	 * @param title
	 *            表格标题，传“空值”，表示无标题
	 * @param headerList
	 *            表头列表
	 */
	public ExportCompareExcel(String title, List<String> headerList) {
		initialize(title, headerList);
	}

	/**
	 * 初始化函数
	 * 
	 * @param title
	 *            表格标题，传“空值”，表示无标题
	 * @param headerList
	 *            表头列表
	 */
	private void initialize(String title, List<String> headerList) {
		this.wb = new SXSSFWorkbook(500);
		this.sheet = wb.createSheet("Export");
		this.styles = createStyles(wb);
		// Create title
		if (StringUtils.isNotBlank(title)) {
			Row titleRow = sheet.createRow(rownum++);
			titleRow.setHeightInPoints(30);
			Cell titleCell = titleRow.createCell(0);
			titleCell.setCellStyle(styles.get("title"));
			titleCell.setCellValue(title);
			sheet.addMergedRegion(new CellRangeAddress(titleRow.getRowNum(), titleRow.getRowNum(), titleRow.getRowNum(),
					headerList.size() - 1));
		}
		// Create header
		if (headerList == null) {
			throw new RuntimeException("headerList not null!");
		}
		Row headerRow = sheet.createRow(rownum++);
		headerRow.setHeightInPoints(16);
		for (int i = 0; i < headerList.size(); i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellStyle(styles.get("header"));
			String[] ss = StringUtils.split(headerList.get(i), "**", 2);
			if (ss.length == 2) {
				cell.setCellValue(ss[0]);
				Comment comment = this.sheet.createDrawingPatriarch()
						.createCellComment(new XSSFClientAnchor(0, 0, 0, 0, (short) 3, 3, (short) 5, 6));
				comment.setString(new XSSFRichTextString(ss[1]));
				cell.setCellComment(comment);
			} else {
				cell.setCellValue(headerList.get(i));
			}
			sheet.autoSizeColumn(i);
		}
		for (int i = 0; i < headerList.size(); i++) {
			int colWidth = sheet.getColumnWidth(i) * 2;
			sheet.setColumnWidth(i, colWidth < 3000 ? 3000 : colWidth);
		}
		log.debug("Initialize success.");
	}
	
	/***
	 * 
	 * @param title
	 * 				表格标题，传“空值”，表示无标题
	 * @param headerList
	 * 					表头列表
	 * @param sheetNum
	 * 	
	 */
	private void initialize(String title, List<String> headerList,int sheetNum) {
		this.sheetNum = sheetNum;//需要生成的工作表的数量
		this.wb = new SXSSFWorkbook(500);
		this.styles = createStyles(wb);
		
		for(int i=0;i<sheetNum;i++){
			Sheet sheet = wb.createSheet("Export"+i);
			this.sheets.add(sheet);
		}
		if (headerList == null) {
			throw new RuntimeException("headerList not null!");
		}
		for(int i=0;i<this.sheets.size();i++){
			Sheet sheet = this.sheets.get(i);
			int rowNum =  this.getRowBySheet(i);
			// Create title
			if (StringUtils.isNotBlank(title)) {
				Row titleRow = sheet.createRow(rowNum);
				titleRow.setHeightInPoints(30);
				Cell titleCell = titleRow.createCell(0);
				titleCell.setCellStyle(styles.get("title"));
				titleCell.setCellValue(title);
				sheet.addMergedRegion(new CellRangeAddress(titleRow.getRowNum(), titleRow.getRowNum(), titleRow.getRowNum(),
						headerList.size() - 1));
				
			}
			//创建表头
			Row headerRow = sheet.createRow(rowNum);
			headerRow.setHeightInPoints(16);
			for (int j = 0; j < headerList.size(); j++) {
				Cell cell = headerRow.createCell(j);
				cell.setCellStyle(styles.get("header"));
				String[] ss = StringUtils.split(headerList.get(j), "**", 2);
				if (ss.length == 2) {
					cell.setCellValue(ss[0]);
					Comment comment = sheet.createDrawingPatriarch()
							.createCellComment(new XSSFClientAnchor(0, 0, 0, 0, (short) 3, 3, (short) 5, 6));
					comment.setString(new XSSFRichTextString(ss[1]));
					cell.setCellComment(comment);
				} else {
					cell.setCellValue(headerList.get(j));
				}
				sheet.autoSizeColumn(j);
			}
			for (int k = 0; k < headerList.size(); k++) {
				int colWidth = sheet.getColumnWidth(k) * 2;
				sheet.setColumnWidth(k, colWidth < 3000 ? 3000 : colWidth);
			}
		}
		
		
		log.debug("Initialize success.");
	}
	
	/***
	 * 得到指定工作表的行数
	 * @param num
	 * @return
	 */
	private int getRowBySheet(int num){
		return this.sheets.get(num).getLastRowNum();
	}

	/**
	 * 创建表格样式
	 * 
	 * @param wb
	 *            工作薄对象
	 * @return 样式列表
	 */
	private Map<String, CellStyle> createStyles(Workbook wb) {
		Map<String, CellStyle> styles = new HashMap<String, CellStyle>();

		CellStyle style = wb.createCellStyle();
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		Font titleFont = wb.createFont();
		titleFont.setFontName("Arial");
		titleFont.setFontHeightInPoints((short) 16);
		titleFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		style.setFont(titleFont);
		styles.put("title", style);

		style = wb.createCellStyle();
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		style.setBorderRight(CellStyle.BORDER_THIN);
		style.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
		style.setBorderLeft(CellStyle.BORDER_THIN);
		style.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
		style.setBorderTop(CellStyle.BORDER_THIN);
		style.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
		style.setBorderBottom(CellStyle.BORDER_THIN);
		style.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
		Font dataFont = wb.createFont();
		dataFont.setFontName("Arial");
		dataFont.setFontHeightInPoints((short) 10);
		style.setFont(dataFont);
		styles.put("data", style);

		style = wb.createCellStyle();
		style.cloneStyleFrom(styles.get("data"));
		style.setAlignment(CellStyle.ALIGN_LEFT);
		styles.put("data1", style);

		style = wb.createCellStyle();
		style.cloneStyleFrom(styles.get("data"));
		style.setAlignment(CellStyle.ALIGN_CENTER);
		styles.put("data2", style);

		style = wb.createCellStyle();
		style.cloneStyleFrom(styles.get("data"));
		style.setAlignment(CellStyle.ALIGN_RIGHT);
		styles.put("data3", style);

		style = wb.createCellStyle();
		style.cloneStyleFrom(styles.get("data"));
		style.setAlignment(CellStyle.ALIGN_LEFT);
		Font redFont = wb.createFont();
		redFont.setFontName("Arial");
		redFont.setFontHeightInPoints((short) 10);
		redFont.setColor(HSSFColor.RED.index);
		style.setFont(redFont);
		styles.put("data4", style);

		style = wb.createCellStyle();
		style.cloneStyleFrom(styles.get("data"));
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setFont(redFont);
		styles.put("data5", style);

		style = wb.createCellStyle();
		style.cloneStyleFrom(styles.get("data"));
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setFont(redFont);
		styles.put("data6", style);

		style = wb.createCellStyle();
		style.cloneStyleFrom(styles.get("data"));
		// style.setWrapText(true);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		Font headerFont = wb.createFont();
		headerFont.setFontName("Arial");
		headerFont.setFontHeightInPoints((short) 10);
		headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		headerFont.setColor(IndexedColors.WHITE.getIndex());
		style.setFont(headerFont);
		styles.put("header", style);

		return styles;
	}

	/**
	 * 添加一行  单工作表
	 * 
	 * @return 行对象
	 */
	public Row addRow() {
		return sheet.createRow(rownum++);
	}
	
	/**
	 * 添加一行  单工作表
	 * 
	 * @return 行对象
	 */
	public Row addRowBysheetNum(int num) {
		int rownum = this.getRowBySheet(num)+1;
		return this.sheets.get(num).createRow(rownum);
	}
	/**
	 * 添加一个单元格
	 * 
	 * @param row
	 *            添加的行
	 * @param column
	 *            添加列号
	 * @param val
	 *            添加值
	 * @return 单元格对象
	 */
	public Cell addCell(Row row, int column, Object val) {
		return this.addCell(row, column, val, 0, Class.class);
	}

	/**
	 * 添加一个单元格
	 * 
	 * @param row
	 *            添加的行
	 * @param column
	 *            添加列号
	 * @param val
	 *            添加值
	 * @param align
	 *            对齐方式（1：靠左；2：居中；3：靠右）
	 * @return 单元格对象
	 */
	public Cell addCell(Row row, int column, Object val, int align, Class<?> fieldType) {
		Cell cell = row.createCell(column);
		CellStyle style = styles.get("data" + (align >= 1 && align <= 3 ? align : ""));
		try {
			if (val == null) {
				cell.setCellValue("");
			} else if (val instanceof String) {
				String value = (String) val;
				if (value.contains("_")) {
					String[] teStrings = value.split("_");
					if ("0".equals(teStrings[0])) {
						align = align + 3;
						style = styles.get("data" + (align >= 4 && align <= 6 ? align : ""));
					}
					cell.setCellValue(teStrings[1]);
				} else {
					cell.setCellValue(value);
				}
			} else if (val instanceof Integer) {
				cell.setCellValue((Integer) val);
			} else if (val instanceof Long) {
				cell.setCellValue((Long) val);
			} else if (val instanceof Double) {
				cell.setCellValue((Double) val);
			} else if (val instanceof Float) {
				cell.setCellValue((Float) val);
			} else if (val instanceof Date) {
				DataFormat format = wb.createDataFormat();
				style.setDataFormat(format.getFormat("yyyy-MM-dd"));
				cell.setCellValue((Date) val);
			} else {
				if (fieldType != Class.class) {
					cell.setCellValue((String) fieldType.getMethod("setValue", Object.class).invoke(null, val));
				} else {
					cell.setCellValue((String) Class
							.forName(this.getClass().getName().replaceAll(this.getClass().getSimpleName(),
									"fieldtype." + val.getClass().getSimpleName() + "Type"))
							.getMethod("setValue", Object.class).invoke(null, val));
				}
			}
		} catch (Exception ex) {
			log.info("Set cell value [" + row.getRowNum() + "," + column + "] error: " + ex.toString());
			cell.setCellValue(val.toString());
		}
		cell.setCellStyle(style);
		return cell;
	}

	
	public Cell addCell1(Row row, int column, Object val, int align, Class<?> fieldType) {
		Cell cell = row.createCell(column);
		CellStyle style = styles.get("data" + (align >= 1 && align <= 3 ? align : ""));
		try {
			if (val == null) {
				cell.setCellValue("");
			} else if (val instanceof String) {
				String value = (String) val;
				cell.setCellValue(value);
			} else if (val instanceof Integer) {
				cell.setCellValue((Integer) val);
			} else if (val instanceof Long) {
				cell.setCellValue((Long) val);
			} else if (val instanceof Double) {
				cell.setCellValue((Double) val);
			} else if (val instanceof Float) {
				cell.setCellValue((Float) val);
			} else if (val instanceof Date) {
				DataFormat format = wb.createDataFormat();
				style.setDataFormat(format.getFormat("yyyy-MM-dd"));
				cell.setCellValue((Date) val);
			} else {
				if (fieldType != Class.class) {
					cell.setCellValue((String) fieldType.getMethod("setValue", Object.class).invoke(null, val));
				} else {
					cell.setCellValue((String) Class
							.forName(this.getClass().getName().replaceAll(this.getClass().getSimpleName(),
									"fieldtype." + val.getClass().getSimpleName() + "Type"))
							.getMethod("setValue", Object.class).invoke(null, val));
				}
			}
		} catch (Exception ex) {
			log.info("Set cell value [" + row.getRowNum() + "," + column + "] error: " + ex.toString());
			cell.setCellValue(val.toString());
		}
		cell.setCellStyle(style);
		return cell;
	}
	/**
	 * 添加数据（通过annotation.ExportField添加数据）
	 * 
	 * @return list 数据列表  单工作表
	 */
	public <E> ExportCompareExcel setDataList(List<E> list) {
		if(list != null){
			for (E e : list) {
				int colunm = 0;
				Row row = this.addRow();
				StringBuilder sb = new StringBuilder();
				for (Object[] os : annotationList) {
					ExcelField ef = (ExcelField) os[0];
					Object val = null;
					// Get entity value
					try {
						if (StringUtils.isNotBlank(ef.value())) {
							val = Reflections.invokeGetter(e, ef.value());
						} else {
							if (os[1] instanceof Field) {
								val = Reflections.invokeGetter(e, ((Field) os[1]).getName());
							} else if (os[1] instanceof Method) {
								val = Reflections.invokeMethod(e, ((Method) os[1]).getName(), new Class[] {},
										new Object[] {});
							}
						}
					} catch (Exception ex) {
						// Failure to ignore
						log.info(ex.toString());
						val = "";
					}
					System.out.println("--------------------------------"+val);
					this.addCell1(row, colunm++, val, ef.align(), ef.fieldType());
					sb.append(val + ", ");
				}
				log.debug("Write success: [" + row.getRowNum() + "] " + sb.toString());
			}
		}
		return this;
	}

	/**
	 * 添加数据（通过columns添加数据）
	 * 
	 * @return list 数据列表  单工作表
	 */
	public ExportCompareExcel setDataListForDynamicColumn(List<Map<Integer, Object>> data) {
		for (Map<Integer, Object> item : data) {
			int colunm = 0;
			Row row = this.addRow();
			StringBuilder sb = new StringBuilder();
			for (Integer val : columns) {
				this.addCell1(row, colunm++, item.get(val), 1, Class.class);
				sb.append(val + ", ");
			}
			
			log.debug("Write success: [" + row.getRowNum() + "] " + sb.toString());
		}
		return this;
	}
	
	/**
	 * 添加指定 sheet的数据
	 * @param num 
	 * @return list 数据列表  单工作表
	 * 
	 */
	public <E> ExportCompareExcel setDataList(List<E> list,int num) {
		for (E e : list) {
			int colunm = 0;
			Row row = this.addRowBysheetNum(num);
			StringBuilder sb = new StringBuilder();
			for (Object[] os : annotationList) {
				ExcelField ef = (ExcelField) os[0];
				Object val = null;
				// Get entity value
				try {
					if (StringUtils.isNotBlank(ef.value())) {
						val = Reflections.invokeGetter(e, ef.value());
					} else {
						if (os[1] instanceof Field) {
							val = Reflections.invokeGetter(e, ((Field) os[1]).getName());
						} else if (os[1] instanceof Method) {
							val = Reflections.invokeMethod(e, ((Method) os[1]).getName(), new Class[] {},
									new Object[] {});
						}
					}
				} catch (Exception ex) {
					// Failure to ignore
					log.info(ex.toString());
					val = "";
				}
				this.addCell(row, colunm++, val, ef.align(), ef.fieldType());
				sb.append(val + ", ");
			}
			log.debug("Write success: [" + row.getRowNum() + "] " + sb.toString());
		}
		return this;
	}
	
	
	
	
	
	/**
	 * 输出数据流
	 * 
	 * @param os
	 *            输出数据流
	 */
	public ExportCompareExcel write(OutputStream os) throws IOException {
		wb.write(os);
		return this;
	}

	/**
	 * 输出到客户端
	 * 
	 * @param fileName
	 *            输出文件名
	 */
	public ExportCompareExcel write(HttpServletResponse response, String fileName) throws IOException {
		response.reset();
		response.setContentType("application/octet-stream; charset=utf-8");
		response.setHeader("Content-Disposition", "attachment; filename=" + Encodes.urlEncode(fileName));
		write(response.getOutputStream());
		return this;
	}

	/**
	 * 输出到文件
	 * 
	 * @param fileName
	 *            输出文件名
	 */
	public ExportCompareExcel writeFile(String name) throws FileNotFoundException, IOException {
		FileOutputStream os = new FileOutputStream(name);
		this.write(os);
		return this;
	}

	/**
	 * 清理临时文件
	 */
	public ExportCompareExcel dispose() {
		wb.dispose();
		return this;
	}
	/***
	 * 
	 * @param colNum
	 * @param mergedColNum 需要 合并的列
	 */
	public void merged(String colNum,String ...mergedColNum){
		List<String> mergedColNumList = new ArrayList<>();
		for(String str : mergedColNum){
			mergedColNumList.add(str);
		}
		Sheet sheet = this.sheet;
		List<Row> rows = new ArrayList<>();
		
		int rowNum = sheet.getLastRowNum();
		for(int i=1;i<rowNum;i++){
			Row row = sheet.getRow(i);
			rows.add(row);
		}
		//按标准列 分组
		for(int i=0;i<rows.size();i++){
			//if(){}
		}
		
		
		
		//sheet.addMergedRegion(new CellRangeAddress(int firstRow, int lastRow, int firstCol, int lastCol);
		
	}

	public Sheet getSheet() {
		return sheet;
	}
	
	public SXSSFWorkbook getWorkbook(){
		return wb;
	}
}
