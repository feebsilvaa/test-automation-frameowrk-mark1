package br.com.fernando.automationframework.support;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

public class WordTemplate {

    public static void main(String[] args) throws URISyntaxException, IOException {
        String file = "src/test/resources/modelo.docx";
        FileInputStream in = new FileInputStream(file);
        WordTemplate w = new WordTemplate();
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("#valor_parcela#", "12.000,00");
        properties.put("#valor_total#", "200.000,00");
        XWPFDocument document = w.extractTemplate(in, properties);
        FileOutputStream os = new FileOutputStream("src/test/resources/teste.docx");
        document.write(os);
    }

    /**
     * Extrai o template do word
     * @param stream inputstream do arquivo word
     * @param properties chave/valor a ser usado.
     * @return XWPFDocument
     * @throws IOException
     */
    public XWPFDocument extractTemplate(InputStream stream, Map<String, String> properties) throws IOException {
        XWPFDocument document = new XWPFDocument(stream);
        replaceParagraphs(document.getParagraphs(), properties);
        replaceTables(document.getTablesIterator(), properties);
        return document;
    }

    /**
     * Altera os valores dos paragrafos.
     * @param paragraphs paragrafos
     * @param properties propriedades
     */
    public void replaceParagraphs(List<XWPFParagraph> paragraphs, Map<String, String> properties) {
        for (XWPFParagraph paragraph : paragraphs) {
            List<XWPFRun> runs = paragraph.getRuns();

            for (XWPFRun run : runs) {
                String textRun = run.getText(run.getTextPosition());
                for (Entry<String, String> entry : properties.entrySet()) {
                    if (textRun != null && textRun.contains(entry.getKey())) {
                        String newText = textRun.replace(entry.getKey(), String.valueOf(properties.get(entry.getKey())));
                        run.setText(newText, 0);
                        break;
                    }
                }

            }
        }

    }

    /**
     * Altera os valores da Table
     * @param itTable table
     * @param properties proprierties
     */
    public void replaceTables(Iterator<XWPFTable> itTable, Map<String, String> properties) {
        while (itTable.hasNext()) {
            XWPFTable table = itTable.next();
            extractLines(properties, table);
        }
    }

    /**
     * Altera os valores das linhas da tabela.
     * @param properties propriedades
     * @param table tabela
     */
    private void extractLines(Map<String, String> properties, XWPFTable table) {
        int rcount = table.getNumberOfRows();
        for (int j = 0; j < rcount; j++) {
            XWPFTableRow row = table.getRow(j);
            List<XWPFTableCell> cells = row.getTableCells();
            for (XWPFTableCell cell : cells) {
                replaceParagraphs(cell.getParagraphs(), properties);
            }
        }
    }
}
