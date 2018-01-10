/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package Analyzer;

/**
*
* @author skannuku
*/
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Analyzer {
            public static void main(String[] args) throws IOException {
                long start = System.currentTimeMillis();
                //String path = "C:\\OracleATS\\OFT\\DataBank";
                String path = args[0];
                Pattern regex = Pattern.compile(args[1]);
                //Pattern regex = Pattern.compile("jSE+");
                List<Path> paths = Files.walk(Paths.get(path)).parallel().filter(p -> p.toString().endsWith(".java"))
                                        .collect(Collectors.toList());
                check(paths, regex, args[0], args[1]);

              //check(paths, regex,path,"jSE");
                System.out.println(System.currentTimeMillis() - start);
            }
            public static String createFile(String sFilePath)
            {
                  File f = null;
                  try {

                     f = new File(sFilePath);
                     f.createNewFile();
                     System.out.println("File created at "+ sFilePath);
                  } catch(Exception e) {
                     e.printStackTrace();
                  }
                  return sFilePath;
            }
            static void check(List<Path> px, Pattern regex, String location, String pattern) throws IOException {

                        List<String> alist = new ArrayList<String>();
                        Set<String> aset = new LinkedHashSet<>();
                        px.parallelStream().forEach(p -> {
                                    try {
                                                BufferedReader r = new BufferedReader((new FileReader(p.toFile().getAbsolutePath())));
                                                int lineNum = 0;
                                                String line = null;

                                                while ((line = r.readLine()) != null) {
                                                            Matcher m = regex.matcher(line);
                                                            lineNum++;
                                                            if (m.find()) {
                                                                        System.out.println(p.toFile().getAbsolutePath() + " ; at line " + lineNum);
                                                                        alist.add(p.toFile().getAbsolutePath() + " ; at line " + lineNum);
                                                                        aset.add(p.toFile().getAbsolutePath());
                                                            }
                                                }
                                                r.close();
                                    } catch (IOException e) {
                                                // TODO Auto-generated catch block
                                                e.printStackTrace();
                                    }

                        });


                        
                        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
//                        System.out.println(timeStamp);
                        String sExcel ="ImpactAnalysisReport_"+timeStamp+".xlsx";
//                        System.out.println(sExcel);
                        createFile(sExcel);
                        writeExcel(sExcel, aset, alist, pattern,location);
                        
                        writeHTML(alist,location,pattern);
                        writeHTML1(aset,location,pattern);
            }

            private static void writeHTML1(Set<String> aset,String location, String pattern) {
                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date date = new Date();
                        String theader = "<tr><th>File</th></tr>";
                        String style = "<style>table{font-family:arial,sans-serif;border-collapse:collapse;width:100%}td,th{border:1px solid #ddd;text-align:left;padding:8px}tr:nth-child(even){background-color:#ddd}</style>";

                        StringBuilder sb = new StringBuilder();
                        sb.append("<html><HEAD><Title>");
                        sb.append("Imapct Analysis Report - " + dateFormat.format(date));
                        sb.append("</Title>"+style+"</HEAD><BODY>");
                        sb.append("<table>");
                        sb.append(theader);
                        sb.append("<tr>");
                        sb.append("<td> Searched from : " + location + "</td><td> For Pattern : " + pattern + "</td>");
                        sb.append("</tr>");
                        aset.forEach(p -> {
                                    sb.append("<tr>");
                                    sb.append("<td>" + p + "</td>");
                                    sb.append("</tr>");
                        });
                        sb.append("</table>");
                        sb.append("</BODY></HEAD>");

                        try {
                                    PrintWriter writer = new PrintWriter("ImapctAnalysisReport-Unique-" + System.currentTimeMillis() + ".html",
                                                            "UTF-8");
                                    writer.append(sb.toString());
                                    writer.close();
                        } catch (FileNotFoundException | UnsupportedEncodingException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                        }
                        
                        
            }

            private static void writeHTML(List<String> alist, String location, String pattern) {

                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date date = new Date();
                        String theader = "<tr><th>File</th><th>Location</th></tr>";
                        String style = "<style>table{font-family:arial,sans-serif;border-collapse:collapse;width:100%}td,th{border:1px solid #ddd;text-align:left;padding:8px}tr:nth-child(even){background-color:#ddd}</style>";

                        StringBuilder sb = new StringBuilder();
                        sb.append("<html><HEAD><Title>");
                        sb.append("Imapct Analysis Report - " + dateFormat.format(date));
                        sb.append("</Title>"+style+"</HEAD><BODY>");
                        sb.append("<table>");
                        sb.append(theader);
                        sb.append("<tr>");
                        sb.append("<td> Searched from : " + location + "</td><td> For Pattern : " + pattern + "</td>");
                        sb.append("</tr>");
                        alist.forEach(p -> {
                                    sb.append("<tr>");
                                    sb.append("<td>" + p.split(";")[0] + "</td><td>" + p.split(";")[1] + "</td>");
                                    sb.append("</tr>");
                        });
                        sb.append("</table>");
                        sb.append("</BODY></HEAD>");

                        try {
                                    PrintWriter writer = new PrintWriter("ImapctAnalysisReport-Detailed-" + System.currentTimeMillis() + ".html",
                                                            "UTF-8");
                                    writer.append(sb.toString());
                                    writer.close();
                        } catch (FileNotFoundException | UnsupportedEncodingException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                        }

            }
    public static void makeRowBold(Workbook wb, Row row){
        CellStyle style = wb.createCellStyle();//Create style
        Font font = wb.createFont();//Create font
        font.setBold(true);//Make font bold
        style.setFont(font);//set it to bold
//                style.setWrapText(true);
        style.setFillBackgroundColor(IndexedColors.YELLOW.getIndex());
        for(int i = 0; i < row.getLastCellNum(); i++){//For each cell in the row 
            row.getCell(i).setCellStyle(style);//Set the sty;e
        }

    }
    public static void writeExcel(String sFilePath, Set<String> aSet, List<String> aList, String sPattern,String location) throws IOException
    {
        File file =    new File(sFilePath);
        FileOutputStream outputStream = new FileOutputStream(file);
        Workbook WB= new XSSFWorkbook();
        WB.createSheet("Unique");
        WB.createSheet("Detailed");
        Sheet sheet1 = WB.getSheet("Unique");
        Sheet sheet2 = WB.getSheet("Detailed");
        Row prow1 = sheet1.createRow(0);
        Row prow2 = sheet2.createRow(0);
        
        
        prow1.createCell(0).setCellValue("Search Location: '"+ location +"'");
        prow2.createCell(0).setCellValue("Search Location: '"+ location +"'");
        prow1.createCell(1).setCellValue("For String Pattern: '"+ sPattern +"'");
        prow2.createCell(1).setCellValue("For String Pattern: '"+ sPattern +"'");
        
        Row row1 = sheet1.createRow(1);
        Row row2 = sheet2.createRow(1);
        
        
        row1.createCell(0).setCellValue("File Location");
        row1.createCell(1).setCellValue("File Name");
        row1.createCell(2).setCellValue("Complete Path");
        
        
        row2.createCell(0).setCellValue("File Location");
        row2.createCell(1).setCellValue("File Name");
        row2.createCell(2).setCellValue("Complete Path");
        row2.createCell(3).setCellValue("Line Numer");
        
        int iCount1 = 2;
        System.out.println(aSet);
        for (String p: aSet)
        {
//            System.out.println(p);
            
            
            int lastIndex1 = p.lastIndexOf("\\");
            Row row = sheet1.createRow(iCount1);
            
            row.createCell(0).setCellValue((String) p.subSequence(0, lastIndex1));
            row.createCell(1).setCellValue((String) p.subSequence(lastIndex1+1, p.length()));
            row.createCell(2).setCellValue(p);
            iCount1++;
//            System.out.println(iCount1);
        }
        int iCount2 = 2;
        for (String p: aList)
        {
//            System.out.println(p);
            int lastIndex2 = p.lastIndexOf("\\");
            int lastIndex3 = p.lastIndexOf(";");
            Row row = sheet2.createRow(iCount2);
            row.createCell(0).setCellValue((String) p.subSequence(0, lastIndex2));
            row.createCell(1).setCellValue((String) p.subSequence(lastIndex2+1, lastIndex3));
            row.createCell(2).setCellValue((String) p.subSequence(0, lastIndex3));
            row.createCell(3).setCellValue((String) p.subSequence(lastIndex3+1, p.length()));
            iCount2++;
//            System.out.println(iCount2);
        }
        makeRowBold(WB, prow1);
        makeRowBold(WB, prow2);
        
        makeRowBold(WB, row1);
        makeRowBold(WB, row2);
        
        sheet1.autoSizeColumn(0);
        sheet1.autoSizeColumn(1); 
        sheet1.autoSizeColumn(2); 
        
        sheet2.autoSizeColumn(0);
        sheet2.autoSizeColumn(1); 
        sheet2.autoSizeColumn(2); 
        sheet2.autoSizeColumn(3); 
       
        
        WB.write(outputStream);
        outputStream.flush();
        outputStream.close();
    }

}
