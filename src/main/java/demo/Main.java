package demo;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.rewrite.handler.RewriteHandler;
import org.eclipse.jetty.rewrite.handler.RewritePatternRule;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;

public class Main
{
    public static void main(String args[])
    {
        try
        {
            new Main().run();
        }
        catch (Throwable t)
        {
            t.printStackTrace(System.err);
        }
    }
    
    private void run() throws Exception
    {
        Server server = new Server(9090);
        
        RewriteHandler rewrite = new RewriteHandler()
        {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
            {
                System.err.println("RewriteHandler#handle(" + target + ")");
                super.handle(target, baseRequest, request, response);
            }
        };
        
        // External Rewrite Handler is the default behavior.
        // Set to false to use a ServletContext internal RewriteHandler
        boolean externalRewrite = true;
        
        RewritePatternRule rule = new RewritePatternRule();
        rule.setPattern("/old/*");
        rule.setReplacement("/demo/");
        rewrite.addRule(rule);
        
        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/demo");
        
        if (!externalRewrite)
        {
            context.insertHandler(rewrite);
        }
        
        context.addServlet(AccessibleServlet.class, "/dummy");
        context.addServlet(ForwardServlet.class, "/forward");
        context.addServlet(IncludeServlet.class, "/include");
        
        HandlerList handlers = new HandlerList();
        if (externalRewrite)
        {
            handlers.addHandler(rewrite);
        }
        handlers.addHandler(context);
        handlers.addHandler(new DefaultHandler());
        
        server.setHandler(handlers);
        
        server.start();
        server.join();
    }
}
