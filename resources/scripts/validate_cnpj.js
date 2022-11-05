function validate_cnpj(cnpj, objMessage) {
    cnpj = cnpj.replace(/[^\d]+/g,'');

    if (cnpj.length != 14) {
        objMessage.message = 'Tamanho inválido';
        return false;
    }

    size_p1 = cnpj.length - 2;
    cnpj_p1 = cnpj.substring(0, size_p1);
    cnpj_dv = cnpj.substring(size_p1);

    sum = 0;
    pos = size_p1 - 7;
    for (i = size_p1; i >= 1; i--) {
      sum += cnpj_p1.charAt(size_p1 - i) * pos--;
      if (pos < 2) pos = 9;
    }

    calc_dv = sum % 11 < 2 ? 0 : 11 - sum % 11;
    if (calc_dv != cnpj_dv.charAt(0)) {
        objMessage.message = 'Dígito verificador inválido';
        return false;
    }

    size_p1 = size_p1 + 1;
    cnpj_p1 = cnpj.substring(0, size_p1);
    sum = 0;
    pos = size_p1 - 7;
    for (i=size_p1; i>=1; i--) {
      sum += cnpj_p1.charAt(size_p1 - i) * pos--;
      if (pos < 2) pos = 9;
    }

    calc_dv = sum % 11 < 2 ? 0 : 11 - sum % 11;
    if (calc_dv != cnpj_dv.charAt(1)) {
        objMessage.message = 'Dígito verificador inválido';
        return false;
    }

    return true;
}
