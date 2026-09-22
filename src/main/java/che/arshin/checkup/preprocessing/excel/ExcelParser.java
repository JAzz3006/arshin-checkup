package che.arshin.checkup.preprocessing.excel;
import che.arshin.checkup.exception.ExcelParseException;
import che.arshin.checkup.web.dto.MIRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
@Component
public class ExcelParser {

    @Value("${app.xls-location}")
    String pathAsSting;

    public List<MIRequest> getRequests() throws IOException{

        Resource resource = new ClassPathResource(pathAsSting);

        try(InputStream inputStream = resource.getInputStream()){
            return parse(inputStream);
        } catch (IOException e) {
            log.warn("Cannot parse excel file '{}'", pathAsSting);
            throw new ExcelParseException("Can't parse excel file " + pathAsSting, e);
        }
    }

    public List<MIRequest> parse(InputStream inputStream) throws IOException{

        List<MIRequest> requests = new ArrayList<>();
        int serialNumberColumn = -1;
        int modelColumn = -1;
        try(Workbook workbook = new XSSFWorkbook(inputStream)){
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(2);
            for (Cell cell : headerRow){
                if (cell.getStringCellValue().trim().toLowerCase(Locale.ROOT).equals("Серийный номер".toLowerCase(Locale.ROOT))){
                    serialNumberColumn = cell.getColumnIndex();
                }
                if (cell.getStringCellValue().trim().equals("Модель")){
                    modelColumn = cell.getColumnIndex();
                }
                if (serialNumberColumn != -1 && modelColumn != -1) break;
            }

            log.info("serialNumberColumn = {} and modelColumn = {}", serialNumberColumn, modelColumn);

            if (serialNumberColumn == -1 || modelColumn == -1) {
                throw new IllegalArgumentException(
                        "Required columns 'Серийный номер' and 'Модель' not found"
                );
            }

            for (int i =3; i <= sheet.getLastRowNum(); i++){
                Row row = sheet.getRow(i);
                String serialNumber = row
                        .getCell(serialNumberColumn)
                        .getStringCellValue();
                String model = row
                        .getCell(modelColumn)
                        .getStringCellValue();
                MIRequest request = new MIRequest();
                request.setModel(model);
                request.setSerialNumber(serialNumber);
                requests.add(request);
            }
        }
        return requests;
    }
}