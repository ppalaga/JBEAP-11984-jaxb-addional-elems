package org.wildfly.issues.jbeap11984;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class HelloTest {

    @ArquillianResource
    private URL deploymentUrl;

    @Deployment
    public static WebArchive createDeployment() {
        System.out.println(" path "+ new File("src/main/webapp/WEB-INF/jboss-deployment-structure.xml").getAbsoluteFile());
        return ShrinkWrap.create(WebArchive.class, "hello.war") //
                .addPackage(Hello.class.getPackage()) //
                .addAsWebInfResource(new File("src/main/webapp/WEB-INF/jboss-deployment-structure.xml").getAbsoluteFile()) //
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml"); //
    }


    @Test
    @RunAsClient
    public void helloValid() throws IOException, URISyntaxException {
        assertResponse("/request-valid.xml", HttpURLConnection.HTTP_OK);
    }

    @Test
    @RunAsClient
    public void helloBadNumber() throws IOException, URISyntaxException {
        assertResponse("/request-bad-number.xml", HttpURLConnection.HTTP_INTERNAL_ERROR);
    }

    @Test
    @RunAsClient
    public void helloBadNumberAndUnexpectedElement() throws IOException, URISyntaxException {
        assertResponse("/request-bad-number-and-unexpected-element.xml", HttpURLConnection.HTTP_INTERNAL_ERROR);
    }

    @Test
    @RunAsClient
    public void helloUnexpectedElement() throws IOException, URISyntaxException {
        assertResponse("/request-unexpected-element.xml", HttpURLConnection.HTTP_OK);
    }

    private byte[] readBytes(InputStream in) throws IOException, URISyntaxException {
        ByteArrayOutputStream reqBaos = new ByteArrayOutputStream(1024);
        try {
            byte[] buff = new byte[1024];
            int len = 0;
            while ((len = in.read(buff)) >= 0) {
                reqBaos.write(buff, 0, len);
            }
        } finally {
            in.close();
        }
        return reqBaos.toByteArray();
    }
    private void assertResponse(String request, int expectedCode) throws IOException, URISyntaxException {
        byte[] reqBytes = readBytes(getClass().getResourceAsStream(request));
        URI uri = new URL(deploymentUrl, "Hello").toURI();
        System.out.println("uri = "+ uri);
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            HttpPost post = new HttpPost(uri);
            post.setEntity(new ByteArrayEntity(reqBytes, ContentType.create(ContentType.TEXT_XML.getMimeType(), Charset.forName("utf-8"))));
            HttpResponse response = httpClient.execute(post);

            Assert.assertEquals("Code for "+ request, expectedCode, response.getStatusLine().getStatusCode());

            String actual = EntityUtils.toString(response.getEntity());
            System.out.println("actual for "+ request +"\n"+ actual);
            String expected = new String(readBytes(getClass().getResourceAsStream(request.replace(".xml", ".expected.xml"))), "utf-8");
            Assert.assertEquals("Reponse for "+ request, expected, actual);
        }


    }

}
