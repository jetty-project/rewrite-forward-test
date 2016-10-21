package demo;

import org.eclipse.jetty.rewrite.handler.RewriteHandler;
import org.eclipse.jetty.rewrite.handler.RewritePatternRule;
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
        
        RewriteHandler rewrite = new RewriteHandler();
        RewritePatternRule rule = new RewritePatternRule();
        rule.setPattern("/old/*");
        rule.setReplacement("/demo/");
        rewrite.addRule(rule);
        
        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/demo");
        
        context.addServlet(AccessibleServlet.class, "/dummy");
        context.addServlet(TargetForwardServlet.class, "/forward");
        
        HandlerList handlers = new HandlerList();
        handlers.addHandler(rewrite);
        handlers.addHandler(context);
        handlers.addHandler(new DefaultHandler());
        
        server.setHandler(handlers);
        
        server.start();
        server.join();
    }
}
