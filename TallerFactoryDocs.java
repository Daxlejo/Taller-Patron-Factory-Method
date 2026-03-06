import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.BorderFactory;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Font;
import java.awt.Color;
import java.awt.Dimension;

public class TallerFactoryDocs {

    static List<Document> documents = new ArrayList<>();

    public static void main(String[] args) {

        JFrame frame = new JFrame("GlobalDocs Processor");
        frame.setSize(450,320);
        frame.setLayout(new FlowLayout());
        frame.getContentPane().setBackground(new Color(245,247,250));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Font labelFont = new Font("Segoe UI", Font.BOLD, 13);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 13);
        Font buttonFont = new Font("Segoe UI", Font.BOLD, 13);

        JPanel panel = new JPanel(new GridLayout(5,2,8,8));
        panel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        panel.setBackground(new Color(245,247,250));

        JTextField nameField = new JTextField();
        nameField.setFont(fieldFont);

        String[] types = {"factura","contrato","reporte","certificado","declaracion"};
        JComboBox<String> typeBox = new JComboBox<>(types);
        typeBox.setFont(fieldFont);

        String[] formats = {"pdf","doc","docx","xlsx","csv","txt","md"};
        JComboBox<String> formatBox = new JComboBox<>(formats);
        formatBox.setFont(fieldFont);

        JTextField countryField = new JTextField();
        countryField.setFont(fieldFont);

        JLabel nameLabel = new JLabel("Nombre archivo");
        JLabel typeLabel = new JLabel("Tipo");
        JLabel formatLabel = new JLabel("Formato");
        JLabel countryLabel = new JLabel("Pais");

        nameLabel.setFont(labelFont);
        typeLabel.setFont(labelFont);
        formatLabel.setFont(labelFont);
        countryLabel.setFont(labelFont);

        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(typeLabel);
        panel.add(typeBox);
        panel.add(formatLabel);
        panel.add(formatBox);
        panel.add(countryLabel);
        panel.add(countryField);

        frame.add(panel);

        JButton addButton = new JButton("Agregar al lote");
        JButton processButton = new JButton("Procesar lote");

        addButton.setFont(buttonFont);
        processButton.setFont(buttonFont);

        addButton.setBackground(new Color(52,152,219));
        processButton.setBackground(new Color(39,174,96));

        addButton.setForeground(Color.WHITE);
        processButton.setForeground(Color.WHITE);

        addButton.setFocusPainted(false);
        processButton.setFocusPainted(false);

        addButton.setPreferredSize(new Dimension(160,35));
        processButton.setPreferredSize(new Dimension(160,35));

        frame.add(addButton);
        frame.add(processButton);

        addButton.addActionListener(e -> {

            String name = nameField.getText();
            String type = typeBox.getSelectedItem().toString();
            String format = formatBox.getSelectedItem().toString();
            String country = countryField.getText();

            Document doc = new Document(name,type,format,country);

            documents.add(doc);

            JOptionPane.showMessageDialog(frame,"Documento agregado al lote");

            nameField.setText("");
            countryField.setText("");

        });

        processButton.addActionListener(e -> {

            processBatch(documents);

        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }

    public static void processBatch(List<Document> documents){

        List<String> failedFiles = new ArrayList<>();

        for(Document doc : documents){

            try{

                DocumentProcessor processor =
                        DocumentFactory.createProcessor(doc.type);

                processor.validate(doc);
                processor.process(doc);

            }catch(Exception e){

                failedFiles.add(doc.name + " -> " + e.getMessage());

            }

        }

        showErrors(failedFiles);

    }

    public static void showErrors(List<String> failedFiles){

        if(failedFiles.isEmpty()){

            JOptionPane.showMessageDialog(null,
                    "Todos los documentos fueron procesados correctamente");

            return;
        }

        String message = "Archivos con error:\n\n";

        for(String error : failedFiles){
            message += error + "\n";
        }

        JOptionPane.showMessageDialog(null,message);

    }

}

class Document{

    String name;
    String type;
    String format;
    String country;

    public Document(String name,String type,String format,String country){
        this.name=name;
        this.type=type;
        this.format=format;
        this.country=country;
    }

}

interface DocumentProcessor{

    void validate(Document doc);
    void process(Document doc);

}

class InvoiceProcessor implements DocumentProcessor{

    public void validate(Document doc){

        if(!doc.format.equals("pdf")){
            throw new RuntimeException("Factura debe ser pdf");
        }

    }

    public void process(Document doc){
        System.out.println("Procesando factura "+doc.name);
    }

}

class ContractProcessor implements DocumentProcessor{

    public void validate(Document doc){

        if(!doc.format.equals("doc") && !doc.format.equals("docx")){
            throw new RuntimeException("Contrato debe ser doc o docx");
        }

    }

    public void process(Document doc){
        System.out.println("Procesando contrato "+doc.name);
    }

}

class FinancialReportProcessor implements DocumentProcessor{

    public void validate(Document doc){

        if(!doc.format.equals("xlsx") && !doc.format.equals("csv")){
            throw new RuntimeException("Reporte debe ser xlsx o csv");
        }

    }

    public void process(Document doc){
        System.out.println("Procesando reporte "+doc.name);
    }

}

class CertificateProcessor implements DocumentProcessor{

    public void validate(Document doc){

        if(!doc.format.equals("txt")){
            throw new RuntimeException("Certificado debe ser txt");
        }

    }

    public void process(Document doc){
        System.out.println("Procesando certificado "+doc.name);
    }

}

class TaxDeclarationProcessor implements DocumentProcessor{

    public void validate(Document doc){

        if(!doc.format.equals("md")){
            throw new RuntimeException("Declaracion debe ser md");
        }

    }

    public void process(Document doc){
        System.out.println("Procesando declaracion "+doc.name);
    }

}

class DocumentFactory{

    public static DocumentProcessor createProcessor(String type){

        if(type.equalsIgnoreCase("factura")){
            return new InvoiceProcessor();
        }

        if(type.equalsIgnoreCase("contrato")){
            return new ContractProcessor();
        }

        if(type.equalsIgnoreCase("reporte")){
            return new FinancialReportProcessor();
        }

        if(type.equalsIgnoreCase("certificado")){
            return new CertificateProcessor();
        }

        if(type.equalsIgnoreCase("declaracion")){
            return new TaxDeclarationProcessor();
        }

        throw new RuntimeException("Tipo de documento no soportado");

    }

}