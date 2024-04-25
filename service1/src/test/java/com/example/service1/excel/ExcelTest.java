package com.example.service1.excel;

//import com.example.service1.v4.itf.Interface;
//import com.example.service1.v4.itf.Reader;
import com.example.service1.reader.ExcelInterfaceReader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelTest {


    ExcelInterfaceReader reader = new ExcelInterfaceReader();

    @Test
    void test() {
        InputStream io;
//        Interface itf = new Interface();
        // 인터페이스설계서_V3OIS00019_대출상품안내조회.xlsx

        try {
            System.out.println("Working Directory = " + System.getProperty("user.dir"));

            FileInputStream file = new FileInputStream(new File("/Users/lee/Study/kyobo/SCG/인터페이스설계서_V3OIS00019_대출상품안내조회.xlsx"));

            Map<String, Object> res = reader.convert(file);
//            System.out.println((res.get("inInfo")));
//            System.out.println((res.get("outInfo")))
            System.out.println(res.toString());


        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

}
