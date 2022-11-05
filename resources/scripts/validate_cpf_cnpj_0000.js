function validate_cpf_cnpj_0000(strCPF, objMessage) {
    if (register.getFieldValue('CNPJ') && register.getFieldValue('CPF')) {
        objMessage.message = 'Informe apenas CNPJ ou CPF para o Registro ' + register.getName();
        return false;
    }

    if (!register.getFieldValue('CNPJ') && !register.getFieldValue('CPF')) {
        objMessage.message = 'Obrigatório informar CNPJ ou CPF para o Registro ' + register.getName();
        return false;
    }

    return true;
}