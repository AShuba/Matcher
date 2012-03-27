package com.ti.shuba.matcher;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.xml.sax.SAXException;

/**
 *
 * @author x0158321
 */
public class Main {
    
    public static String versionString = "v.1.1";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String domainName  = "";
        String XLSFileName = "";
        String XMLFileName = "";
        String interceptFileName = "";
        String complementFileName = "";
        boolean debug = false;

        if (args.length < 3) {
            infoMessage("Very few arguments.");
            showUsage();
            System.exit(-1);
        }
        
        for (int i = 0; i < args.length; i++) {
            if(args[i].trim().startsWith("--domain="))
                domainName = args[i].substring(args[i].indexOf("=") + 1) + "_Results";
            else if(args[i].trim().startsWith("--xls="))
                XLSFileName = args[i].substring(args[i].indexOf("=") + 1);
            else if(args[i].trim().startsWith("--xml="))
                XMLFileName = args[i].substring(args[i].indexOf("=") + 1);
            else if(args[i].trim().startsWith("--intercept_output="))
                interceptFileName = args[i].substring(args[i].indexOf("=") + 1);
            else if(args[i].trim().startsWith("--complement_output="))
                complementFileName = args[i].substring(args[i].indexOf("=") + 1);
            else if(args[i].trim().startsWith("--debug"))
                debug = true;
        }
        if(domainName.isEmpty()) {
            infoMessage("Domain name missed");
            showUsage();
            System.exit(-1);
        } else if(XLSFileName.isEmpty()) {
            infoMessage("XLS file name missed");
            showUsage();
            System.exit(-1);
        } else if(XMLFileName.isEmpty()) {
            infoMessage("XML file name missed");
            showUsage();
            System.exit(-1);
        }
        
        if(interceptFileName.isEmpty()) {
            interceptFileName = domainName + ".intercept";
            infoMessage("Interception output file name is missed. Using '" + interceptFileName + "' file name\n");
        }
        
        if(complementFileName.isEmpty()) {
            complementFileName = domainName + ".complement";
            infoMessage("Complemention output file name is missed. Using '" + interceptFileName + "' file name\n");
        }

        FileInputStream is;
        
        try {
            List<String> testIDs = new ArrayList<String>();
            
            is = new FileInputStream(XLSFileName);
            HSSFWorkbook wb = new HSSFWorkbook(is);
            HSSFSheet worksheet = null;
            for (int sheetNum = 0; sheetNum < wb.getNumberOfSheets(); sheetNum++) {
                if(wb.getSheetAt(sheetNum).getSheetName().equals(domainName)) {
                    worksheet = wb.getSheetAt(sheetNum);
                    break;
                }
            }
            
            if (null == worksheet) {
                System.out.println("Sheet '" + domainName + "' not found inside " + XLSFileName + " file.");
                System.exit(-1);
            }
            
            if(debug)
                System.out.println("Sheet name found: '" + worksheet.getSheetName() + "'");
            int rows = worksheet.getPhysicalNumberOfRows();
            if(debug)
                System.out.println("Sheet " + "has " + rows + " row(s).");

            // Search all cells from column 0 for string which starts from 'L_'
            for (int rowNum = 0; rowNum < rows; rowNum++) {
                HSSFRow row = worksheet.getRow(rowNum);
                HSSFCell cell = row.getCell(0);
                if (null == cell)
                    // No cells anymore
                    break;
                if(cell.getCellType() != HSSFCell.CELL_TYPE_STRING)
                    continue;
                String cellValue = cell.getStringCellValue();
                if(!cellValue.startsWith("L_")) {
                    continue;
                }
                testIDs.add(cellValue);
            }
            if (debug) {
                System.out.println("List of IDs from XLS file");
                for(String s : testIDs)
                    System.out.println(s);
            }
            
            TITANParser titanParser = new TITANParser();
            // This hash map represents all tests from the XML file
            // Key - testID string, value - Test object
            HashMap<String, Test> titanTestIDs = titanParser.parse(new File(XMLFileName));
            if (debug) {
                System.out.println("List of tests from XML file");
                for (Test test : titanTestIDs.values())
                    System.out.println(test.toString());
                
            }
            // Intersection and complemention of the tests IDs from XLS and XML
            // files using corresponding domain
            ArrayList<String> testsIntersect  = new ArrayList<String>();
            ArrayList<String> testsComplement = new ArrayList<String>();
            
            // Filling Interception and complemention result arrays
            for(String s : testIDs) {
                if (titanTestIDs.containsKey(s))
                    testsIntersect.add(s);
                else
                    testsComplement.add(s);
            }
            // Output files
            OutputStream interStream;
            OutputStream complStream;
            if(interceptFileName.equals("-"))
                interStream = System.out;
            else
                interStream = new FileOutputStream(interceptFileName, false);
            
            if(complementFileName.equals("-"))
                complStream = System.out;
            else
                complStream = new FileOutputStream(complementFileName, false);
            
            // Output Intersection result
            if (testsIntersect.isEmpty() && !interceptFileName.equals("-"))
                // Erase previous output file if result is empty
                interStream.write("".getBytes());
            else
                for (String s : testsIntersect) {
                    interStream.write(s.getBytes());
                    interStream.write("\n".getBytes());
                }
            // Output Complemetion result
            if (testsComplement.isEmpty() && !complementFileName.equals("-"))
                // Erase output file if result is empty
                complStream.write("".getBytes());
            else
                for (String s : testsComplement) {
                    complStream.write(s.getBytes());
                    complStream.write("\n".getBytes());
                }
            
            // Safety closing streams
            interStream.flush();
            interStream.close();
            complStream.flush();
            complStream.close();
                
        } catch (FileNotFoundException ex) {
            errorMessage("File not found", ex);
            System.exit(-1);
        } catch (IOException ex) {
            errorMessage("IO exception ", ex);
            System.exit(-1);
        } catch (ParserConfigurationException ex) {
            errorMessage("ParserConfigurationException ", ex);
            System.exit(-1);
        } catch (SAXException ex) {
            errorMessage("SAXException ", ex);
            System.exit(-1);
        } catch (Exception ex) {
            errorMessage("UNKNOWN exception ", ex);
            ex.printStackTrace();
            System.exit(-1);
        }
        
    }
    
    private static void infoMessage(String str) {
            System.out.println(str);
    }
    
    private static void errorMessage(String str, Exception ex) {
            System.out.println(str);
            System.out.println(ex.toString());
    }
    
    private static void showUsage() {
        infoMessage("\nMatcher " + versionString + "\n");
        infoMessage("Usage: java -jar Matcher.jar --domain=domain_to_compare --xls=UTR.xls --xml=TitanDescr.xml [--intercept_output=[-|file.name]] [--complement_output=[-|file.name]]\n");
    }
}
