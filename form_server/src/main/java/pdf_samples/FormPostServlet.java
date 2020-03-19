package pdf_samples;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;

/**
 * Java servlet that handles a sample form post
 */
public class FormPostServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String[] PARAMETERS = {
        "GIVEN_NAME",
        "FAMILY_NAME",
        "DOB",
        "FAVORITE_ANIMAL",
    };

    private final String formTemplate;
    private final String destinationDir;

    public FormPostServlet(String formTemplate, String destinationDir) {
        this.formTemplate = formTemplate;
        this.destinationDir = destinationDir;
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // Extract parameter values
        Map<String, String> parameters = new HashMap<>();
        for (String param : PARAMETERS) {
            String value = request.getParameter(param);
            if (value == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing required \"" + param + "\" parameter");
                return;
            }
            parameters.put(param, value);
        }

        // Spit out a filled in PDF file
        try (PDDocument pdfDocument = PDDocument.load(new File(formTemplate))) {
            // get the document catalog
            PDAcroForm acroForm = pdfDocument.getDocumentCatalog().getAcroForm();
            
            // as there might not be an AcroForm entry a null check is necessary
            if (acroForm == null) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "The given PDF has no form in it");
                return;
            }

            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                // Retrieve an individual field and set its value.
                PDTextField field = (PDTextField)acroForm.getField(entry.getKey());
                field.setValue(entry.getValue());
            }
            
            // Save and close the filled out form.
            String familyName = parameters.get("FAMILY_NAME");
            String givenName = parameters.get("GIVEN_NAME");

            String fileName = familyName.replace(' ', '_');
            if (!givenName.isBlank()) {
                fileName += "_" + givenName.replace(' ', '_');
            }
            fileName += ".pdf";

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/pdf");
            response.addHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            pdfDocument.save(response.getOutputStream());
        }
    }
}
