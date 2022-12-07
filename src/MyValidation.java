import sped.core.FieldNotFoundException;
import sped.core.Register;
import sped.core.ValidationHelper;
import sped.core.ValidationMessage;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MyValidation implements ValidationHelper {
    private boolean validate_cpf(String cpf) {
        return false;
    }

    @Override
    public boolean validate(ValidationMessage validationMessage, String methodName, String value, Register register) {
        try {
            Method method = this.getClass().getDeclaredMethod(methodName, ValidationMessage.class, String.class, Register.class);
            return (boolean) method.invoke(this, validationMessage, value, register);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean validate_cpf(ValidationMessage validationMessage, String value, Register register) {
        validationMessage.setMessage("Validação de CPF não implementada");
        return false;
    }

    public boolean validate_dates_0000(ValidationMessage validationMessage, String value, Register register) {
        Calendar cal = Calendar.getInstance();

        try {
            Date dt_ini = register.<Date>getFieldValue("DT_INI");
            Date dt_fin = register.<Date>getFieldValue("DT_FIN");

            cal.setTime(dt_ini);
            int m_dt_ini = cal.get(Calendar.MONTH);
            int y_dt_ini = cal.get(Calendar.YEAR);

            cal.setTime(dt_fin);
            int m_dt_fin = cal.get(Calendar.MONTH);
            int y_dt_fin = cal.get(Calendar.YEAR);

            if (m_dt_ini != m_dt_fin || y_dt_ini != y_dt_fin) {
                validationMessage.setMessage("Data de início e fim devem ser no mesmo mês e ano");
                return false;
            }
        } catch (FieldNotFoundException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    public boolean validate_codigo_municipio(ValidationMessage validationMessage, String value, Register register) {
        //checa 7 digitos
        if (value.length() != 7) {
            validationMessage.setMessage("Código do município deve ter 7 dígitos");
            return false;
        }

        //checa se só tem numeros
        if (!value.matches("[0-9]*")) {
            validationMessage.setMessage("Código do município inválido");
            return false;
        }

        //checa se é um estado válido
        if (!value.substring(0,2).matches("12|27|13|16|29|23|53|32|52|21|31|50|51|15|25|26|22|41|33|24|11|14|43|42|28|35|17")) {
            validationMessage.setMessage("Código do município inválido. Código de Estado inexistente: %s".formatted(value.substring(0,2)));
            return false;
        }

        //checa digito verificador
        int sum = 0;
        Integer[] doublePositions = new Integer[]{1, 3, 5};
        List<Integer> list = Arrays.asList(doublePositions);
        for (int i=0; i<value.length(); i++) {
            int dig = Integer.parseInt(String.valueOf(value.charAt(i)));

            if (i==6) {
                int dv = (10 - (sum % 10)) % 10;
                if (dig != dv) {
                    validationMessage.setMessage("Dígito verificador do Código do Município inválido");
                    return false;
                }
            }

            if (list.contains(i)) {
                int doubleDig = dig * 2;
                sum += doubleDig > 9 ? doubleDig-9 : doubleDig; //-9 --> -10 +1  ex. 9*9 = 18 --> 1+8 = 9
            } else {
                sum += dig;
            }
        }

        return true;
    }
}
