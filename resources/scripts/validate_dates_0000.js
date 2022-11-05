function validate_dates_0000(date, objMessage) {
  var dt_ini = register.getFieldValue('DT_INI');
  var dt_fin = register.getFieldValue('DT_FIN');
  var m_dt_ini = dt_ini.getMonth() + 1;
  var m_dt_fin = dt_fin.getMonth() + 1;
  var y_dt_ini = dt_ini.getYear();
  var y_dt_fin = dt_fin.getYear();

  if (m_dt_ini != m_dt_fin || y_dt_ini != y_dt_fin) {
    objMessage.message = 'Data de início e fim devem ser no mesmo mês e ano ';
    return false;
  }

  return true;
}