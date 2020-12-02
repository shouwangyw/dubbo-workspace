package org.apache.dubbo.remoting;

import org.apache.dubbo.common.extension.ExtensionLoader;

public class Transporter$Adaptive implements Transporter {
    public Client connect(org.apache.dubbo.common.URL arg0, ChannelHandler arg1) throws RemotingException {
        if (arg0 == null) throw new IllegalArgumentException("url == null");
        org.apache.dubbo.common.URL url = arg0;
        String extName = url.getParameter("client", url.getParameter("transporter", "netty"));
        if (extName == null)
            throw new IllegalStateException("Failed to get extension (org.apache.dubbo.remoting.Transporter) name from url (" + url.toString() + ") use keys([client, transporter])");
        Transporter extension = (Transporter) ExtensionLoader.getExtensionLoader(Transporter.class).getExtension(extName);
        return extension.connect(arg0, arg1);
    }

    public Server bind(org.apache.dubbo.common.URL arg0, ChannelHandler arg1) throws RemotingException {
        if (arg0 == null) throw new IllegalArgumentException("url == null");
        org.apache.dubbo.common.URL url = arg0;
        String extName = url.getParameter("server", url.getParameter("transporter", "netty"));
        if (extName == null)
            throw new IllegalStateException("Failed to get extension (org.apache.dubbo.remoting.Transporter) name from url (" + url.toString() + ") use keys([server, transporter])");
        Transporter extension = (Transporter) ExtensionLoader.getExtensionLoader(Transporter.class).getExtension(extName);
        return extension.bind(arg0, arg1);
    }
}