import org.xml.sax.InputSource;
import sped.lib.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Objects;

public class Main {
    public static void main(String[] args) {
        try {
            //configurações utilizadas pela classe SPEDGenerator

            Definitions definitions = new Definitions(
                    "definitions.xml",
                    new MyValidation()
            );

            definitions.setFileLoader(fileName -> {
                try {
                    return Objects.requireNonNull(Main.class.getClassLoader().getResource(fileName)).openStream();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            Factory factory = new Factory(definitions);
            SPEDGenerator spedGenerator = factory.createSPEDGenerator();

            Register r = spedGenerator.getRegister0000().getRegister();  //0000

            r.setFieldValue("COD_VER", 14);
            r.setFieldValue("COD_FIN", 1);
            r.setFieldValue("DT_INI", new Date());
            r.setFieldValue("DT_FIN", new Date(555525));
            r.setFieldValue("NOME", "  FABIANO ARNDT ");
            r.setFieldValue("CPF", "123456789-10");
            r.setFieldValue("CNPJ", "00.360.305/0001-04");
            r.setFieldValue("UF", "");
            r.setFieldValue("COD_MUN", 1501452);
            r.setFieldValue("IND_PERFIL", "A");
            r.setFieldValue("IE", "ISENTO");
            r.setFieldValue("IND_ATIV", 0);

            Block b0 = spedGenerator.addBlock("0", "0001", "0990");

            r = b0.addRegister("0002");
            r.setFieldValue("CLAS_ESTAB_IND", "05");

            r = b0.addRegister("0005");
            r.setFieldValue("FANTASIA", "   TESTE FANTASIA");
            r.setFieldValue("CEP", "teste cep");
            r.setFieldValue("END", "  teste END");
            r.setFieldValue("NUM", "  teste NUM");
            r.setFieldValue("COMPL", "teste COMPL");
            r.setFieldValue("BAIRRO", "  teste BAIRRO");

            Register r0190 = b0.addRegister("0190");
            r0190.setFieldValue("UNID", "M");
            r0190.setFieldValue("DESCR", "METRO");

            r0190 = b0.addRegister("0190");
            r0190.setFieldValue("UNID", "M2");
            r0190.setFieldValue("DESCR", "METRO QUADRADO");

            r0190 = b0.addRegister("0190");
            r0190.setFieldValue("UNID", "KG123456789");  //formatted: KG1234
            r0190.setFieldValue("DESCR", "QUILO");

            Register r0200 = b0.addRegister("0200");
            r0200.setFieldValue("COD_ITEM", "1000");
            r0200.setFieldValue("DESCR_ITEM", "ABACATE");
            r0200.setFieldValue("UNID_INV", r0190);

            System.out.println("0200 ID: " + r0200.getID()); //COD_ITEM

            r = b0.addRegister("0205");
            //r.setFieldValue("teste", "ABACATE");            //throws FieldNotFoundException - nao existe campo teste NO REGISTRO 0200
            r.setFieldValue("DESCR_ANT_ITEM", "ABACATE ANTIGO");


            Block bc = spedGenerator.addBlock("C", "C001", "C990");
            r = bc.addRegister("C100");

            for (int i = 0; i < 4; i++) {
                Register c590 = bc.addRegister("C590");
                Register c591 = c590.addRegister("C591");
                c591.setFieldValue("VL_FCP_OP", 2555.9933 + i);
                c591.setFieldValue("VL_FCP_ST", 2333.09 + 2*i);
            }

            Block bd = spedGenerator.addBlock("D", "D001", "D990");
            Block be = spedGenerator.addBlock("E", "E001", "E990");

            //totalizacao: gerar os registros de contagem (bloco 9)
            spedGenerator.generateBlock9();
            spedGenerator.totalize();

            //altere aqui para 0, 1 ou 2 para testar as diferentes formas de obter a saida dos dados
            int writerOptions = 0;

            //Exemplo com StringBuilder
            if (writerOptions == 0) {
                StringBuilderWriter writer = new StringBuilderWriter(new StringBuilder());
                spedGenerator.write(writer);
                System.out.println(writer.stringBuilder().toString());
            }

            //exemplo com FileWriter
            if (writerOptions == 1) {
                //exemplo com FileWriter:
                FileWriter fileWriter = new FileWriter("c:/executaveis/teste2.txt");
                FileWriterWriter writer = new FileWriterWriter(fileWriter);
                fileWriter.close();
            }

            if (writerOptions == 2) {
                //exemplo implementando Writer em uma lambda
                spedGenerator.write((string, register) -> System.out.println(string));
            }


            //validação dos dados (trabalho em andamento)
            spedGenerator.validate(new ValidationListener() {
                @Override
                public void onSuccessMessage(ValidationEvent event) {
                    System.out.println(event.getMessage());
                }

                @Override
                public void onWarningMessage(ValidationEvent event) {
                    System.out.println(event.getMessage());
                }

                @Override
                public void onErrorMessage(ValidationEvent event) {
                    System.out.println(event.getMessage());
                }
            });

            /*
            0200 ID: 1000
            |0000|010|0|23102022|23102022|FABIANO ARNDT||12345678910|PR||1234567|||A|0|
            |0001|0|
            |0005|TESTE FANTASIA||teste END|teste NUM|teste COMPL|teste BAIRRO||||
            |0190|M|METRO|
            |0190|M2|METRO QUADRADO|
            |0190|KG1234|QUILO|
            |0200|1000|ABACATE|||KG1234||||||||
            |0205|ABACATE ANTIGO||||
            |0990|9|
            |C001|0|
            |C100|||||||||||||||||||||||||||||
            |C590|||||||||||
            |C591|2555,99|2333,09|
            |C590|||||||||||
            |C591|2556,99|2335,09|
            |C990|7|
            |D001|1|
            |D990|2|
            |E001|1|
            |E990|2|
            |9001|0|
            |9900|0000|1|
            |9900|9999|1|
            |9900|0001|1|
            |9900|0990|1|
            |9900|0005|1|
            |9900|0190|3|
            |9900|0200|1|
            |9900|0205|1|
            |9900|C001|1|
            |9900|C990|1|
            |9900|C100|1|
            |9900|C590|2|
            |9900|C591|2|
            |9900|D001|1|
            |9900|D990|1|
            |9900|E001|1|
            |9900|E990|1|
            |9900|9001|1|
            |9900|9990|1|
            |9900|9900|20|
            |9990|23|
            |9999|43|

            0000.DT_INI: "05112022": "Data de início e fim devem ser no mesmo mês e ano"
            0000.DT_FIN: "31121969": "Data de início e fim devem ser no mesmo mês e ano"
            0000.CNPJ: "00360305000104": "Informe apenas CNPJ ou CPF para o Registro 0000".
            0000.CPF: "12345678910": "Digito verificador invalido".
            0000.CPF: "12345678910": "Informe apenas CNPJ ou CPF para o Registro 0000".
            0000.UF: "Campo ogrigatório não informado".
            0000.COD_MUN: "1501452": "Dígito verificador do Código do Município inválido"
            0005.CEP: "Campo ogrigatório não informado".


            *
            * */
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}