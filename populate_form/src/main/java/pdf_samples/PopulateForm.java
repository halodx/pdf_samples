package pdf_samples;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;

public class PopulateForm {
    private static final String[] FIELD_NAMES = {
        "GIVEN_NAME",
        "FAMILY_NAME",
        "DOB",
        "FAVORITE_ANIMAL",
    };

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Missing expected filename parameter");
            System.exit(1);
        }

        String formTemplate = args[0];
        
        try (PDDocument pdfDocument = PDDocument.load(new File(formTemplate))) {
            // get the document catalog
            PDAcroForm acroForm = pdfDocument.getDocumentCatalog().getAcroForm();
            
            // as there might not be an AcroForm entry a null check is necessary
            if (acroForm == null) {
                System.out.println("The given PDF has no form in it");
                System.exit(1);
            }

            for (int i = 0; i < FIELD_NAMES.length; i++) {
                // Retrieve an individual field and set its value.
                PDTextField field = (PDTextField)acroForm.getField(FIELD_NAMES[i]);
                field.setValue("Filled in value " + i);
            }
            
            // Save and close the filled out form.
            pdfDocument.save("FormsFilled.pdf");
        }
    }
}
