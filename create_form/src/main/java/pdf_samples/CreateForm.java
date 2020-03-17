package pdf_samples;

import java.io.IOException;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceCharacteristicsDictionary;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;

/**
 * Generates an ugly PDF file with some sample form fields.
 */
public class CreateForm {
    private static final float LABEL_WIDTH = 100f;
    private static final float LABEL_RIGHT_PAD = 5f;
    private static final float LABEL_VERTICAL_PAD = 4f;
    private static final float FIELD_HEIGHT = 20f;
    private static final float FIELD_PADDING = 6f;

    private static final String[] FIELD_LABELS = {
        "Given name:",
        "Family Name:",
        "Date of birth:",
        "Favorite animal:",
    };

    private static final String[] FIELD_NAMES = {
        "GIVEN_NAME",
        "FAMILY_NAME",
        "DOB",
        "FAVORITE_ANIMAL",
    };

    public static void main(String[] args) throws IOException {
        // Create a new document
        try (PDDocument document = new PDDocument()) {
            populateDocument(document);
        }
    }
    
    private static void populateDocument(PDDocument document) throws IOException {
        // Add a single page to the PDF
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);
        
        // Add a new form and add that to the document
        PDAcroForm acroForm = new PDAcroForm(document);
        document.getDocumentCatalog().setAcroForm(acroForm);
        
        // Adobe Acrobat uses Helvetica as a default font and
        // stores that under the name '/Helv' in the resources dictionary
        PDFont font = PDType1Font.HELVETICA;
        PDResources resources = new PDResources();
        resources.put(COSName.getPDFName("Helv"), font);

        // Add and set the resources and default appearance at the form level
        acroForm.setDefaultResources(resources);
        
        // Acrobat sets the font size on the form level to be
        // auto sized as default. This is done by setting the font size to '0'
        String defaultAppearanceString = "/Helv 0 Tf 0 g";
        acroForm.setDefaultAppearance(defaultAppearanceString);

        // Add some fields to the form
        for (int i = 0; i < FIELD_NAMES.length; i++) {
            float fieldY = 810 - (i * (FIELD_HEIGHT + FIELD_PADDING));
            addFormTextField(page, acroForm, FIELD_NAMES[i], 150, fieldY);
        }

        // Render some labels next to the fields
        try (PDPageContentStream cs = new PDPageContentStream(document, page)) {
            for (int i = 0; i < FIELD_LABELS.length; i++) {
                float labelY = 810 - (i * (FIELD_HEIGHT + FIELD_PADDING));
                addLabel(cs, FIELD_LABELS[i], 50, labelY);
            }
        }

        document.save("SampleForm.pdf");
    }

    private static void addFormTextField(PDPage page, PDAcroForm acroForm,
            String partialName, float x, float y) throws IOException {
        // Add a form field to the form.
        PDTextField textField = new PDTextField(acroForm);
        textField.setPartialName(partialName);
        textField.setMultiline(false);

        // Size 12 Helvetica typeface red green blue
        String defaultAppearanceString = "/Helv 12 Tf 0 0 0 rg";
        textField.setDefaultAppearance(defaultAppearanceString);
        
        // Add the field to the form
        acroForm.getFields().add(textField);

        // Specify the widget annotation associated with the field
        PDAnnotationWidget widget = textField.getWidgets().get(0);
        PDRectangle rect = new PDRectangle(x, y, 200, FIELD_HEIGHT);
        widget.setRectangle(rect);
        widget.setPage(page);

        // Set dark grey border and light grey background
        PDAppearanceCharacteristicsDictionary fieldAppearance
                = new PDAppearanceCharacteristicsDictionary(new COSDictionary());
        fieldAppearance.setBorderColour(new PDColor(new float[]{.5f,.5f,.5f}, PDDeviceRGB.INSTANCE));
        fieldAppearance.setBackground(new PDColor(new float[]{.95f,.95f,.95f}, PDDeviceRGB.INSTANCE));
        widget.setAppearanceCharacteristics(fieldAppearance);

        // Make sure the widget annotation is visible on screen and paper
        widget.setPrinted(true);
        
        // Add the widget annotation to the page
        page.getAnnotations().add(widget);
        
        // Set a sample field value
        textField.setValue(partialName);
    }

    private static void addLabel(PDPageContentStream cs, String text, float x, float y) throws IOException {
        PDFont font = PDType1Font.HELVETICA;
        float fontSize = 12;

        float textWidth = (font.getStringWidth(text) / 1000.0f) * fontSize;
        float rightAlignOffset = LABEL_WIDTH - textWidth;

        float calcX = x + rightAlignOffset - LABEL_RIGHT_PAD;
        float calcY = y + LABEL_VERTICAL_PAD;
        
        cs.beginText();
        cs.setFont(font, fontSize);
        cs.newLineAtOffset(calcX, calcY);
        cs.showText(text);
        cs.endText();
    }
}
