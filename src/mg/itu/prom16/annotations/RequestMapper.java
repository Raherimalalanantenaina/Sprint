package mg.itu.prom16.annotations;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;


public class RequestMapper {


    public static <T> T mapRequestToObject(HttpServletRequest request, Class<T> clazz)throws Exception{
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();
    
            for (Field field : clazz.getDeclaredFields()) {
                RequestParam requestParam = field.getAnnotation(RequestParam.class);
                String paramName = (requestParam != null) ? requestParam.value() : field.getName();
                String paramValue = request.getParameter(paramName);
    
                if (requestParam == null) {
                    throw new Exception("ETU2777   la param n'exite pas'" + paramName + "' is missing");
                }
                if (paramValue != null) {
                    validation(field,paramValue);
                    field.setAccessible(true);
                    field.set(instance, convertToFieldType(field.getType(), paramValue));                   
                }
    
            }
    
            return instance;
        } catch (Exception e) {
            throw e;
        }
    }






    

    private static Object convertToFieldType(Class<?> fieldType, String value) throws IllegalArgumentException {
        try {
            if (fieldType == String.class) {
                return value;
            } else if (fieldType == int.class || fieldType == Integer.class) {
                return Integer.parseInt(value);
            } else if (fieldType == long.class || fieldType == Long.class) {
                return Long.parseLong(value);
            } else if (fieldType == boolean.class || fieldType == Boolean.class) {
                return Boolean.parseBoolean(value);
            } else if (fieldType == double.class || fieldType == Double.class) {
                return Double.parseDouble(value);
            } else if (fieldType == float.class || fieldType == Float.class) {
                return Float.parseFloat(value);
            } else {
                throw new IllegalArgumentException("Unsupported field type: " + fieldType);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Error converting value: " + value + " to type: " + fieldType, e);
        }
    }
    



    public static void validation(Field field, String paramValue) throws Exception {
        if (field.isAnnotationPresent(Required.class) && (paramValue == null || paramValue.isEmpty())) {
            throw new Exception("Le champ " + field.getName() + " ne doit pas être nul ou vide.");
        }
    
        if (field.isAnnotationPresent(TypeDouble.class)) {
            try {
                Double.parseDouble(paramValue);
            } catch (NumberFormatException e) {
                throw new Exception("Le champ " + field.getName() + " doit être un nombre décimal valide.");
            }
        }
    
        if (field.isAnnotationPresent(TypeInt.class)) {
            try {
                Integer.parseInt(paramValue);
            } catch (NumberFormatException e) {
                throw new Exception("Le champ " + field.getName() + " doit être un entier valide.");
            }
        }
    
        if (field.isAnnotationPresent(TypeString.class)) {
            // TypeString est toujours String, donc pas de conversion nécessaire
            if (paramValue == null) {
                throw new Exception("Le champ " + field.getName() + " doit être une chaîne non nulle.");
            }
        }
    }


    
}

