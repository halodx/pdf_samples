package pdf_samples;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class FormServer {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Expect input PDF form template and destination directory as parameters");
            System.exit(-1);
        }

        String formTemplate = args[0];
        String destinationDir = args[1];

        Server server = new Server(80);

        ServletContextHandler context = new ServletContextHandler();
        context.setAllowNullPathInfo(true);
        context.addServlet(new ServletHolder(new FormPostServlet(formTemplate, destinationDir)), "/form");
        server.setHandler(context);

        server.start();
        server.join();
    }
}
