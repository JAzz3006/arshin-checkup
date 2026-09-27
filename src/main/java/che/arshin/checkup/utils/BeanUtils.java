package che.arshin.checkup.utils;
import che.arshin.checkup.web.dto.VerificationStatus;
import lombok.experimental.UtilityClass;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class BeanUtils {
    public void copyNonNullProperties(Object source, Object destination) {
        Field[] sourceFields = source.getClass().getDeclaredFields();

        try {
            for (Field sourceField : sourceFields) {
                sourceField.setAccessible(true);
                Object value = sourceField.get(source);
                if (value == null) {
                    continue;
                }
                if (Collection.class.isAssignableFrom(sourceField.getType())) {
                    continue;
                }
                Field destinationField;
                try {
                    destinationField = destination.getClass()
                            .getDeclaredField(sourceField.getName());
                } catch (NoSuchFieldException e) {
                    continue;
                }
                if (!destinationField.getType().isAssignableFrom(sourceField.getType())) {
                    continue;
                }
                destinationField.setAccessible(true);
                destinationField.set(destination, value);
            }
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(
                    "Failed to copy non-null properties", e
            );
        }
    }

    public Map<String, Integer> getSummaryTemplate(){
        Map<String, Integer> summary = new HashMap<>();
        for (VerificationStatus s : VerificationStatus.values()){
            summary.put(s.name(),0);
        }
        return summary;
    }
}